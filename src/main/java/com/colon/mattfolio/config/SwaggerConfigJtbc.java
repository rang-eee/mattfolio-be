// package com.colon.mattfolio.config;

// import java.util.Arrays;

// import org.springdoc.core.customizers.OpenApiCustomizer;
// import org.springdoc.core.models.GroupedOpenApi;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpHeaders;

// import com.colon.mattfolio.model.common.EnvironmentDto;

// import io.swagger.v3.oas.models.OpenAPI;
// import io.swagger.v3.oas.models.info.Info;
// import io.swagger.v3.oas.models.security.SecurityRequirement;
// import io.swagger.v3.oas.models.security.SecurityScheme;
// import io.swagger.v3.oas.models.servers.Server;

// /**
// * Swagger를 위한 Config 클래스
// */
// // @Configuration
// public class SwaggerConfigJtbc {

// @Value("${server.url}")
// private String serverUrl;

// /**
// * 로그인 인증이 필요한 path list
// */
// @Value("${system.logincheck.url}")
// private String[] systemLogincheckUrl;

// @Bean
// public GroupedOpenApi securityGroupOpenApiV0() {
// return GroupedOpenApi.builder()
// .group("V0 - Security Open Api")
// .pathsToMatch("/v0/**")
// .pathsToExclude("/v1/**")
// .pathsToMatch(systemLogincheckUrl)
// .addOpenApiCustomizer(buildSecurityOpenApi())
// .build();
// }

// @Bean
// public GroupedOpenApi nonSecurityGroupOpenApiV0() {
// return GroupedOpenApi.builder()
// .group("V0 - Non Security Open Api")
// .pathsToMatch("/v0/**")
// .pathsToExclude("/v1/**")
// .pathsToExclude(systemLogincheckUrl)
// .build();
// }

// @Bean
// public GroupedOpenApi securityGroupOpenApiV1() {
// return GroupedOpenApi.builder()
// .group("V1 - Security Open Api")
// .pathsToMatch("/v1/**")
// .pathsToExclude("/v0/**")
// .pathsToMatch(systemLogincheckUrl)
// .addOpenApiCustomizer(buildSecurityOpenApi())
// .build();
// }

// @Bean
// public GroupedOpenApi nonSecurityGroupOpenApiV1() {
// return GroupedOpenApi.builder()
// .group("V1 - Non Security Open Api")
// .pathsToMatch("/v1/**")
// .pathsToExclude("/v0/**")
// .pathsToExclude(systemLogincheckUrl)
// .build();
// }

// @Bean
// public GroupedOpenApi securityGroupOpenApiV2() {
// return GroupedOpenApi.builder()
// .group("V2 - Security Open Api")
// .pathsToMatch("/v2/**")
// .pathsToExclude("/v0/**")
// .pathsToExclude("/v1/**")
// .pathsToMatch(systemLogincheckUrl)
// .addOpenApiCustomizer(buildSecurityOpenApi())
// .build();
// }

// @Bean
// public GroupedOpenApi nonSecurityGroupOpenApiV2() {
// return GroupedOpenApi.builder()
// .group("V2 - Non Security Open Api")
// .pathsToMatch("/v2/**")
// .pathsToExclude("/v0/**")
// .pathsToExclude("/v1/**")
// .pathsToExclude(systemLogincheckUrl)
// .build();
// }

// @Bean
// OpenAPI openAPI(EnvironmentDto config) {

// String title = config.getString("springdoc.swagger-ui.title");
// String description = config.getString("springdoc.swagger-ui.description");
// String apiVersion = config.getString("springdoc.swagger-ui.apiVersion");

// OpenAPI openAPI = new OpenAPI();
// Info info = new Info();

// info.setTitle(title);
// info.setDescription(description);
// info.setVersion(apiVersion);

// openAPI.setInfo(info);

// Server server = new Server();
// server.setUrl(serverUrl);
// openAPI.servers(Arrays.asList(server));

// return openAPI;
// }

// public OpenApiCustomizer buildSecurityOpenApi() {
// SecurityScheme securityScheme = new SecurityScheme().name(HttpHeaders.AUTHORIZATION)
// .type(SecurityScheme.Type.HTTP)
// .in(SecurityScheme.In.HEADER)
// .bearerFormat(HttpHeaders.AUTHORIZATION)
// .scheme("bearer");

// return openApi -> openApi.addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
// .getComponents()
// .addSecuritySchemes(HttpHeaders.AUTHORIZATION, securityScheme);
// }
// }