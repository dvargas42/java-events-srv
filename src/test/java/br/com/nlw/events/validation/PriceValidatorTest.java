package br.com.nlw.events.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PriceValidatorTest {

  private PriceValidator priceValidator;
  private JsonParser jsonParser;
  private DeserializationContext deserializationContext;

  @BeforeEach
  void setUp() {
    priceValidator = new PriceValidator("Invalid price");
    jsonParser = mock(JsonParser.class);
    deserializationContext = mock(DeserializationContext.class);
  }

  @Test
  void shouldDeserializeValidPrice() throws IOException {
    when(jsonParser.getText()).thenReturn("100.0");

    Double result = priceValidator.deserialize(jsonParser, deserializationContext);

    assertNotNull(result);
    assertEquals(100.0, result);
  }

  @Test
  void shouldThrowExceptionForNegativePrice() throws IOException {
    when(jsonParser.getText()).thenReturn("-100.0");

    JsonMappingException exception =
        assertThrows(
            JsonMappingException.class,
            () -> {
              priceValidator.deserialize(jsonParser, deserializationContext);
            });

    assertEquals("Invalid price", exception.getOriginalMessage());
  }

  @Test
  void shouldThrowExceptionForInvalidNumberFormat() throws IOException {
    when(jsonParser.getText()).thenReturn("invalid");

    JsonMappingException exception =
        assertThrows(
            JsonMappingException.class,
            () -> {
              priceValidator.deserialize(jsonParser, deserializationContext);
            });

    assertEquals("For input string: \"invalid\"", exception.getOriginalMessage());
  }

  @Test
  void shouldReturnNullForEmptyValue() throws IOException {
    when(jsonParser.getText()).thenReturn("");

    Double result = priceValidator.deserialize(jsonParser, deserializationContext);

    assertNull(result);
  }

  @Test
  void shouldReturnNullForNullValue() throws IOException {
    when(jsonParser.getText()).thenReturn(null);

    Double result = priceValidator.deserialize(jsonParser, deserializationContext);

    assertNull(result);
  }

  @Test
  void shouldReturnNewPriceValidatorWithAnnotationMessage() {
    BeanProperty property = mock(BeanProperty.class);
    Price priceAnnotation = mock(Price.class);
    when(property.getAnnotation(Price.class)).thenReturn(priceAnnotation);
    when(priceAnnotation.message()).thenReturn("Custom message");

    JsonDeserializer<?> result = priceValidator.createContextual(deserializationContext, property);

    assertNotNull(result);
    assertEquals(PriceValidator.class, result.getClass());
    assertEquals("Custom message", ((PriceValidator) result).getMessage());
  }

  @Test
  void shouldReturnSameInstanceWhenNoAnnotation() {
    BeanProperty property = mock(BeanProperty.class);
    when(property.getAnnotation(Price.class)).thenReturn(null);

    JsonDeserializer<?> result = priceValidator.createContextual(deserializationContext, property);

    assertNotNull(result);
    assertEquals(priceValidator, result);
  }

  @Test
  void shouldReturnSameInstanceWhenPropertyIsNull() {
    JsonDeserializer<?> result = priceValidator.createContextual(deserializationContext, null);

    assertNotNull(result);
    assertEquals(priceValidator, result);
  }
}
