package com.colon.mattfolio.common.exception;

import com.colon.mattfolio.common.base.ApiResultCode;
import com.colon.mattfolio.common.base.BaseReason;
import com.colon.mattfolio.common.property.MessageProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Example API의 예외 처리 클래스
 * 
 * Example API에서 발생할 수 있는 예외 상황들을 정의하며, 각 예외는 `MasterException`을 상속받아 공통적인 예외 처리 방식을 유지합니다.
 */
public class ExampleException extends MasterException {

    /**
     * Example API 예외 사유를 정의하는 열거형
     * 
     * 
     * 이 열거형은 `BaseReason` 인터페이스를 구현하며, 예외 코드와 메시지를 포함합니다.
     * 
     * 각 상수는 특정 예외 상황에 대한 설명과 해당 메시지를 제공합니다.
     */
    @Getter
    @AllArgsConstructor
    public enum Reason implements BaseReason {
        INVALID_REQUEST(ApiResultCode.Example.INVALID_REQUEST.getCode(), MessageProperties.getMessage("example.invalid.request")), // 요청 파라미터 이상
        INVALID_ID(ApiResultCode.Example.INVALID_ID.getCode(), MessageProperties.getMessage("example.invalid.id")), // 유효하지 않은 아이디
        NOT_EXIST_NAME(ApiResultCode.Example.NOT_EXIST_NAME.getCode(), MessageProperties.getMessage("example.not.exist.name")), // 존재하지 않는 이름
        DUPLICATE_ID(ApiResultCode.Example.DUPLICATE_ID.getCode(), MessageProperties.getMessage("example.duplicate.id")); // 중복된 아이디 존재

        private final Integer code; // 예외 코드
        private final String message; // 예외 메시지
    }

    /**
     * 생성자: 주어진 `Reason`을 기반으로 예외를 생성합니다.
     * 
     * @param reason 예외의 원인을 설명하는 `Reason` 객체
     */
    public ExampleException(Reason reason) {
        super(reason);
    }
}
