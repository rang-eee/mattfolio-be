package com.colon.mattfolio.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.colon.mattfolio.api.auth.service.CustomOAuth2UserService;
import com.colon.mattfolio.common.handler.handler.CustomAccessDeniedHandler;
import com.colon.mattfolio.common.handler.handler.CustomAuthenticationEntryPoint;
import com.colon.mattfolio.common.handler.handler.OAuth2FailureHandler;
import com.colon.mattfolio.common.handler.handler.OAuth2SuccessHandler;
import com.colon.mattfolio.common.jwt.TokenAuthenticationFilter;
import com.colon.mattfolio.common.jwt.TokenExceptionFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final CustomOAuth2UserService oAuth2UserService;
        private final OAuth2SuccessHandler oAuth2SuccessHandler;
        private final TokenAuthenticationFilter tokenAuthenticationFilter;

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
                return web -> web.ignoring()
                        .requestMatchers("/error", //
                                        "/favicon.ico", //
                                        // "/v1/**", //
                                        "/swagger-ui.html", //
                                        "/swagger-ui/**", //
                                        "/swagger-resources/**", //
                                        "/apidocs.html", //
                                        "/apidocs", //
                                        "/apidocs/**"//
                        );
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.csrf(AbstractHttpConfigurer::disable)
                        .cors(AbstractHttpConfigurer::disable)
                        .httpBasic(AbstractHttpConfigurer::disable)
                        .formLogin(AbstractHttpConfigurer::disable)
                        .logout(AbstractHttpConfigurer::disable)
                        .headers(c -> c.frameOptions(FrameOptionsConfig::disable)
                                .disable())
                        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                        .authorizeHttpRequests(request -> request.requestMatchers(new AntPathRequestMatcher("/"), new AntPathRequestMatcher("/auth/success"), new AntPathRequestMatcher("/funding-products/**", "GET"), new AntPathRequestMatcher("/notification/subscribe"))
                                .permitAll()
                                .anyRequest()
                                .authenticated())

                        .oauth2Login(oauth -> oauth.userInfoEndpoint(c -> c.userService(oAuth2UserService))
                                .successHandler(oAuth2SuccessHandler)
                                .failureHandler(new OAuth2FailureHandler()))

                        .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                        .addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass())

                        .exceptionHandling((exceptions) -> exceptions.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                                .accessDeniedHandler(new CustomAccessDeniedHandler()));

                return http.build();
        }
}
