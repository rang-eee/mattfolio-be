package com.colon.mattfolio.api.auth.service;

import com.colon.mattfolio.api.auth.dto.LoginRequest;
import com.colon.mattfolio.api.auth.dto.RefreshTokenResponse;
import com.colon.mattfolio.api.auth.dto.LoginResponse;

public interface RequestService<T> {
    LoginResponse loginOrSignup(LoginRequest tokenRequest);

    RefreshTokenResponse getToken(LoginRequest tokenRequest);

    T getUserInfo(String accessToken);

    RefreshTokenResponse getRefreshToken(String provider, String refreshToken);
}
