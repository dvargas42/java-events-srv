package br.com.nlw.events.config;

import com.theokanning.openai.service.OpenAiService;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

  @Value("${openai.api.key}")
  public String apiKey;

  @Bean
  public OpenAiService openAiService() {
    return new OpenAiService(apiKey, Duration.ofSeconds(120)); // 2 minutes
  }
}
