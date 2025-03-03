package com.colon.mattfolio.common.enumType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountRoleType {
    USER("ROLE_USER"), //
    ADMIN("ROLE_ADMIN");

    private final String key;
}
