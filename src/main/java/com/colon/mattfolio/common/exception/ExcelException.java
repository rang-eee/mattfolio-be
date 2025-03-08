package com.colon.mattfolio.common.exception;

import com.colon.mattfolio.common.base.BaseReason;
import com.colon.mattfolio.common.property.Message;

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
        INVALID_RESOURCE(1010, Message.getMessage("excel.invalid.resource")), // 엑셀 이상
        FAIL_CREATE_XLSX(1011, Message.getMessage("excel.fail.create")), // 엑셀 파일 생성 실패
        EMPTY_FILE(1012, Message.getMessage("excel.empty.file")), // 빈 파일
        NOT_EXCEL_EXTENSION(1013, Message.getMessage("excel.invalid.ext")), // 엑셀 확장자 이상
        INVALID_HEADER(1014, Message.getMessage("excel.invalid.header")), // 잘못된 헤더
        HEADER_MISMATCH(1015, Message.getMessage("excel.header.mismatch")), // 헤더와 DTO 불일치
        ROW_MAPPING_FAIL(1016, Message.getMessage("excel.row.mapping.fail")); // 행 매핑 실패
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
