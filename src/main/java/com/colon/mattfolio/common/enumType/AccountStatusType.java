package com.colon.mattfolio.common.enumType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountStatusType {
    FACE_UNIDENTIFIED("얼굴 미식별"), //
    NORMAL("정상"), //
    WITHDRAWN("탈퇴"), //
    SUSPENDED("정지") //
    ;

    private final String key;
}
