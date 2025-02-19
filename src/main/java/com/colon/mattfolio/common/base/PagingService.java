package com.colon.mattfolio.common.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.mybatis.spring.MyBatisSystemException;

import com.colon.mattfolio.model.common.PagingRequestDto;
import com.colon.mattfolio.model.common.PagingResponseDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 공통 페이징 처리 서비스 클래스.
 * 
 * <p>
 * 컨트롤러에서 사용되는 페이징 응답 처리를 위한 공통 메서드를 제공합니다. 이 클래스는 요청 객체를 기반으로 매퍼의 메서드를 호출하여 페이징 처리 결과를 {@link PagingResponseDto} 형태로 반환합니다.
 * </p>
 */
@Slf4j
public class PagingService {

    /**
     * 공통 페이징 처리 메서드.
     *
     * <p>
     * Mapper 객체와 메서드명을 기반으로 요청 객체를 전달하여 페이징 데이터를 조회하고, {@link PagingResponseDto} 형태로 응답을 구성합니다.
     * </p>
     *
     * @param <T> 응답 데이터 타입 (예: DTO 클래스)
     * @param <R> 요청 데이터 타입 (예: 페이징 요청 DTO)
     * @param mapper Mapper 객체 (MyBatis Mapper 또는 기타 데이터 접근 객체)
     * @param methodName 실행할 데이터 조회 메서드명 (예: "selectByCriteriaPaging")
     * @param request 페이징 요청 객체
     * @return {@link PagingResponseDto} 페이징 처리 결과 객체
     * @throws RuntimeException 메서드 실행 또는 데이터 변환 중 오류 발생 시
     */
    public static <T, R> PagingResponseDto<T> findData(Object mapper, String methodName, R request) {
        try {
            // 건수 조회 메서드 실행
            String countMethodName = methodName + "Count";
            Method countMethod = mapper.getClass()
                .getMethod(countMethodName, request.getClass());
            Integer totalCount = (Integer) countMethod.invoke(mapper, request);

            // 데이터 조회 메서드 실행
            Method dataMethod = mapper.getClass()
                .getMethod(methodName, request.getClass());
            @SuppressWarnings("unchecked")
            List<T> results = (List<T>) dataMethod.invoke(mapper, request);

            // 요청 객체에서 페이지 번호와 페이지 크기 추출
            int page = ((PagingRequestDto) request).getPage(); // 페이지 번호
            int limit = ((PagingRequestDto) request).getLimit(); // 페이지 크기

            // 순번 계산 및 할당
            int startNumber = totalCount - (page - 1) * limit;
            for (T dto : results) {
                // "number" 필드가 존재하는지 확인
                boolean hasNumberField = false;
                for (Field field : dto.getClass()
                    .getDeclaredFields()) {
                    if (field.getName()
                        .equals("number")) {
                        hasNumberField = true;
                        break;
                    }
                }

                // "number" 필드가 없으면 건너뛰기
                if (!hasNumberField) {
                    continue;
                }

                try {
                    // "number" 필드에 값 설정
                    Field numberField = dto.getClass()
                        .getDeclaredField("number");
                    numberField.setAccessible(true);
                    numberField.set(dto, startNumber--); // 역순으로 할당
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("number 필드 접근 중 오류 발생", e);
                }
            }

            // 응답 객체 생성 및 반환
            PagingResponseDto<T> result = PagingResponseDto.<T>builder()
                .size(limit)
                .number(page)
                .totalPages((int) Math.ceil((double) totalCount / limit))
                .totalElements(totalCount)
                .content(results)
                .build();

            return result;
            // }catch (IllegalAccessExcep/*tion e) {
            // throw new RuntimeExcep*/tion("페이징 처리 중 오류가 발생했습니다.", e);
        } catch (MyBatisSystemException e) {
            log.error("MyBatis 시스템 오류 발생: {}", e.toString());
            throw new RuntimeException("SQL 실행 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("페이징 처리 중 오류 발생: {}", e.toString());
            throw new RuntimeException("페이징 처리 중 오류가 발생했습니다.", e);
        }
    }
}
