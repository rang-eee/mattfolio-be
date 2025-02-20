package com.colon.mattfolio.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.colon.mattfolio.property.AppProperties;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Profile({ "local", "prd" })
@Configuration
public class SwaggerConfig {

	private final AppProperties appProperties;

	public SwaggerConfig(AppProperties appProperties) {
		this.appProperties = appProperties;
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()//
			.components(new Components().addSecuritySchemes("spring_oauth", new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
				.flows(new OAuthFlows().password(new OAuthFlow().tokenUrl("/oauth/token")
					.scopes(new Scopes().addString("any", "for any operations"))))))
			.addSecurityItem(new SecurityRequirement().addList("spring_oauth"))
			.info(new Info().title(appProperties.getName())
				.version("2.0.0")
				.description("아워홈 Qsis api 정보")
				.termsOfService("https://qsis.ourhome.co.kr")
				.contact(new Contact().name("아워홈")
					.url("https://www.ourhome.co.kr")
					.email("example@gmail.com"))
				.license(new License().name("License")
					.url("License URL")));
	}

	@Bean
	public GroupedOpenApi api() {
		return GroupedOpenApi.builder()
			.group("default")
			.pathsToMatch("/api/**")
			.build();
	}

	@Bean
	public GroupedOpenApi v1Api() {
		return GroupedOpenApi.builder()
			.group("default_new")
			.pathsToMatch("/v1/api/**")
			.addOperationCustomizer((operation, handlerMethod) -> {
				// global header parameter "menuId" 추가 (Swagger2의 globalRequestParameters와 유사)
				Parameter headerParameter = new Parameter().name("menuId")
					.in("header")
					.description("@AuthCheck 사용 시 Menu ID 필요")
					.required(false)
					.schema(new StringSchema());
				operation.addParametersItem(headerParameter);
				return operation;
			})
			.build();
	}
}
