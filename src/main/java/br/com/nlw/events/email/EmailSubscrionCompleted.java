package br.com.nlw.events.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailSubscrionCompleted {

  private static final Logger logger = LoggerFactory.getLogger(EmailSubscrionCompleted.class);

  private final EmailService emailService;

  public EmailSubscrionCompleted(EmailService emailservice) {
    this.emailService = emailservice;
  }

  @Async("asyncExecutor")
  public void execute(String to, String subject, String content) {
    emailService.sendEmail(to, subject, content);
    logger.info("Email thread " + Thread.currentThread().getName());
  }
}
