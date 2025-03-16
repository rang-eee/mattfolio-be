package com.colon.mattfolio.common.history;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.colon.mattfolio.util.BeanUtil;
import com.colon.mattfolio.util.DtoUtil;
import com.colon.mattfolio.util.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 테이블 변경 이력을 저장하는 서비스 클래스.
 * <p>
 * 특정 테이블에 대한 INSERT, UPDATE, DELETE 기록을 별도의 이력 테이블에 적재한다.
 */
@Slf4j
@Service
@AllArgsConstructor
public class HistoryAppendService {

    private final HistoryTransactionManager historyTransactionManager;

    // 이력 테이블에 INSERT 쿼리를 생성하는 기본 포맷
    public static final String INSERT_QUERY = "/* %s 테이블 이력 적재 */ INSERT INTO %s (%s) VALUES (%s)";

    /**
     * 이력 저장 유형 ENUM
     */
    public enum HistoryType {
        INSERT, // 데이터 삽입
        UPDATE, // 데이터 수정
        DELETE // 데이터 삭제
    }

    /**
     * TQMS 데이터베이스용 이력 저장
     */
    public void insertHistoryForTqms(String tableName, HistoryType historyType, Long historyCreatedId, String historyContent, Object dto) {
        historyTransactionManager.registerHistoryOperation(() -> saveHistoryToDB(tableName, historyType, historyCreatedId, historyContent, dto, "dataSourceForTqmsDb"));
    }

    /**
     * 특정 테이블의 이력 데이터를 기록하는 메소드.
     *
     * @param tableName 원본 테이블명
     * @param historyType 이력 유형 (INSERT, UPDATE, DELETE)
     * @param historyCreatedId 이력을 생성한 사용자 ID (nullable)
     * @param historyContent 이력 내용 (nullable)
     * @param dto DTO 객체 (이력에 저장될 데이터)
     * @param dbSchemaName 사용할 데이터소스 스키마명 (ex. dataSourceForTqmsDb, dataSourceForIntfDb)
     */
    private void saveHistoryToDB(String tableName, HistoryType historyType, Long historyCreatedId, String historyContent, Object dto, String dbSchemaName) {

        if (dbSchemaName == null || dbSchemaName.trim()
            .isEmpty() || tableName == null || tableName.trim()
                .isEmpty() || dto == null) {
            log.warn("[이력 저장] 유효하지 않은 데이터 - 스키마: {}, 테이블: {}, DTO: {}", dbSchemaName, tableName, dto);
            return;
        }

        // 이력 테이블명 (기본적으로 "_history" 접미어 추가)
        String histTableName = tableName + "_history";

        // DTO를 Map<String, Object> 형태로 변환
        Map<String, Object> dtoMap = DtoUtil.convertDtoToMap(dto);

        // 기본적인 이력 컬럼 추가
        dtoMap.put("history_type", historyType.name());
        dtoMap.put("history_content", historyContent);
        dtoMap.put("history_create_id", historyCreatedId);
        dtoMap.put("history_create_at", LocalDateTime.now());

        // 컬럼명을 스네이크 케이스로 변환하여 사용
        List<String> columnNames = dtoMap.keySet()
            .stream()
            .map(StringUtil::camelToSnake)
            .collect(Collectors.toList());

        String columns = String.join(", ", columnNames);

        // 컬럼에 대한 바인딩(Placeholder) 문자열 생성 → ?, ?, ?, ...
        String valuesPlaceholder = columnNames.stream()
            .map(c -> "?")
            .collect(Collectors.joining(", "));

        // SQL 생성
        String sql = String.format(INSERT_QUERY, histTableName, histTableName, columns, valuesPlaceholder);

        log.debug("[이력 저장] SQL 실행 준비 - 테이블: {}", histTableName);
        log.debug("[이력 저장] SQL Query: {}", sql);
        log.debug("[이력 저장] 바인딩할 데이터: {}", dtoMap);

        // 해당 스키마의 DataSource 객체 가져오기
        DataSource dataSourceForDb = BeanUtil.getBean(dbSchemaName, DataSource.class);

        // SQL 실행 (PreparedStatement 사용)
        try (Connection connection = dataSourceForDb.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            int index = 1;
            for (Object value : dtoMap.values()) {
                statement.setObject(index++, value);
            }

            int affectedRows = statement.executeUpdate();
            log.info("[이력 저장 성공] 테이블: {}, 반영된 행 수: {}", histTableName, affectedRows);

        } catch (SQLException e) {
            log.error("[이력 저장 실패] 테이블: {}, 에러: {}", histTableName, e.getMessage());
        }
    }

