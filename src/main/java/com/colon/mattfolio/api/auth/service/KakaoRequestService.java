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
import com.colon.mattfolio.common.auth.AuthUtil;
import com.colon.mattfolio.common.auth.oauth.dto.KakaoUserInfo;
import com.colon.mattfolio.common.auth.oauth.dto.KakaoUserInfo.KakaoAccount;
import com.colon.mattfolio.common.auth.oauth.dto.KakaoUserInfo.KakaoAccount.Profile;
import com.colon.mattfolio.common.enumType.AccountRoleType;
import com.colon.mattfolio.common.enumType.AccountStatusType;
import com.colon.mattfolio.common.enumType.LoginAuthProvider;
import com.colon.mattfolio.database.account.entity.AccountEntity;
import com.colon.mattfolio.database.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoRequestService implements RequestService<KakaoUserInfo> {
    private final AccountRepository accountRepository;
    private final AuthUtil authUtil;
    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String GRANT_TYPE;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;

    @Override
    public LoginResponse loginOrSignup(LoginRequest loginRequest) {
        LoginAuthProvider kakaoLoginAuthProvider = LoginAuthProvider.KAKAO;

        RefreshTokenResponse tokenResponse = getToken(loginRequest);
        KakaoUserInfo kakaoUserInfo = getUserInfo(tokenResponse.getAccessToken());

        KakaoAccount kakaoAccount = kakaoUserInfo.getKakaoAccount();
        Long kakaoId = kakaoUserInfo.getId();
        String stringKakaoId = String.valueOf(kakaoUserInfo.getId());
        Profile kakaoProfile = kakaoAccount.getProfile();

        // Boolean existsUser = accountRepository.existsById(kakaoId);
        Optional<AccountEntity> existsUserOpt = accountRepository.findByLoginAuthProviderAndLoginAuthProviderId(LoginAuthProvider.KAKAO, stringKakaoId);
        Boolean needFaceup = false;

        if (existsUserOpt.isPresent()) {
            AccountEntity user = existsUserOpt.get();

            needFaceup = user.getStatus()
                .equals(AccountStatusType.FACE_UNIDENTIFIED);
        } else {
            // 신규 회원 얼굴사진 업로드 필요
            needFaceup = true;

            AccountEntity accountEntity = AccountEntity.builder()
                .loginAuthProvider(kakaoLoginAuthProvider)
                .loginAuthProviderId(stringKakaoId)
                .email(kakaoAccount.getEmail())
                .name(kakaoProfile.getNickname())
                .profileImgUrl(kakaoProfile.getProfileImageUrl())
                .status(AccountStatusType.FACE_UNIDENTIFIED)
                .role(AccountRoleType.USER)
                .build();
            accountRepository.save(accountEntity);
        }

        String accessToken = authUtil.createAccessToken(String.valueOf(kakaoId), kakaoLoginAuthProvider, tokenResponse.getAccessToken());
        String refreshToken = authUtil.createRefreshToken(String.valueOf(kakaoId), kakaoLoginAuthProvider, tokenResponse.getRefreshToken());
        return LoginResponse.builder()
            .authProvider(kakaoLoginAuthProvider)
            .kakaoUserInfo(kakaoUserInfo)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .needFaceup(needFaceup)
            .build();
    }

    @Override
    public RefreshTokenResponse getToken(LoginRequest loginRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", GRANT_TYPE);
        formData.add("redirect_uri", REDIRECT_URI);
        formData.add("client_id", CLIENT_ID);
        formData.add("client_secret", CLIENT_SECRET);
        formData.add("code", loginRequest.getCode());

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
    public KakaoUserInfo getUserInfo(String accessToken) {
        return webClient.mutate()
            .baseUrl(USER_INFO_URI)
            .build()
            .get()
            // .uri("/v2/user/me")
            .headers(h -> h.setBearerAuth(accessToken))
            .retrieve()
            .bodyToMono(KakaoUserInfo.class)
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
            // .uri("/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            // .onStatus(HttpStatus::is4xxClientError, response -> Mono.just(new BadRequestException()))
            .bodyToMono(RefreshTokenResponse.class)
            .block();
    }
}
