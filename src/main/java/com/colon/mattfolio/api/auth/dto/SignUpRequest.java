package com.colon.mattfolio.api.auth.dto;

import com.colon.mattfolio.common.enumType.LoginAuthProvider;

import lombok.Getter;

@Getter
public class SignUpRequest {
    private String id;
    private String email;
    private String nickname;
    private String name;
    private String providerId;
    private LoginAuthProvider authProvider;
    private String profileImageUrl;
}
