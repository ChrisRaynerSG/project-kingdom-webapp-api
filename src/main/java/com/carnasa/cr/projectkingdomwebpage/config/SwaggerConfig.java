package com.carnasa.cr.projectkingdomwebpage.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Info;

/**
 * SwaggerConfig is a configuration class for setting up Swagger/OpenAPI documentation.
 * It defines a bean to customize the OpenAPI configuration, including API metadata,
 * security schemes, and global security requirements.
 *
 * The configuration is used for generating API documentation and enabling interactive
 * API exploration via Swagger-UI or compatible tools.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kingdom Of Mine API")
                        .description("Documentation and functionality of the API for " +
                                " Kingdom Of Mine webapp.")
                        .version("1.0.0"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}