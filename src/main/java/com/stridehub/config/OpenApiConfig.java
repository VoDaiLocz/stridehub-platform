package com.stridehub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI stridehubOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("StrideHub API")
                        .version("0.0.1")
                        .description("Runtime foundation contract for the StrideHub modular monolith."));
    }
}
