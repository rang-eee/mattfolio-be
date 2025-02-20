package com.colon.mattfolio.service;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.colon.mattfolio.common.base.PagingService;
import com.colon.mattfolio.config.Message;
import com.colon.mattfolio.exception.ExampleException;
import com.colon.mattfolio.exception.ExcelException;
import com.colon.mattfolio.mapper.ExampleMapper;
import com.colon.mattfolio.model.common.PagingResponseDto;
import com.colon.mattfolio.model.common.ResourceDto;
import com.colon.mattfolio.model.example.ExampleDetailResponseDto;
import com.colon.mattfolio.model.example.ExampleDownloadRequestDto;
import com.colon.mattfolio.model.example.ExampleExcelUploadParseDto;
import com.colon.mattfolio.model.example.ExampleExcelUploadRequestDto;
import com.colon.mattfolio.model.example.ExampleListRequestDto;
import com.colon.mattfolio.model.example.ExampleListResponseDto;
import com.colon.mattfolio.model.example.ExampleModifyRequestDto;
import com.colon.mattfolio.model.example.ExampleRegisterRequestDto;
import com.colon.mattfolio.service.HistoryAppendService.HistoryType;
import com.colon.mattfolio.util.FileUtils;
import com.colon.mattfolio.util.ParseXlsxUtil;
import com.colon.mattfolio.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ExampleService extends PagingService {

    private final ExampleMapper exampleMapper; // tqms

    private final HistoryAppendService historyAppendService;

    /**
     * 주어진 검색 조건(request) 및 페이징을 기반으로 데이터를 조회합니다.
     *
     * @param request 검색 조건을 포함하는 요청 객체
     * @return 검색 조건에 부합하는 데이터 목록
     * @throws ExampleException 유효하지 않은 검색 타입인 경우
     */
    public PagingResponseDto<ExampleListResponseDto> findByCriteriaPaging(ExampleListRequestDto request) {

        // 유효하지 않은 요청 파라미터 검사 및 예외 처리
        if (request.getSearchType() != null && isInvalidSearchType(request.getSearchType())) {
            throw new ExampleException(ExampleException.Reason.INVALID_REQUEST);
        }

        // List<ExampleListResponseDto> resultList =
        // exampleMapper.selectByCriteriaPaging(request);

        PagingResponseDto<ExampleListResponseDto> pagingResult = findData(exampleMapper, "selectByCriteriaPaging", request);

        return pagingResult;
    }

    /**
     * 주어진 검색 조건(request)을 기반으로 데이터를 조회합니다.
     *
     * @param request 검색 조건을 포함하는 요청 객체
     * @return 검색 조건에 부합하는 데이터 목록
     * @throws ExampleException 유효하지 않은 검색 타입인 경우
     */
    public List<ExampleDetailResponseDto> findByCriteria(ExampleListRequestDto request) {

        // 유효하지 않은 요청 파라미터 검사 및 예외 처리
        if (request.getSearchType() != null && isInvalidSearchType(request.getSearchType())) {
            throw new ExampleException(ExampleException.Reason.INVALID_REQUEST);
        }

        return exampleMapper.selectByCriteria(request);
    }

    /**
     * 주어진 사용자 ID(userId)를 기반으로 데이터를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 조회된 사용자 정보
     */
    public ExampleDetailResponseDto findById(String userId) {
        return exampleMapper.selectById(userId);
    }

    /**
     * 새로운 데이터를 생성합니다.
     *
     * @param request 생성할 데이터를 포함하는 요청 객체
     * @return 생성된 데이터의 상세 정보
     * @throws ExampleException 유효하지 않은 요청 파라미터 또는 중복된 ID인 경우
     */
    @Transactional
    public ExampleDetailResponseDto create(ExampleRegisterRequestDto request) {

        // 유효하지 않은 요청 파라미터 검사 및 예외 처리
        if (StringUtils.isNullEmpty(request.getUserId()) || StringUtils.isNullEmpty(request.getUserName()) || request.getAge() == null || request.getAge() < 0) {
            throw new ExampleException(ExampleException.Reason.INVALID_REQUEST);
        }

        // 중복된 아이디 존재할 시에 예외 처리
        if (exampleMapper.existsUserId(request.getUserId())) {
            log.error("{} : {}", Message.getMessage("example.duplicate.id"), request.getUserId());
            throw new ExampleException(ExampleException.Reason.DUPLICATE_ID);
        }

        // 데이터를 생성
        exampleMapper.createExample(request);

        // 이력 적재
        ExampleDetailResponseDto createdExample = exampleMapper.selectById(request.getUserId()); // 삽입 후, ID로 재조회하여 최종 상태 반환
        historyAppendService.insertHistoryForTqms("a_test_table_q", HistoryType.INSERT, "[삽입] 예제 삽입", createdExample);

        return createdExample;
    }

    /**
     * 데이터를 수정합니다.
     *
     * @param request 수정할 데이터를 포함하는 요청 객체
     * @return 수정된 데이터의 상세 정보
     * @throws ExampleException 유효하지 않은 요청 파라미터 또는 대상 데이터가 없는 경우
     */
    @Transactional
    public ExampleDetailResponseDto update(ExampleModifyRequestDto request) {

        // 유효하지 않은 요청 파라미터 검사 및 예외 처리
        if (StringUtils.isNullEmpty(request.getUserName()) || request.getUpdateId() == null || request.getAge() == null || request.getAge() < 0) {
            throw new ExampleException(ExampleException.Reason.INVALID_REQUEST);
        }

        // 수정 대상 확인 (존재 여부 검사)
        ExampleDetailResponseDto existingExample = exampleMapper.selectById(request.getUserId());
        if (existingExample == null) {
            throw new ExampleException(ExampleException.Reason.INVALID_ID); // 대상이 없을 때 예외 발생
        }

        // 데이터를 수정
        exampleMapper.modifyExample(request);

        // 이력 적재 - 수정 데이터 적재
        historyAppendService.insertHistoryForTqms("a_test_table_q", HistoryType.UPDATE, "[수정] 예제 수정", existingExample);

        // 업데이트 후 최종 상태 조회 및 반환
        ExampleDetailResponseDto updatedExample = exampleMapper.selectById(request.getUserId());
        return updatedExample;
    }

    /**
     * 주어진 사용자 ID(userId)에 해당하는 데이터를 삭제합니다.
     *
     * @param userId 삭제할 사용자 ID
     * @return 삭제 성공 여부
     * @throws ExampleException 대상 데이터가 없는 경우
     */
    @Transactional
    public boolean delete(String userId) {

        // 삭제 대상 확인 (존재 여부 검사)
        ExampleDetailResponseDto existingExample = exampleMapper.selectById(userId);
        if (existingExample == null) {
            throw new ExampleException(ExampleException.Reason.INVALID_ID); // 대상이 없을 때 예외 발생
        }

        // 데이터를 삭제
        int affectedRows = exampleMapper.removeExample(userId);

        // 이력 적재 - 삭제 전 데이터 적재
        historyAppendService.insertHistoryForTqms("a_test_table_q", HistoryType.DELETE, "[삭제] 예제 삭제", existingExample);

        return affectedRows > 0;
    }

    /**
     * 주어진 검색 타입(searchType)이 유효한지 확인합니다.
     *
     * @param searchType 검색 타입
     * @return 검색 타입이 유효하면 true, 그렇지 않으면 false
     */
    private boolean isInvalidSearchType(String searchType) {
        // 유효한 검색 타입 리스트와 비교하여 확인
        return !Arrays.asList("userId", "userName")
            .contains(searchType);
    }

    /**
     * 엑셀 업로드 예제
     *
     * 엑셀을 업로드하여 데이터를 검증하고 저장하는 테스트를 수행합니다.<br/>
     *
     * @param request 엑셀 업로드 요청 정보
     * @param file 업로드된 엑셀 파일
     */
    public void uploadExcel(ExampleExcelUploadRequestDto request, MultipartFile file) {
        log.info("엑셀 업로드 테스트 : uploadExcel()");

        // 파일이 비어 있는지 확인
        if (file == null || file.isEmpty()) {
            throw new ExcelException(ExcelException.Reason.EMPTY_FILE);
        }

        // 파일 확장자 검증
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!"xls".equalsIgnoreCase(extension) && !"xlsx".equalsIgnoreCase(extension)) {
            throw new ExcelException(ExcelException.Reason.NOT_EXCEL_EXTENSION);
        }

        List<ExampleExcelUploadParseDto> parsedList = ParseXlsxUtil.parseToList(file, ExampleExcelUploadParseDto.class);

        parsedList.stream()
            .forEach(data -> log.info("parsed {} {} {} {} {} {} {}", data.getUserId(), data.getAccountEmail(), data.getAccountName(), data.getAge(), data.getHeight(), data.getRegistDate(), data.getRegistDatetime()));

        log.info("엑셀 파일 검증 완료: {}", file.getOriginalFilename());
    }

    /**
     * 엑셀 양식 다운로드 예제
     *
     * 엑셀 양식 파일을 다운로드하는 테스트를 수행합니다.<br/>
     *
     * @param filePath 다운로드할 파일 경로
     * @return ResourceDto 엑셀 양식 파일 리소스
     */
    public ResourceDto downloadExcelTemplate(ExampleDownloadRequestDto request) {
        log.info("엑셀 양식 다운로드 경로 : {}", request);

        try {
            String filePath = request.getFilePath();
            Resource resource = new ClassPathResource(filePath);

            // 파일 확장자에 따른 MediaType 설정
            MediaType mediaType = FileUtils.determineMediaType(filePath);

            // 다운로드 명 설정
            String downloadName = request.getDownloadName();
            String downloadFileName = downloadName == null ? StringUtils.getFilename(filePath) : downloadName;
            String downloadFileExtension = StringUtils.getFilenameExtension(filePath)
                .toLowerCase();

            Boolean preview = request.getPreview();

            return ResourceDto.builder()
                .resource(resource)
                .downloadFileName(downloadFileName + "." + downloadFileExtension)
                .mediaType(mediaType)
                .preview(preview == null ? false : (preview == true ? FileUtils.isPreviewable(mediaType) : false))
                .build();
        } catch (Exception e) {
            throw e;
        }
    }

}
