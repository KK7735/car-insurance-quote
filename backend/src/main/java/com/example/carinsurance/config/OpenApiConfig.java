package com.example.carinsurance.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Swagger/OpenAPI の設定クラス。グローバルなセキュリティ要件 (SecurityRequirement) を設定し、Swagger UI で保護された Admin API をテストする際に "Authorize" ボタンからトークンを注入できるようにしている。
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "BearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("自動車保険見積サイト API")
                        .version("1.0.0")
                        .description("自動車保険見積サイトのバックエンドAPIドキュメント"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
