package com.colon.mattfolio.api.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.colon.mattfolio.api.auth.dto.SignInRequest;
import com.colon.mattfolio.api.auth.dto.RefreshTokenResponse;
import com.colon.mattfolio.api.auth.dto.SignInResponse;
import com.colon.mattfolio.common.auth.AuthUtil;
import com.colon.mattfolio.common.auth.oauth.dto.NaverUserInfo;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;
import com.colon.mattfolio.database.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NaverRequestService implements RequestService<NaverUserInfo> {
    private final AccountRepository accountRepository;
    private final AuthUtil authUtil;
    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}")
    private String GRANT_TYPE;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String USER_INFO_URI;

    @Override
    public SignInResponse redirect(SignInRequest tokenRequest) {
        RefreshTokenResponse tokenResponse = getToken(tokenRequest);
        NaverUserInfo naverUserInfo = getUserInfo(tokenResponse.getAccessToken());

        if (accountRepository.existsByLoginAuthProviderAndLoginAuthProviderId(LoginAuthProvider.GOOGLE, naverUserInfo.getResponse()
            .getId())) {
            String accessToken = authUtil.createAccessToken(naverUserInfo.getResponse()
                .getId(), LoginAuthProvider.NAVER, tokenResponse.getAccessToken());
            String refreshToken = authUtil.createRefreshToken(naverUserInfo.getResponse()
                .getId(), LoginAuthProvider.NAVER, tokenResponse.getRefreshToken());
            return SignInResponse.builder()
                .authProvider(LoginAuthProvider.NAVER)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        } else {
            return SignInResponse.builder()
                .authProvider(LoginAuthProvider.NAVER)
                .naverUserInfo(naverUserInfo)
                .build();
        }
    }

    @Override
    public RefreshTokenResponse getToken(SignInRequest tokenRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", GRANT_TYPE);
        formData.add("client_id", CLIENT_ID);
        formData.add("client_secret", CLIENT_SECRET);
        formData.add("code", tokenRequest.getCode());
        formData.add("state", tokenRequest.getState());

        return webClient.mutate()
            .baseUrl(TOKEN_URI)
            .build()
            .post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            // .onStatus(HttpStatus::is4xxClientError, response -> Mono.just(new BadRequestException()))
            .bodyToMono(RefreshTokenResponse.class)
            .block();
    }

    @Override
    public NaverUserInfo getUserInfo(String accessToken) {
        return webClient.mutate()
            .baseUrl(USER_INFO_URI)
            .build()
            .get()
            .headers(h -> h.setBearerAuth(accessToken))
            .retrieve()
            .bodyToMono(NaverUserInfo.class)
            .block();
    }

    @Override
    public RefreshTokenResponse getRefreshToken(String provider, String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", CLIENT_ID);
        formData.add("refresh_token", refreshToken);

        return webClient.mutate()
            .baseUrl(TOKEN_URI)
            .build()
            .post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            // .onStatus(HttpStatus::is4xxClientError, response -> Mono.just(new BadRequestException()))
            .bodyToMono(RefreshTokenResponse.class)
            .block();
    }
}
