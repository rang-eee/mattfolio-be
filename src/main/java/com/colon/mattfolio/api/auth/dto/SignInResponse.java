package com.colon.mattfolio.api.auth.dto;

import com.colon.mattfolio.common.auth.oauth.dto.GoogleUserInfo;
import com.colon.mattfolio.common.auth.oauth.dto.KakaoUserInfo;
import com.colon.mattfolio.common.auth.oauth.dto.NaverUserInfo;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignInResponse {
    private LoginAuthProvider authProvider;

    private KakaoUserInfo kakaoUserInfo;

    private NaverUserInfo naverUserInfo;

    private GoogleUserInfo googleUserInfo;

    private String accessToken;

    private String refreshToken;

    @Builder.Default
    private Boolean needSignup = false;
}
