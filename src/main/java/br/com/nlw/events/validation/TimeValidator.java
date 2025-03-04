package br.com.nlw.events.validation;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import br.com.nlw.events.exception.TimeFormatException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

public class TimeValidator extends JsonDeserializer<LocalTime> implements ContextualDeserializer {

    private String pattern;
    private String message;

    public TimeValidator() { }

    public TimeValidator(String pattern, String message) {
        this.pattern = pattern;
        this.message = message;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        if (property != null) {
            Time annotation = property.getAnnotation(Time.class);
            if (annotation != null) {
                return new TimeValidator(annotation.pattern(), annotation.message());
            }
        }
        return this;
    }

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            String value = p.getText();
            if (value == null || "".equals(value)) {
                return null;
            }

            if (pattern.length() >= 2 && Integer.parseInt(value.substring(0,2)) > 23) {
                throw new TimeFormatException("Invalid value for hour (valid values 00 - 23)");
            }
            if (pattern.length() > 3 && Integer.parseInt(value.substring(3,5)) > 59) {
                throw new TimeFormatException("Invalid value for minutes (valid values 00 - 59)");
            }
            if (pattern.length() > 6 && Integer.parseInt(value.substring(6,8)) > 59) {
                throw new TimeFormatException("Invalid value for seconds (valid values 00 - 59)");
            }
            return LocalTime.parse(value, DateTimeFormatter.ofPattern(this.pattern));
        } catch (TimeFormatException ex) {
            throw JsonMappingException.from(p, ex.getMessage());
        } catch (Exception ex) {
            throw JsonMappingException.from(p, this.message);
        }
    }
}