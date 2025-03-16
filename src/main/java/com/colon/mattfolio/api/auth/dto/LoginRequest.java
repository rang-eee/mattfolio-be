package com.colon.mattfolio.api.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginRequest {
    private String registrationId;
    private String code;
    private String state;
    private String refreshToken;
}
