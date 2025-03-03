package com.colon.mattfolio.common.enumType;

import java.util.Arrays;

public enum LoginAuthProvider {
    GOOGLE("google"), //
    KAKAO("kakao"), //
    NAVER("naver"), //
    EMPTY("");

    private String authProvider;

    public String getAuthProvider() {
        return authProvider;
    }

    LoginAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public static LoginAuthProvider findByCode(String code) {
        return Arrays.stream(LoginAuthProvider.values())
            .filter(provider -> provider.getAuthProvider()
                .equals(code))
            .findFirst()
            .orElse(EMPTY);
    }
}