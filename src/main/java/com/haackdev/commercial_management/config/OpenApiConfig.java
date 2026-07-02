package com.haackdev.commercial_management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI commerceFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CommerceFlow API")
                        .description("API REST do Sistema de Gestão CommerceFlow para controle comercial.")
                        .version("v1.0.0"));
    }
}
