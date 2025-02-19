package com.colon.mattfolio.exception;

import com.colon.mattfolio.common.base.ApiResultCode;
import com.colon.mattfolio.common.base.BaseReason;
import com.colon.mattfolio.config.Message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Excel exception
 * 
 */
public class ExcelException extends MasterException {

    /**
     * Excel API 예외 사유를 정의하는 열거형
     * 
     * 
     * 이 열거형은 `BaseReason` 인터페이스를 구현하며, 예외 코드와 메시지를 포함합니다.
     * 
     * 각 상수는 특정 예외 상황에 대한 설명과 해당 메시지를 제공합니다.
     */
    @Getter
    @AllArgsConstructor
    public enum Reason implements BaseReason {
        INVALID_RESOURCE(ApiResultCode.Excel.INVALID_RESOURCE.getCode(), Message.getMessage("excel.invalid.resource")), // 엑셀 이상
        FAIL_CREATE_XLSX(ApiResultCode.Excel.FAIL_CREATE_XLSX.getCode(), Message.getMessage("excel.fail.create")), // 엑셀 파일 생성 실패
        ;

        private final Integer code; // 예외 코드
        private final String message; // 예외 메시지
    }

    /**
     * 생성자: 주어진 `Reason`을 기반으로 예외를 생성합니다.
     * 
     * @param reason 예외의 원인을 설명하는 `Reason` 객체
     */
    public ExcelException(Reason reason) {
        super(reason);
    }
}
