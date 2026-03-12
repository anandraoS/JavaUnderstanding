package com.learning.common_library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) Configuration
 * Demonstrates: API documentation with Swagger/OpenAPI
 *
 * Only activates when springdoc/swagger classes are on classpath.
 * The api-gateway (WebFlux) won't load this since it excludes WebMVC springdoc.
 */
@Configuration
@ConditionalOnClass(name = "io.swagger.v3.oas.models.OpenAPI")
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservices Learning API")
                        .version("1.0")
                        .description("Comprehensive Spring Boot Microservices Architecture demonstrating all enterprise patterns")
                        .contact(new Contact()
                                .name("Learning Team")
                                .email("learning@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}

