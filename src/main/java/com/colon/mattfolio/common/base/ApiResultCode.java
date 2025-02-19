package com.colon.mattfolio.common.base;

import com.colon.mattfolio.exception.ExampleException;
import com.colon.mattfolio.exception.ExcelException;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 결과 코드 관련 클래스
 * 
 * 이 클래스는 API 요청의 처리 결과에 대한 코드를 정의합니다. <br>
 *
 * 코드 범위: <br>
 * - 100번대: 정상 처리 관련 코드 <br>
 * - 900번대: 오류 처리 관련 코드 <br>
 * - 200~800번대: 각 업무에서 사용하는 결과 코드 (업무별로 200부터 10단위 순차적으로 사용) <br>
 */
public class ApiResultCode {

    /**
     * API 공통 결과 코드
     * 
     * API 요청 성공 및 실패와 같은 공통적인 결과를 나타내는 코드입니다.
     */
    @Getter
    @AllArgsConstructor
    public enum Common {
        SUCCESS(100), // 공통 - 정상 처리
        // 994 HttpStatus 413(파일업로드 용량 초과) 에러 케이스
        // 995 HttpStatus 403 에러 케이스
        // 996 HttpStatus 404 에러 케이스
        // 997 HttpStatus 405 에러 케이스
        // 998 입력 값에 대한 유요성 검증을 통과하지 못한 케이스
        FAIL(999); // 공통 - 서비스 로직 상의 오류

        private final Integer code;
    }

    /**
     * 엑셀 관련 API 결과 코드
     * 
     * 엑셀 관련 API에서 발생할 수 있는 결과 코드입니다. <br>
     * 각 코드는 `ExcelException.Reason`과 연결되어 있어, 예외 발생 시 적절한 코드와 메시지를 제공합니다.
     */
    @Getter
    @AllArgsConstructor
    public enum Excel {
        INVALID_RESOURCE(201, ExcelException.Reason.INVALID_RESOURCE), // 엑셀 이상
        FAIL_CREATE_XLSX(202, ExcelException.Reason.FAIL_CREATE_XLSX), // 엑셀 파일 생성 실패
        ;

        private final Integer code;
        private final BaseReason reason; // 예외 사유와 연결된 객체
    }

    /**
     * Example API 결과 코드
     * 
     * Example API에서 발생할 수 있는 결과 코드입니다. <br>
     * 각 코드는 `ExampleException.Reason`과 연결되어 있어, 예외 발생 시 적절한 코드와 메시지를 제공합니다.
     */
    @Getter
    @AllArgsConstructor
    public enum Example {
        INVALID_REQUEST(210, ExampleException.Reason.INVALID_REQUEST), // 요청 파라미터 이상
        INVALID_ID(211, ExampleException.Reason.INVALID_ID), // 유효하지 않은 아이디
        NOT_EXIST_NAME(212, ExampleException.Reason.NOT_EXIST_NAME), // 존재하지 않는 이름
        DUPLICATE_ID(213, ExampleException.Reason.DUPLICATE_ID); // 중복된 아이디 존재

        private final Integer code;
        private final BaseReason reason; // 예외 사유와 연결된 객체
    }

}
