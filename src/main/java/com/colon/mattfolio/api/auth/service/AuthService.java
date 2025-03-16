package com.colon.mattfolio.api.auth.service;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import com.colon.mattfolio.api.auth.dto.LoginRequest;
import com.colon.mattfolio.api.auth.dto.LoginResponse;
import com.colon.mattfolio.api.auth.dto.RefreshTokenResponse;
import com.colon.mattfolio.common.auth.AuthUtil;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;
import com.colon.mattfolio.common.exception.AuthException;
import com.colon.mattfolio.database.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoRequestService kakaoRequestService;
    private final NaverRequestService naverRequestService;
    // private final GoogleRequestService googleRequestService;
    private final AccountRepository accountRepository;
    private final AuthUtil authUtil;

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

    public LoginResponse refreshToken(LoginRequest loginRequest) throws BadRequestException {
        String userId = (String) authUtil.get(loginRequest.getRefreshToken())
            .get("userId");
        String provider = (String) authUtil.get(loginRequest.getRefreshToken())
            .get("provider");
        String oldRefreshToken = (String) authUtil.get(loginRequest.getRefreshToken())
            .get("refreshToken");

        if (!accountRepository.existsByLoginAuthProviderAndLoginAuthProviderId(LoginAuthProvider.findByCode(provider), userId)) {
            throw new BadRequestException("CANNOT_FOUND_USER");
        }

        RefreshTokenResponse tokenResponse = null;
        if (LoginAuthProvider.KAKAO.getAuthProvider()
            .equals(provider.toLowerCase())) {
            tokenResponse = kakaoRequestService.getRefreshToken(provider, oldRefreshToken);
        } else if (LoginAuthProvider.NAVER.getAuthProvider()
            .equals(provider.toLowerCase())) {
            tokenResponse = naverRequestService.getRefreshToken(provider, oldRefreshToken);
        }
        // else if (AuthProvider.GOOGLE.getAuthProvider()
        // .equals(provider.toLowerCase())) {
        // tokenResponse = googleRequestService.getRefreshToken(provider,
        // oldRefreshToken);
        // }

        String accessToken = authUtil.createAccessToken(userId, LoginAuthProvider.findByCode(provider.toLowerCase()), tokenResponse.getAccessToken());

        return LoginResponse.builder()
            .authProvider(LoginAuthProvider.findByCode(provider.toLowerCase()))
            .kakaoUserInfo(null)
            .accessToken(accessToken)
            .refreshToken(null)
            .build();
    }
}
