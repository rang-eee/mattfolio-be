package com.colon.mattfolio.api.auth.service;

import com.colon.mattfolio.api.auth.dto.SignInRequest;
import com.colon.mattfolio.api.auth.dto.RefreshTokenResponse;
import com.colon.mattfolio.api.auth.dto.SignInResponse;

public interface RequestService<T> {
    SignInResponse redirect(SignInRequest tokenRequest);

    RefreshTokenResponse getToken(SignInRequest tokenRequest);

    T getUserInfo(String accessToken);

    RefreshTokenResponse getRefreshToken(String provider, String refreshToken);
}
