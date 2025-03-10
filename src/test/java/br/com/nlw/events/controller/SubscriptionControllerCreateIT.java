package br.com.nlw.events.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.dto.EventOut;
import br.com.nlw.events.dto.SubscriptionOut;
import br.com.nlw.events.dto.UserIn;
import br.com.nlw.events.error.ApiErrorResponse;
import br.com.nlw.events.service.EventService;
import br.com.nlw.events.service.SubscriptionService;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SubscriptionControllerCreateIT {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private EventService eventService;
  @Autowired private SubscriptionService subscriptionService;

  private LocalDate startDate = LocalDate.now().plusDays(1);
  private LocalDate endDate = LocalDate.now().plusDays(2);
  private LocalTime startTime = LocalTime.of(19, 9, 9);
  private LocalTime endTime = LocalTime.of(21, 9, 9);
  private String title = "Codecraft Summit";
  private String location = "Online";
  private Double price = 0.0;
  String prettyName = title.toLowerCase().replace(" ", "-");

  ResponseEntity<EventOut> eventResponse;
  private EventIn eventIn =
      new EventIn(title, location, price, startDate, endDate, startTime, endTime);

  @BeforeEach
  void setUp() {
    eventService.addNewEvent(eventIn);
  }

  @Test
  void shouldCreateSubscriptionWithOutIndicator() {
    UserIn userIn = new UserIn("Integration Test", "integration.test@test.com");
    ResponseEntity<SubscriptionOut> subscriptionResponse =
        restTemplate.postForEntity("/subscription/" + prettyName, userIn, SubscriptionOut.class);

    if (subscriptionResponse.getBody().equals(null)) {
      fail("User subscription failed");
    }

    Integer actualSubscriptionNumber = subscriptionResponse.getBody().subscriptionNumber();
    SubscriptionOut subscriptionOut =
        new SubscriptionOut(
            1, "http://codecraft.com/subscription/" + prettyName + "/" + actualSubscriptionNumber);

    assertEquals(200, subscriptionResponse.getStatusCode().value());
    assertThat(subscriptionResponse.getBody())
        .usingRecursiveComparison()
        .isEqualTo(subscriptionOut);
  }

  @Test
  void shouldCreateSubscriptionWithIndicator() {
    UserIn userIndicator = new UserIn("User One", "user.one@test.com");
    subscriptionService.createSubscription(prettyName, null, userIndicator);

    UserIn userByIndication = new UserIn("User Two", "user.two@test.com");
    ResponseEntity<SubscriptionOut> createUserByIndicationRes =
        restTemplate.postForEntity(
            "/subscription/" + prettyName + "/1", userByIndication, SubscriptionOut.class);

    if (createUserByIndicationRes.getBody().equals(null)) {
      fail("User by indication subscription failed");
    }

    Integer actualSubscriptionNumber = createUserByIndicationRes.getBody().subscriptionNumber();
    SubscriptionOut subscriptionOut =
        new SubscriptionOut(
            2, "http://codecraft.com/subscription/" + prettyName + "/" + actualSubscriptionNumber);

    assertEquals(200, createUserByIndicationRes.getStatusCode().value());
    assertThat(createUserByIndicationRes.getBody())
        .usingRecursiveComparison()
        .isEqualTo(subscriptionOut);
  }

  static Stream<String> provideInvalidNames() {
    return Stream.of(
        "1; DROP TABLE users; --",
        "<script>alert(\"Hacked!\")</script>",
        "SELECT * FROM users WHERE name = \"admin\"",
        "<img src=\"x\" onerror=\"alert(1)\">",
        " ",
        "");
  }

  @ParameterizedTest
  @MethodSource("provideInvalidNames")
  @NullSource
  void shouldToBeNotAbleToCreateSubscriptionWhenNameIsInvalid(String invalidName) {
    UserIn userIn = new UserIn(invalidName, "test@test.com");
    ResponseEntity<ApiErrorResponse> subscriptionResponse =
        restTemplate.postForEntity("/subscription/" + prettyName, userIn, ApiErrorResponse.class);

    if (subscriptionResponse.getBody() == null) {
      fail("User indicator subscription failed");
    }

    List<String> expectedMessages =
        List.of(
            "The field 'name' must not have SQL",
            "The field 'name' must not have HTML tags",
            "The field 'name' must not be null or blank");

    assertEquals(400, subscriptionResponse.getStatusCode().value());
    assertTrue(
        expectedMessages.contains(
            subscriptionResponse.getBody().getValidationErrors().getFirst().message()));
  }

  static Stream<String> provideValidEmails() {
    return Stream.of(
        "email@example.com",
        "user.name@example.com",
        "user-name@example.co.uk",
        "user_name@example.io",
        "user+tag@example.com",
        "user123@example.com",
        "123user@example.org",
        "user@example.travel",
        "email@sub.domain.com",
        "email@domain-name.com",
        "email@xn--example-9db.com", // Domínio IDN válido
        "usuario@exámple.com", // Domínio com caracteres acentuados (quando suportado)
        "测试@例子.测试", // Exemplo de e-mail internacionalizado em chinês
        "email@bücher.de" // Domínio com caracteres especiais (IDN real)
        );
  }

  @ParameterizedTest
  @MethodSource("provideValidEmails")
  void shouldCreateSubscriptionWhenEmailIsValid(String validEmail) {
    UserIn userIn = new UserIn("Integration Test", validEmail);
    ResponseEntity<ApiErrorResponse> subscriptionResponse =
        restTemplate.postForEntity("/subscription/" + prettyName, userIn, ApiErrorResponse.class);

    if (subscriptionResponse.getBody() == null) {
      fail("User subscription failed");
    }

    assertEquals(200, subscriptionResponse.getStatusCode().value());
  }

  static Stream<String> provideInvalidEmails() {
    return Stream.of(
        ".email@example.com",
        "email..example@example.com",
        "email@example..com",
        "email@example.com.",
        "email@.example.com",
        "email@ex..ample.com",
        "email@example",
        "email@exa_mple.com",
        "email@-example.com",
        "email@example-.com",
        "email@example.c",
        "email@123.123.123.123",
        "email@.123.com",
        "email@ex+ample.com",
        "email@example..com",
        "email.@example.com",
        "ema il@example.com",
        "email@ex_ample.com",
        "email@ex!ample.com",
        "@example.com",
        "email@",
        "email@example..com");
  }

  @ParameterizedTest
  @MethodSource("provideInvalidEmails")
  @NullSource
  void shouldToBeNotAbleToCreateSubscriptionWhenEmailIsInvalid(String invalidEmail) {
    UserIn userIn = new UserIn("Integration Test", invalidEmail);
    ResponseEntity<ApiErrorResponse> subscriptionResponse =
        restTemplate.postForEntity("/subscription/" + prettyName, userIn, ApiErrorResponse.class);
    if (subscriptionResponse.getBody() == null) {
      fail();
    }

    List<String> expectedMessages =
        List.of("The field 'email' must be valid", "The field 'email' must not be null or blank");

    assertEquals(400, subscriptionResponse.getStatusCode().value());
    assertTrue(
        expectedMessages.contains(
            subscriptionResponse.getBody().getValidationErrors().getFirst().message()));
  }

  @Test
  void shouldNotCreateSubscriptionWhenEventNotExists() {
    UserIn userIn = new UserIn("Integration Test", "integration.test@test.com");
    String invalidPrettyName = prettyName + "-invalid";
    ResponseEntity<ApiErrorResponse> subscriptionResponse =
        restTemplate.postForEntity(
            "/subscription/" + invalidPrettyName, userIn, ApiErrorResponse.class);

    if (subscriptionResponse.getBody() == null) {
      fail("User subscription with event not found failed");
    }

    String expectedMessage = "Event " + invalidPrettyName + " not found.";

    assertEquals(404, subscriptionResponse.getStatusCode().value());
    assertThat(subscriptionResponse.getBody().getErrorMessage()).isEqualTo(expectedMessage);
  }

  @Test
  void shouldNotCreateSubscriptionWhenIndicatorUserNotExists() {
    UserIn userIn = new UserIn("Integration Test", "integration.test@test.com");
    int userId = 2_000;
    ResponseEntity<ApiErrorResponse> subscriptionResponse =
        restTemplate.postForEntity(
            "/subscription/" + prettyName + "/" + userId, userIn, ApiErrorResponse.class);

    if (subscriptionResponse.getBody() == null) {
      fail("User subscription with user indicator failed");
    }

    String expectedMessage = "User indicator " + userId + " not found.";

    assertEquals(404, subscriptionResponse.getStatusCode().value());
    assertThat(subscriptionResponse.getBody().getErrorMessage()).isEqualTo(expectedMessage);
  }

  @Test
  void shouldCreateSubscriptionWhenUserAlreadyExists() {
    String email = "integration.test@test.com";
    UserIn userIn = new UserIn("Integration Test", email);

    ResponseEntity<ApiErrorResponse> subscriptionResponse = null;
    for (int i = 0; i < 2; i++) {
      subscriptionResponse =
          restTemplate.postForEntity("/subscription/" + prettyName, userIn, ApiErrorResponse.class);
    }

    if (subscriptionResponse.getBody() == null) {
      fail("User subscription with user indicator failed");
    }

    String expectedMessage = "There is already a registration for the user " + email + " in event " + title;

    assertEquals(409, subscriptionResponse.getStatusCode().value());
    assertThat(subscriptionResponse.getBody().getErrorMessage()).isEqualTo(expectedMessage);
  }
}
