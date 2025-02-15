package com.carnasa.cr.projectkingdomwebpage.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kingdom Of Mine API")
                        .description("Documentation and functionality of the API for " +
                                " Kingdom Of Mine webapp.")
                        .version("1.0.0"));
    }
}