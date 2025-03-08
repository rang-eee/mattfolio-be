package com.colon.mattfolio.api.auth.service;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import com.colon.mattfolio.api.auth.dto.SignInResponse;
import com.colon.mattfolio.common.auth.AuthUtil;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;
import com.colon.mattfolio.common.exception.AuthException;
import com.colon.mattfolio.database.account.repository.AccountRepository;

import com.colon.mattfolio.api.auth.dto.SignInRequest;
import com.colon.mattfolio.api.auth.dto.RefreshTokenResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoRequestService kakaoRequestService;
    private final NaverRequestService naverRequestService;
    // private final GoogleRequestService googleRequestService;
    private final AccountRepository accountRepository;
    private final AuthUtil authUtil;

    public SignInResponse siginin(SignInRequest tokenRequest) throws AuthException {
        if (LoginAuthProvider.KAKAO.getAuthProvider()
            .equals(tokenRequest.getRegistrationId())) {
            return kakaoRequestService.redirect(tokenRequest);
        } else if (LoginAuthProvider.NAVER.getAuthProvider()
            .equals(tokenRequest.getRegistrationId())) {
            return naverRequestService.redirect(tokenRequest);
        }
        // else if (AuthProvider.GOOGLE.getAuthProvider()
        // .equals(tokenRequest.getRegistrationId())) {
        // return googleRequestService.redirect(tokenRequest);
        // }

        throw new AuthException(AuthException.Reason.INVALID_PROVIDER);
    }

    public SignInResponse refreshToken(SignInRequest tokenRequest) throws BadRequestException {
        String userId = (String) authUtil.get(tokenRequest.getRefreshToken())
            .get("userId");
        String provider = (String) authUtil.get(tokenRequest.getRefreshToken())
            .get("provider");
        String oldRefreshToken = (String) authUtil.get(tokenRequest.getRefreshToken())
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

        return SignInResponse.builder()
            .authProvider(LoginAuthProvider.findByCode(provider.toLowerCase()))
            .kakaoUserInfo(null)
            .accessToken(accessToken)
            .refreshToken(null)
            .build();
    }
}