    /**
     * 특정 테이블의 다중 이력 데이터를 Batch Insert하는 메소드.
     *
     * @param tableName 원본 테이블명
     * @param historyType 이력 유형 (INSERT, UPDATE, DELETE)
     * @param historyCreatedId 이력을 생성한 사용자 ID (nullable)
     * @param historyContent 이력 내용 (nullable)
     * @param dtoList DTO 객체 리스트 (이력에 저장될 데이터)
     * @param dbSchemaName 사용할 데이터소스 스키마명 (ex. dataSourceForTqmsDb, dataSourceForIntfDb)
     */
    private void saveBatchHistoryToDB(String tableName, HistoryType historyType, Long historyCreatedId, String historyContent, List<?> dtoList, String dbSchemaName) {
        // 데이터소스 스키마명 검증
        if (dbSchemaName == null || dbSchemaName.trim()
            .isEmpty() || dtoList == null || dtoList.isEmpty()) {
            return;
        }

        // 해당 스키마의 DataSource 객체 가져오기
        DataSource dataSourceForDb = BeanUtil.getBean(dbSchemaName, DataSource.class);

        // 이력 테이블명 설정
        String histTableName = tableName + "_history";

        // 첫 번째 DTO 객체를 이용해 컬럼명 리스트 추출
        List<String> columnNames = DtoUtil.getFieldNames(dtoList.get(0));

        // 이력 컬럼 추가
        columnNames.add("history_type");
        columnNames.add("history_content");
        columnNames.add("history_create_id");
        columnNames.add("history_create_at");

        // SQL 생성할 때만 컬럼명을 스네이크 케이스로 변환
        String sqlColumns = columnNames.stream()
            .map(StringUtil::camelToSnake)
            .collect(Collectors.joining(", "));

        // 바인딩(Placeholder) 문자열 생성 → (?, ?, ?, ...)
        String bindString = columnNames.stream()
            .map(col -> "?")
            .collect(Collectors.joining(", "));

        // 최종 SQL 쿼리 생성
        String query = String.format(INSERT_QUERY, histTableName, histTableName, sqlColumns, bindString);

        // 로그 출력
        log.debug("[Batch 이력 저장] SQL 실행 준비 - 테이블: {}", histTableName);
        log.debug("[Batch 이력 저장] SQL Query: {}", query);

        // Batch Insert 실행
        try (Connection connection = dataSourceForDb.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            for (Object dto : dtoList) {
                // DTO를 Map<String, Object> 형태로 변환
                Map<String, Object> dtoMap = DtoUtil.convertDtoToMap(dto);

                // 기본적인 이력 컬럼 추가
                dtoMap.put("history_type", historyType.name());
                dtoMap.put("history_content", historyContent);
                dtoMap.put("history_create_id", historyCreatedId);
                dtoMap.put("history_create_at", LocalDateTime.now());

                int index = 1;
                for (String column : columnNames) {
                    statement.setObject(index++, dtoMap.get(column));
                }

                statement.addBatch(); // Batch에 추가
            }

            int[] batchResults = statement.executeBatch(); // Batch 실행
            int insertedCount = Arrays.stream(batchResults)
                .sum(); // 삽입된 행 개수 반환
            log.info("[Batch 이력 저장 성공] 테이블: {}, 삽입된 행 수: {}", histTableName, insertedCount);

        } catch (SQLException e1) {
            log.error("[Batch 이력 저장 실패] 테이블: {}, 에러: {}", histTableName, e1.getMessage());
        }
    }

    /**
     * TQMS 데이터베이스용 다중 이력 저장 (Batch Insert)
     */
    public void insertBatchHistoryForTqms(String tableName, HistoryType historyType, Long historyCreatedId, String historyContent, List<?> dtoList) {
        historyTransactionManager.registerHistoryOperation(() -> saveBatchHistoryToDB(tableName, historyType, historyCreatedId, historyContent, dtoList, "dataSourceForTqmsDb"));
    }

    /**
     * 인터페이스 데이터베이스용 이력 저장
     */
    public void insertHistoryForIntf(String tableName, HistoryType historyType, Long historyCreatedId, String historyContent, Object dto) {
        historyTransactionManager.registerHistoryOperation(() -> saveHistoryToDB(tableName, historyType, historyCreatedId, historyContent, dto, "dataSourceForIntfDb"));
    }

    /**
     * TQMS 데이터베이스용 기본 이력 저장 (historyCreatedId, historyContent 없이 저장)
     */
    public void insertHistoryForTqms(String tableName, HistoryType historyType, Object dto) {
        insertHistoryForTqms(tableName, historyType, null, null, dto);
    }

    /**
     * TQMS 데이터베이스용 이력 저장 (historyContent만 포함)
     */
    public void insertHistoryForTqms(String tableName, HistoryType historyType, String historyContent, Object dto) {
        insertHistoryForTqms(tableName, historyType, null, historyContent, dto);
    }

    /**
     * 인터페이스 데이터베이스용 기본 이력 저장 (historyCreatedId, historyContent 없이 저장)
     */
    public void insertHistoryForIntf(String tableName, HistoryType historyType, Object dto) {
        insertHistoryForIntf(tableName, historyType, null, null, dto);
    }

    /**
     * 인터페이스 데이터베이스용 이력 저장 (historyContent만 포함)
     */
    public void insertHistoryForIntf(String tableName, HistoryType historyType, String historyContent, Object dto) {
        insertHistoryForIntf(tableName, historyType, null, historyContent, dto);
    }

}
