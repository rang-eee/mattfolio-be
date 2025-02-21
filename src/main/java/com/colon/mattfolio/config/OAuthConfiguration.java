package com.colon.mattfolio.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import com.colon.mattfolio.exception.LoginExpiredException;
import com.colon.mattfolio.property.AppProperties;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AppProperties.class)
public class OAuthConfiguration {

    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    /**
     * OAuth2 인증 서버 엔드포인트에 대한 보안 필터 체인
     */
    @Bean
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Spring Authorization Server의 기본 보안 설정 적용
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.formLogin(Customizer.withDefaults())
            .build();
    }

    /**
     * 클라이언트 정보 등록 (in-memory)
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        AppProperties.Oauth oauthProperties = appProperties.getOauth();

        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID()
            .toString())
            .clientId(oauthProperties.getClientId())
            .clientSecret(passwordEncoder.encode(oauthProperties.getClientSecret()))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)

            // [중요] password grant 제거
            // .authorizationGrantType(new AuthorizationGrantType("password"))

            // 권장: Authorization Code, Refresh Token, Client Credentials
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)

            // Authorization Code 방식을 사용할 때 필요한 redirectUri
            // .redirectUri("http://localhost:3000/login/oauth2/code/mattfolio")
            .redirectUri("http://localhost:8180/swagger-ui/oauth2-redirect.html")
            // ↑ 실제 리다이렉트 경로로 수정

            .scope("any")
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofSeconds(oauthProperties.getTokenValiditySeconds()))
                .refreshTokenTimeToLive(Duration.ofSeconds(oauthProperties.getRefreshTokenValiditySeconds()))
                .build())
            .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    /**
     * 발급자(issuer) 설정
     */
    @Bean
    public AuthorizationServerSettings providerSettings() {
        // 기본 토큰 엔드포인트: /oauth2/token
        // 기본 인가 엔드포인트: /oauth2/authorize
        return AuthorizationServerSettings.builder()
            .issuer("http://localhost:8080")
            .build();
    }

    /**
     * JWT 서명/검증용 JwtEncoder (대칭키 방식)
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey secretKey = new SecretKeySpec(appProperties.getOauth()
            .getTokenSigningKey()
            .getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    /**
     * JWT 검증용 JwtDecoder
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(appProperties.getOauth()
            .getTokenSigningKey()
            .getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey)
            .build();
    }

    /**
     * OAuth2 예외 처리 번역기 (기존 loginExceptionTranslator와 유사하게 구현)
     */
    @Bean
    public WebResponseExceptionTranslator<OAuth2Exception> loginExceptionTranslator() {
        return e -> {
            e.printStackTrace();
            if (e instanceof LoginExpiredException) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            OAuth2Exception oAuth2Exception = new OAuth2Exception(e.getMessage());
            HttpHeaders headers = new HttpHeaders();
            return new ResponseEntity<>(oAuth2Exception, headers, HttpStatus.BAD_REQUEST);
        };
    }

}
