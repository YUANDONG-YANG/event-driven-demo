package com.demo.azure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eventGridServiceBusOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Event Grid + Service Bus sample API")
                        .description("Spring Boot 3.2 demo: study endpoint plus optional Azure Event Grid / Service Bus integration.")
                        .version("0.0.1"));
    }
}
