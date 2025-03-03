package com.colon.mattfolio.common.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;

@SecurityScheme(name = "spring_oauth", // Swagger UI에서 사용할 보안 스키마 이름
        type = SecuritySchemeType.OAUTH2, //
        flows = @OAuthFlows( //
                authorizationCode = @OAuthFlow(//
                        authorizationUrl = "/oauth2/authorize", // 실제 OAuth2 인증 서버의 URL로 변경
                        tokenUrl = "/oauth2/token", // 실제 토큰 발급 URL로 변경
                        scopes = { //
                                @OAuthScope(name = "any", description = "for any operations")//
                        }//
                )//

        )//
)
@Configuration
@AllArgsConstructor
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
            .group("v1 API")
            // .pathsToMatch("/v1/api/**")
            .pathsToMatch("/**/api/**")
            .build();
    }
}
