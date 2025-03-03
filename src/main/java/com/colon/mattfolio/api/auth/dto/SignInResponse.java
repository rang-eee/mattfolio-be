package com.colon.mattfolio.api.auth.dto;

import com.colon.mattfolio.common.auth.oauth.dto.GoogleUserInfo;
import com.colon.mattfolio.common.auth.oauth.dto.KakaoUserInfo;
import com.colon.mattfolio.common.auth.oauth.dto.NaverUserInfo;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignInResponse {
    private LoginAuthProvider authProvider;
    private KakaoUserInfo kakaoUserInfo;
    private NaverUserInfo naverUserInfo;
    private GoogleUserInfo googleUserInfo;
    private String accessToken;
    private String refreshToken;

    @Builder
    public SignInResponse(LoginAuthProvider authProvider, KakaoUserInfo kakaoUserInfo, NaverUserInfo naverUserInfo, GoogleUserInfo googleUserInfo, String accessToken, String refreshToken) {
        this.authProvider = authProvider;
        this.kakaoUserInfo = kakaoUserInfo;
        this.naverUserInfo = naverUserInfo;
        this.googleUserInfo = googleUserInfo;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
