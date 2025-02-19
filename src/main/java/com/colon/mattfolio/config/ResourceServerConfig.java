package com.colon.mattfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.GET, "/api/**")
            .permitAll()
            .requestMatchers(HttpMethod.POST, "/api/**")
            .permitAll()
            .requestMatchers(HttpMethod.PUT, "/api/**")
            .permitAll()
            .requestMatchers(HttpMethod.DELETE, "/api/**")
            .permitAll()
            .anyRequest()
            .authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
            }) // JWT 인증 적용
            );

        return http.build();
    }
}
