package br.com.nlw.events.validation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;

public class PriceValidator extends JsonDeserializer<Double> implements ContextualDeserializer {

    private String message;

    public PriceValidator() { }

    public PriceValidator( String message) {
        this.message = message;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        if (property != null) {
            Price annotation = property.getAnnotation(Price.class);
            if (annotation != null) {
                return new PriceValidator(annotation.message());
            }
        }
        return this;
    }

    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            String value = p.getText();
            if (value == null || value.isEmpty()) {
                return null;
            }
            double price = Double.parseDouble(value);
            if (price < 0.0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            return price > 0 ? price : -price;
        } catch (IllegalArgumentException ex) {
            throw JsonMappingException.from(p, this.message);
        }
    }
}

