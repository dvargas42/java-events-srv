package br.com.nlw.events.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(information());
    }

    private Info information() {
        return new Info().title("Events Backend")
                .description("API responsible for managing events end user subscriptions")
                .version("1");
    }
}
