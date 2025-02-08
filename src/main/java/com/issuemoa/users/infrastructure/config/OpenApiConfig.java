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
                .description("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJncGZtMzEyQGdtYWlsLmNvbSIsImlhdCI6MTczODk4NDE0MiwiZXhwIjozNzM4OTg3NzQyLCJzdWIiOiJkZXZAaXNzdWVtb2Eua3IiLCJpZCI6NywibmFtZSI6Iu2FjOyKpO2EsCJ9.oBxyCqB5a-6hkbGCG9QMBjbxStT-myh5AEOSDUzReTffTEd5r279ntLwMv84KC3AIkgo5sfJTQjtKBQ5IN5XEA");

        SecurityScheme apiKeyScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .scheme("api-key")
                .name("X-Client-Key")
                .in(SecurityScheme.In.HEADER)
                .description("SamQHPleQjbSKeyRvJWElcHJvamVjdCFA");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Authorization")
                .addList("X-Client-Key");

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
