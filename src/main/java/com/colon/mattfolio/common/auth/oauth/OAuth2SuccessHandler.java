package com.colon.mattfolio.common.auth.oauth;

import java.io.IOException;
import java.time.Duration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import com.colon.mattfolio.common.auth.jwt.TokenProvider;
import com.colon.mattfolio.common.auth.oauth.dto.OAuth2UserInfo;
import com.colon.mattfolio.common.auth.oauth.dto.OAuth2UserInfoFactory;
import com.colon.mattfolio.database.account.entity.AccountEntity;
import com.colon.mattfolio.database.account.entity.AccountTokenEntity;
import com.colon.mattfolio.database.account.repository.AccountRepository;
import com.colon.mattfolio.database.account.repository.AccountTokenRepository;
import com.colon.mattfolio.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    private static final String REDIRECT_PATH = "/auth/success";

    private final TokenProvider tokenProvider;
    private final AccountTokenRepository tokenRepository;
    private final AccountRepository accountRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

    /**
     * OAuth2 인증 성공 후 호출되어, 토큰 생성 및 리다이렉트를 처리합니다. <br/>
     * 1. 공급자(registrationId)와 사용자 속성(attributes)를 통해 공통 DTO를 생성합니다. <br/>
     * 2. 계정을 조회하여, 리프레시 토큰과 액세스 토큰을 생성합니다. <br/>
     * 3. 리프레시 토큰은 DB에 저장하고, 쿠키에 추가합니다. <br/>
     * 4. 액세스 토큰은 URL 쿼리 파라미터로 추가한 후 리다이렉트합니다. <br/>
     * 
     * @param request HTTP 요청 객체 <br/>
     * @param response HTTP 응답 객체 <br/>
     * @param authentication 인증 성공 후의 Authentication 객체 <br/>
     * @throws IOException 발생 시 예외 전달 <br/>
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 인증 토큰에서 등록 ID를 추출합니다. (예: "kakao", "google", "naver") <br/>
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        // 공통 팩토리를 통해 공급자별 OAuth2UserInfo 객체를 생성합니다. <br/>
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        // 로그인 공급자와 공급자 ID를 기반으로 계정을 조회합니다. <br/>
        AccountEntity account = accountRepository.findByLoginAuthProviderAndLoginAuthProviderId(userInfo.getProvider(), userInfo.getProviderId())
            .orElse(null);

        // 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장 <br/>
        String refreshToken = tokenProvider.generateToken(account, REFRESH_TOKEN_DURATION);
        this.saveRefreshToken(account.getAccountId(), refreshToken);
        this.addRefreshTokenToCookie(request, response, refreshToken);

        // 액세스 토큰 생성 -> URL 쿼리 파라미터에 추가할 토큰 생성 <br/>
        String accessToken = tokenProvider.generateToken(account, ACCESS_TOKEN_DURATION);
        String targetUrl = this.getTargetUrl(accessToken);

        // 인증 관련 설정값과 쿠키를 제거합니다. <br/>
        this.clearAuthenticationAttributes(request, response);

        // 최종적으로 targetUrl로 클라이언트를 리다이렉트합니다. <br/>
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * 생성된 리프레시 토큰을 DB에 저장합니다. <br/>
     * 기존 토큰이 있으면 업데이트하고, 없으면 새로 생성합니다. <br/>
     * 
     * @param userId 계정의 고유 ID <br/>
     * @param newRefreshToken 새로 생성된 리프레시 토큰 <br/>
     */
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        AccountTokenEntity refreshToken = tokenRepository.findByAccountId(userId)
            .map(entity -> entity.updateRefreshToken(newRefreshToken))
            .orElse(AccountTokenEntity.builder()
                .accountId(userId)
                .build());
        tokenRepository.save(refreshToken);
    }

    /**
     * 생성된 리프레시 토큰을 쿠키에 저장합니다. <br/>
     * 기존 쿠키를 삭제하고, 새 토큰 쿠키를 추가합니다. <br/>
     * 
     * @param request HTTP 요청 객체 <br/>
     * @param response HTTP 응답 객체 <br/>
     * @param refreshToken 저장할 리프레시 토큰 <br/>
     */
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    /**
     * 인증 관련 설정값과 쿠키를 제거합니다. <br/>
     * OAuth2 인증 후 남은 임시 데이터와 쿠키를 정리합니다. <br/>
     * 
     * @param request HTTP 요청 객체 <br/>
     * @param response HTTP 응답 객체 <br/>
     */
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    /**
     * 액세스 토큰을 쿼리 파라미터로 추가한 URL을 생성하여 반환합니다. <br/>
     * 
     * @param token 생성된 액세스 토큰 <br/>
     * @return 액세스 토큰이 쿼리 파라미터로 포함된 리다이렉트 URL <br/>
     */
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
            .queryParam("accessToken", token)
            .build()
            .toUriString();
    }
}
