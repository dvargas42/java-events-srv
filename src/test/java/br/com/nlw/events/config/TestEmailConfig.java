package br.com.nlw.events.config;

import br.com.nlw.events.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestEmailConfig {

  private static final Logger logger = LoggerFactory.getLogger(TestEmailConfig.class);

  @Bean
  EmailService emailService() {
    return new EmailService() {
      @Override
      public void sendEmail(String to, String subject, String content) {
        logger.info("TEEEEEEEST - mock email sent to {}", to);
      }
    };
  }
}
