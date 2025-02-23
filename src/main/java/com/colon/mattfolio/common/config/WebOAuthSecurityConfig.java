package com.colon.mattfolio.common.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.colon.mattfolio.api.account.AccountService;
import com.colon.mattfolio.api.auth.service.OAuth2UserCustomService;
import com.colon.mattfolio.common.handler.handler.OAuth2SuccessHandler;
import com.colon.mattfolio.common.jwt.TokenAuthenticationFilter;
import com.colon.mattfolio.common.jwt.TokenProvider;
import com.colon.mattfolio.database.token.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.colon.mattfolio.database.token.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

	private final OAuth2UserCustomService oAuth2UserCustomService;
	private final TokenProvider tokenProvider;
	private final TokenRepository tokenRepository;
	private final AccountService accountService;

	// 스프링 시큐리티 기능 비활성화
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
			.requestMatchers(toH2Console())
			.requestMatchers("/img/**", "/css/**", "/js/**");
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		// 토큰 방식으로 인증을 하기 때문에 기존에 사용하던 폼로그인, 세션 비활성화
		http.csrf(csrf -> csrf.disable())
			.httpBasic(httpBasic -> httpBasic.disable())
			.formLogin(formLogin -> formLogin.disable())
			.logout(logout -> logout.disable());

		http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// 헤더를 확인할 커스텀 필터 추가
		http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		// 토큰 재발급 URL은 인증 없이 접근 가능하도록 설정. 나머지 API URL은 인증 필요
		http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/token")
			.permitAll()
			.requestMatchers("/api/**")
			.authenticated()
			.anyRequest()
			.permitAll());

		http.oauth2Login(oauth2 -> oauth2.loginPage("/login")
			// Authorization 요청과 관련된 상태 저장
			.authorizationEndpoint(authorization -> authorization.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
			.successHandler(oAuth2SuccessHandler())// 인증 성공 시 실행할 핸들러
			.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserCustomService)));

		http.logout(logout -> logout.logoutSuccessUrl("/login"));

		// /api로 시작하는 url인 경우 401 상태 코드를 반환하도록 예외 처리
		http.exceptionHandling(exceptionHandling -> exceptionHandling.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), new AntPathRequestMatcher("/api/**")));

		return http.build();
	}

	@Bean
	public OAuth2SuccessHandler oAuth2SuccessHandler() {
		return new OAuth2SuccessHandler(tokenProvider, tokenRepository, oAuth2AuthorizationRequestBasedOnCookieRepository(), accountService);
	}

	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() {
		return new TokenAuthenticationFilter(tokenProvider);
	}

	@Bean
	public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
		return new OAuth2AuthorizationRequestBasedOnCookieRepository();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}