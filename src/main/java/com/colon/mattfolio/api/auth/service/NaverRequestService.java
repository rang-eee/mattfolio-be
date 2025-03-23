package com.colon.mattfolio.api.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.colon.mattfolio.api.auth.dto.LoginRequest;
import com.colon.mattfolio.api.auth.dto.LoginResponse;
import com.colon.mattfolio.api.auth.dto.RefreshTokenResponse;
import com.colon.mattfolio.common.auth.jwt.TokenProvider;
import com.colon.mattfolio.common.auth.oauth.dto.NaverUserInfo;
import com.colon.mattfolio.common.auth.oauth.dto.NaverUserInfo.NaverAccount;
import com.colon.mattfolio.common.enumType.AccountRoleType;
import com.colon.mattfolio.common.enumType.AccountStatusType;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;
import com.colon.mattfolio.database.account.entity.AccountEntity;
import com.colon.mattfolio.database.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NaverRequestService implements RequestService<NaverUserInfo> {
    private final AccountRepository accountRepository;
    private final TokenProvider tokenProvider;
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
    public LoginResponse loginOrSignup(LoginRequest loginRequest) {
        LoginAuthProvider naverLoginAuthProvider = LoginAuthProvider.NAVER;
        RefreshTokenResponse tokenResponse = getToken(loginRequest);
        NaverUserInfo naverUserInfo = getUserInfo(tokenResponse.getAccessToken());

        NaverAccount naverAccount = naverUserInfo.getResponse();
        String naverId = naverAccount.getId();

        Optional<AccountEntity> existsUserOpt = accountRepository.findByLoginAuthProviderAndLoginAuthProviderId(LoginAuthProvider.NAVER, naverId);
        Boolean needFaceup = false;

        AccountEntity account;
        if (existsUserOpt.isPresent()) {
            account = existsUserOpt.get();

            needFaceup = account.getStatus()
                .equals(AccountStatusType.FACE_UNIDENTIFIED);
        } else {
            // 신규 회원 얼굴사진 업로드 필요
            needFaceup = true;

            account = AccountEntity.builder()
                .loginAuthProvider(naverLoginAuthProvider)
                .loginAuthProviderId(naverId)
                .email(naverAccount.getEmail())
                .name(naverAccount.getName())
                .profileImgUrl(naverAccount.getProfile_image())
                .status(AccountStatusType.FACE_UNIDENTIFIED)
                .role(AccountRoleType.USER)
                .build();
            accountRepository.save(account);
        }

        String accessToken = tokenProvider.createAccessToken(account, naverLoginAuthProvider, tokenResponse.getAccessToken());
        String refreshToken = tokenProvider.createRefreshToken(account, naverLoginAuthProvider, accessToken);

        return LoginResponse.builder()
            .authProvider(naverLoginAuthProvider)
            .naverUserInfo(naverUserInfo)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .needFaceup(needFaceup)
            .build();
    }

    @Override
    public RefreshTokenResponse getToken(LoginRequest loginRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", GRANT_TYPE);
        formData.add("client_id", CLIENT_ID);
        formData.add("client_secret", CLIENT_SECRET);
        formData.add("code", loginRequest.getCode());
        formData.add("state", loginRequest.getState());

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
