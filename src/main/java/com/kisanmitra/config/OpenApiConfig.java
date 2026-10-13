package com.kisanmitra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI kisanMitraOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KisanMitra AI — Full Stack Agricultural Advisory API")
                        .description("REST API documentation for KisanMitra AI decision support platform: " +
                                "Crop Recommendation, What-If Decision Lab, Weather & Climate, " +
                                "Market Price Intelligence, Government Schemes, Disease Guidance, Supply-Demand, and AI Assistant.")
                        .version("1.0.0")
                        .contact(new Contact().name("KisanMitra AI Team").email("support@kisanmitra.ai"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, createSecurityScheme()));
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Provide valid JWT bearer token obtained via POST /api/auth/login or /api/auth/register");
    }
}
