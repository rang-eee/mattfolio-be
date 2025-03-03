package com.colon.mattfolio.api.auth.service;

import com.colon.mattfolio.api.auth.dto.RefreshTokenRequest;
import com.colon.mattfolio.api.auth.dto.RefreshTokenResponse;
import com.colon.mattfolio.api.auth.dto.SignInResponse;

public interface RequestService<T> {
    SignInResponse redirect(RefreshTokenRequest tokenRequest);

    RefreshTokenResponse getToken(RefreshTokenRequest tokenRequest);

    T getUserInfo(String accessToken);

    RefreshTokenResponse getRefreshToken(String provider, String refreshToken);
}
