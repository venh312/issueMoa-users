package com.issuemoa.users.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Define the server URL
        Server server = new Server().url("/");

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .name("Authorization")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .description("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjb25mMzEyQGtha2FvLmNvbSIsIm5hbWUiOiLslYzsiJjsl4bsnYwiLCJpZCI6NCwiYXV0aCI6IklTU1VFTU9BX1VTRVIiLCJleHAiOjE3MDQ5NzkzMjl9.gzXnASGdtoiVQgh57cJqnxREkVGZTFvade8ppCb_yTQKfTMNLashMxD5cZ8FvHPMFTNcC0arvXbQ-vqIxViyvQ");

        SecurityScheme apiKeyScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .scheme("api-key")
                .name("X-Client-Key")
                .in(SecurityScheme.In.HEADER)
                .description("SamQHPleQjbSKeyRvJWElcHJvamVjdCFA");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Authorization")
                .addList("X-Client-Key");

        // Create securityRequirements object
        return new OpenAPI()
            .info(new Info().title("사용자 인증∙인가 API").version("1.0"))
            .addServersItem(server)
            .addSecurityItem(securityRequirement)
            .components(new Components()
                .addSecuritySchemes("Authorization", securityScheme)
                .addSecuritySchemes("X-Client-Key", apiKeyScheme)
            );
    }
}
