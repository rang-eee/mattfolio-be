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
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
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
    // private final AuthenticationManager authenticationManager;
    private final AppProperties appProperties;
    // private final UserDetailsServiceImpl userDetailsService;

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
            // NOTE: "password" grant type는 Spring Authorization Server에서 기본 지원되지 않으므로
            // 보안상의 이유로 사용을 재검토하시기 바랍니다.
            .authorizationGrantType(new AuthorizationGrantType("password"))
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
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
     * (선택사항) 기존 코드와 유사한 토큰 서비스 빈. Spring Authorization Server는 내부에서 토큰 관리를 처리하므로 별도 사용은 권장되지 않음.
     */
    // @Bean
    // @Primary
    // public DefaultTokenServices tokenServices() {
    // DefaultTokenServices tokenServices = new DefaultTokenServices();
    // tokenServices.setSupportRefreshToken(true);
    // return tokenServices;
    // }

    /**
     * OAuth2 예외 처리 번역기 (기존 loginExceptionTranslator와 유사하게 구현)
     */
    @Bean
    public WebResponseExceptionTranslator<OAuth2Exception> loginExceptionTranslator() {
        return new WebResponseExceptionTranslator<OAuth2Exception>() {
            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                e.printStackTrace();
                if (e instanceof LoginExpiredException) {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
                // 필요한 경우 여기서 e를 적절히 변환합니다.
                OAuth2Exception oAuth2Exception = new OAuth2Exception(e.getMessage());
                HttpHeaders headers = new HttpHeaders();
                return new ResponseEntity<>(oAuth2Exception, headers, HttpStatus.BAD_REQUEST);
            }
        };
    }

}
