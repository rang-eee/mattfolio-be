package com.colon.mattfolio.api.auth.service;

import org.springframework.stereotype.Service;

import com.colon.mattfolio.api.auth.dto.LoginRequest;
import com.colon.mattfolio.api.auth.dto.LoginResponse;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;
import com.colon.mattfolio.common.exception.AuthException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoRequestService kakaoRequestService;
    private final NaverRequestService naverRequestService;
    // private final GoogleRequestService googleRequestService;

    public LoginResponse siginin(LoginRequest loginRequest) throws AuthException {
        if (LoginAuthProvider.KAKAO.getAuthProvider()
            .equals(loginRequest.getRegistrationId())) {
            return kakaoRequestService.loginOrSignup(loginRequest);
        } else if (LoginAuthProvider.NAVER.getAuthProvider()
            .equals(loginRequest.getRegistrationId())) {
            return naverRequestService.loginOrSignup(loginRequest);
        }
        // else if (AuthProvider.GOOGLE.getAuthProvider()
        // .equals(loginRequest.getRegistrationId())) {
        // return googleRequestService.redirect(loginRequest);
        // }

        throw new AuthException(AuthException.Reason.INVALID_PROVIDER);
    }

    public LoginResponse signup(LoginRequest loginRequest) throws AuthException {
        if (LoginAuthProvider.KAKAO.getAuthProvider()
            .equals(loginRequest.getRegistrationId())) {
            return kakaoRequestService.loginOrSignup(loginRequest);
        } else if (LoginAuthProvider.NAVER.getAuthProvider()
            .equals(loginRequest.getRegistrationId())) {
            return naverRequestService.loginOrSignup(loginRequest);
        }

        throw new AuthException(AuthException.Reason.INVALID_PROVIDER);
    }

}
