package br.com.nlw.events.validation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateValidator extends JsonDeserializer<LocalDate> implements ContextualDeserializer {

  private String pattern;
  private String message;

  public DateValidator() {}

  public DateValidator(String pattern, String message) {
    this.pattern = pattern;
    this.message = message;
  }

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
    if (property != null) {
      Date annotation = property.getAnnotation(Date.class);
      if (annotation != null) {
        return new DateValidator(annotation.pattern(), annotation.message());
      }
    }
    return this;
  }

  @Override
  public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    try {
      String value = p.getText();
      if (value == null || value.isEmpty()) {
        return null;
      }
      return LocalDate.parse(value, DateTimeFormatter.ofPattern(this.pattern));
    } catch (Exception ex) {
      throw JsonMappingException.from(p, this.message);
    }
  }
}
