package com.skthon.sixthsensebe.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Value("${server.servlet.context-path:}")
  private String contextPath;

  @Bean
  public OpenAPI customOpenAPI() {
    String securitySchemeName = "bearerAuth";

    // 로컬 서버
    Server localServer = new Server();
    localServer.setUrl("http://localhost:8080");
    localServer.setDescription("🛠️ 로컬 서버");

    // 운영 서버
    Server prodServer = new Server();
    prodServer.setUrl("https://api.dasijob.site");
    prodServer.setDescription("🚀 운영 서버");

    return new OpenAPI()
        .addServersItem(localServer)
        .addServersItem(prodServer)
        .info(new Info().title("Swagger API 명세서").version("1.0").description("SKTHON-sixthsense"))
        // Bearer 토큰 인증 설정
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
        .components(
            new Components()
                .addSecuritySchemes(
                    securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }

  @Bean
  public GroupedOpenApi customGroupedOpenApi() {
    return GroupedOpenApi.builder().group("api").pathsToMatch("/**").build();
  }
}
