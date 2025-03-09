package com.colon.mattfolio.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.colon.mattfolio.common.auth.SecurityAuthenticationFailEntryPoint;
import com.colon.mattfolio.common.auth.SecurityTokenAuthenticationFilter;
import com.colon.mattfolio.common.auth.SecurityTokenExceptionFilter;

import lombok.RequiredArgsConstructor;

/**
 * SecurityConfig 클래스는 스프링 시큐리티의 HTTP 보안 설정을 구성합니다. <br/>
 * 주요 역할: <br/>
 * - 정적 리소스 및 Swagger 관련 URL에 대해 보안 필터를 적용하지 않음 <br/>
 * - CSRF, HTTP 기본 인증, 폼 로그인, 로그아웃 등의 기본 기능을 비활성화 <br/>
 * - 세션 관리를 STATELESS 모드로 설정하여 토큰 기반 인증을 사용 <br/>
 * - TokenAuthenticationFilter를 UsernamePasswordAuthenticationFilter보다 먼저 실행하여 토큰 인증 처리 <br/>
 * - TokenExceptionFilter를 추가해 토큰 관련 예외를 처리 <br/>
 * - URL별 접근 권한 설정: 일부 URL은 인증 없이 접근 허용, "/v1/api/**"는 인증된 사용자만 접근 <br/>
 * - OAuth2 로그인 관련 엔드포인트 및 핸들러 구성 <br/>
 * - 로그아웃 후 리다이렉트 및 인증 실패 시 커스텀 에러 응답(CustomAuthenticationEntryPoint) 반환
 *
 */
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    // TokenAuthenticationFilter를 생성자 주입 받음 (순환 참조 주의)
    private final SecurityTokenAuthenticationFilter tokenAuthenticationFilter;

    /**
     * WebSecurityCustomizer 빈을 정의하여 스프링 시큐리티가 아래 URL에 대해 보안 필터를 적용하지 않도록 설정합니다. <br/>
     * 주로 정적 리소스, 에러 페이지, Swagger 관련 URL 등이 포함됩니다. <br/>
     * URL 예: "/error", "/favicon.ico", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/apidocs.html", "/apidocs", "/apidocs/**"
     *
     * @return WebSecurityCustomizer 빈
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .requestMatchers("/error", // 에러 관련 URL <br/>
                    "/favicon.ico", // 파비콘 <br/>
                    "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/apidocs.html", "/apidocs", "/apidocs/**" // Swagger 및 API 문서 관련 URL
            );
    }

    /**
     * SecurityFilterChain 빈을 정의하여 HTTP 보안 설정을 구성합니다. <br/>
     * 주요 설정: <br/>
     * - CSRF, HTTP 기본 인증, 폼 로그인, 로그아웃 기능 비활성화 <br/>
     * - 세션 관리: STATELESS 모드 (토큰 기반 인증 사용) <br/>
     * - 커스텀 토큰 인증 필터(TokenAuthenticationFilter)를 UsernamePasswordAuthenticationFilter 앞에 추가 <br/>
     * - TokenExceptionFilter를 추가하여 토큰 인증 과정 중 발생하는 예외를 처리 <br/>
     * - URL별 접근 권한 설정: 특정 URL은 permitAll, "/v1/api/**"는 인증 필요 <br/>
     * - OAuth2 로그인 설정: 로그인 페이지, OAuth2 엔드포인트, 인증 성공 핸들러, 사용자 정보 서비스 설정 <br/>
     * - 로그아웃 후 리다이렉트 URL 설정 <br/>
     * - 예외 처리: "/v1/api/**" 경로에 대해 인증 실패 시 CustomAuthenticationEntryPoint를 실행하여 커스텀 JSON 에러 응답 반환
     *
     * @param http HttpSecurity 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 설정 중 예외 발생 시 던짐
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // CSRF, HTTP 기본 인증, 폼 로그인, 로그아웃 비활성화
        http.csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable())
            .logout(logout -> logout.disable());

        // 세션 관리를 STATELESS 모드로 설정 (토큰 기반 인증을 사용하므로)
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 커스텀 토큰 인증 필터를 UsernamePasswordAuthenticationFilter보다 먼저 실행하도록 추가 <br/>
        // 이후 TokenExceptionFilter를 추가하여 토큰 인증 필터에서 발생하는 예외를 처리
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new SecurityTokenExceptionFilter(), tokenAuthenticationFilter.getClass());

        // URL별 접근 권한 설정
        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/token")
            .permitAll() // 토큰 재발급 등 인증 없이 접근 가능한 URL
            .requestMatchers("/login/**", "/oauth2/**", "/auth/**", "/h2-console/**")
            .permitAll() // 로그인, OAuth2, H2 콘솔 관련 URL은 인증 없이 접근
            .requestMatchers("/v1/api/**")
            .authenticated() // "/v1/api/**"는 인증된 사용자만 접근 가능
            .anyRequest()
            .permitAll() // 그 외의 모든 요청은 접근 허용
        );

        // 예외 처리 설정: "/v1/api/**" 경로에 대해 인증 실패 시 CustomAuthenticationEntryPoint를 실행하여 커스텀 JSON 에러 응답 반환
        http.exceptionHandling(exceptionHandling -> exceptionHandling.defaultAuthenticationEntryPointFor(new SecurityAuthenticationFailEntryPoint(), new AntPathRequestMatcher("/v1/api/**")));

        // 최종적으로 구성된 SecurityFilterChain을 반환
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // configuration.addAllowedOriginPattern("*"); // 🔥 모든 도메인 허용 (allowedOrigins("*") 대신 사용)
        configuration.addAllowedOrigin("http://localhost:3000");

        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedHeader("Authorization");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
