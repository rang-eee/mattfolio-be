package com.colon.mattfolio.api.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.colon.mattfolio.api.auth.dto.LoginRequest;
import com.colon.mattfolio.api.auth.dto.RefreshTokenResponse;
import com.colon.mattfolio.api.auth.dto.LoginResponse;
import com.colon.mattfolio.common.auth.AuthUtil;
import com.colon.mattfolio.common.auth.oauth.dto.GoogleUserInfo;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;
import com.colon.mattfolio.database.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleRequestService implements RequestService<GoogleUserInfo> {
    private final AccountRepository accountRepository;
    private final AuthUtil authUtil;
    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.google.authorization-grant-type}")
    private String GRANT_TYPE;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String USER_INFO_URI;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String REDIRECT_URI;

    @Override
    public LoginResponse loginOrSignup(LoginRequest loginRequest) {
        RefreshTokenResponse tokenResponse = getToken(loginRequest);
        GoogleUserInfo googleUserInfo = getUserInfo(tokenResponse.getAccessToken());

        if (accountRepository.existsByLoginAuthProviderAndLoginAuthProviderId(LoginAuthProvider.GOOGLE, googleUserInfo.getId())) {
            String accessToken = authUtil.createAccessToken(googleUserInfo.getId(), LoginAuthProvider.GOOGLE, tokenResponse.getAccessToken());
            String refreshToken = authUtil.createRefreshToken(googleUserInfo.getId(), LoginAuthProvider.GOOGLE, tokenResponse.getRefreshToken());
            return LoginResponse.builder()
                .authProvider(LoginAuthProvider.GOOGLE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        } else {
            return LoginResponse.builder()
                .authProvider(LoginAuthProvider.GOOGLE)
                .googleUserInfo(googleUserInfo)
                .build();
        }
    }

    @Override
    public RefreshTokenResponse getToken(LoginRequest loginRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", loginRequest.getCode());
        formData.add("client_id", CLIENT_ID);
        formData.add("client_secret", CLIENT_SECRET);
        formData.add("redirect_uri", REDIRECT_URI);
        formData.add("grant_type", GRANT_TYPE);

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
    public GoogleUserInfo getUserInfo(String accessToken) {
        return webClient.mutate()
            .baseUrl(USER_INFO_URI)
            .build()
            .get()
            .headers(h -> h.setBearerAuth(accessToken))
            .retrieve()
            .bodyToMono(GoogleUserInfo.class)
            .block();
    }

    @Override
    public RefreshTokenResponse getRefreshToken(String provider, String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", CLIENT_ID);
        formData.add("client_secret", CLIENT_SECRET);
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
