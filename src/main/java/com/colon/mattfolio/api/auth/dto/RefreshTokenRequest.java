package com.colon.mattfolio.api.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshTokenRequest {
    private String registrationId;
    private String code;
    private String state;
    private String refreshToken;

    @Builder
    public RefreshTokenRequest(String registrationId, String code, String state) {
        this.registrationId = registrationId;
        this.code = code;
        this.state = state;
    }
}
