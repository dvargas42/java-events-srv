package br.com.nlw.events.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

  @Autowired private JavaMailSender emailSender;

  @Value("${spring.mail.username}")
  private String from;

  public void sendEmail(String to, String subject, String content) {
    try {
      SimpleMailMessage email = new SimpleMailMessage();
      email.setFrom(from);
      email.setSubject(subject);
      email.setTo(to);
      email.setText(content);
      emailSender.send(email);
    } catch (Exception ex) {
      throw new RuntimeException("Error sending email", ex);
    }
  }
}
