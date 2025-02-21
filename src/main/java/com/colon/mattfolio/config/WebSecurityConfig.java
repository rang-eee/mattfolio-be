package com.colon.mattfolio.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.colon.mattfolio.filter.ExtraAuthFilter;
import com.colon.mattfolio.security.AccountAuthenticationProvider;
import com.colon.mattfolio.security.support.RestAuthenticationEntryPoint;
import com.colon.mattfolio.security.support.RestAuthenticationFailureHandler;
import com.colon.mattfolio.security.support.RestAuthenticationSuccessHandler;
import com.colon.mattfolio.security.support.RestLogoutSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

	private final AccountAuthenticationProvider accountAuthenticationProvider;
	private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	private final RestAuthenticationFailureHandler restAuthenticationFailureHandler;
	private final RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
	private final RestLogoutSuccessHandler restLogoutSuccessHandler;
	private final RestTemplateBuilder restTemplateBuilder;

	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(List.of(accountAuthenticationProvider));
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// ExtraAuthFilter 등록
			.addFilterBefore(createCustomFilter(), UsernamePasswordAuthenticationFilter.class)
			.headers(headers -> headers.frameOptions(frame -> frame.disable()))

			.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.GET, "/")
				.permitAll()
				.requestMatchers("/oauth2/**", "/webjars/**")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/api/**")
				.permitAll()
				.requestMatchers(HttpMethod.GET, "/api/**")
				.permitAll()
				.requestMatchers(HttpMethod.PUT, "/api/**")
				.permitAll()
				.requestMatchers(HttpMethod.DELETE, "/api/**")
				.permitAll()
				.requestMatchers("/cert/**")
				.permitAll()
				.requestMatchers("/error")
				.permitAll()
				.requestMatchers("/login")
				.permitAll()
				.anyRequest()
				.authenticated())

			.exceptionHandling(exception -> exception.authenticationEntryPoint(restAuthenticationEntryPoint)
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					throw accessDeniedException;
				}))

			.formLogin(form -> form.successHandler(restAuthenticationSuccessHandler)
				.failureHandler(restAuthenticationFailureHandler))

			.logout(logout -> logout.logoutSuccessHandler(restLogoutSuccessHandler))

			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
			.requestMatchers("/docs/index.html", "/vendors/**", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs", "/apidocs.json/**", "/error", "/webjars/**", "/api/poison-map", "/api/poison-map/**", "/fonts/**");
	}

	@Bean
	@ConditionalOnMissingBean
	public RestTemplate restTemplate() {
		return restTemplateBuilder.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("*");
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

	protected AbstractAuthenticationProcessingFilter createCustomFilter() {
		ExtraAuthFilter filter = new ExtraAuthFilter(new NegatedRequestMatcher(new AndRequestMatcher(new AntPathRequestMatcher("/oauth2/**"))));
		filter.setAuthenticationManager(authenticationManager());
		return filter;
	}
}
