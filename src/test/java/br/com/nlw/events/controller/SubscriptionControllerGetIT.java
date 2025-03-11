package br.com.nlw.events.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import br.com.nlw.events.config.TestCacheConfig;
import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.dto.SubscriptionOut;
import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.UserIn;
import br.com.nlw.events.error.ApiErrorResponse;
import br.com.nlw.events.service.EventService;
import br.com.nlw.events.service.SubscriptionService;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.main.allow-bean-definition-overriding=true")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringJUnitConfig(TestCacheConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SubscriptionControllerGetIT {

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
  private EventIn eventIn =
      new EventIn(title, location, price, startDate, endDate, startTime, endTime);

  // ResponseEntity<EventOut> eventResponse;

  @BeforeEach
  void setUp() {
    eventService.addNewEvent(eventIn);
    createIndicatorUsers();
    createSubscriptionUsers();
  }

  private void createIndicatorUsers() {
    for (int i = 1; i <= 4; i++) {
      UserIn indicatorUser =
          new UserIn("Integration Test" + i, "integration.test" + i + "@test.com");
      restTemplate.postForEntity(
          "/subscription/" + prettyName, indicatorUser, SubscriptionOut.class);
    }
  }

  private void createSubscriptionUsers() {
    int max = 0;
    for (int i = 1; i <= 4; i++) {
      for (int j = (10 * i); j < (10 * (i + 1) - max); j++) {
        UserIn user = new UserIn("Integration Test" + j, "integration.test" + j + "@test.com");
        restTemplate.postForEntity(
            "/subscription/" + prettyName + "/" + i, user, SubscriptionOut.class);
      }
      max++;
    }
  }

  @Test
  @Order(4)
  void shouldGetThreeRanking() {
    ResponseEntity<SubscriptionRankingItem[]> rankingResponse =
        restTemplate.getForEntity(
            "/subscription/{prettyName}/ranking", SubscriptionRankingItem[].class, prettyName);
    if (rankingResponse.getBody() == null) {
      fail("Response body is null");
    }

    assertEquals(200, rankingResponse.getStatusCode().value());
    assertEquals(3, rankingResponse.getBody().length);
  }

  @Test
  @Order(3)
  void shouldGetRankingWhenEventNotExists() {
    String invalidPrettyName = prettyName + "-invalid";
    ResponseEntity<ApiErrorResponse> response =
        restTemplate.getForEntity(
            "/subscription/{prettyName}/ranking", ApiErrorResponse.class, invalidPrettyName);

    if (response.getBody() == null) {
      fail("Response body is null");
    }
    String expectedMessage = "Ranking of event " + invalidPrettyName + " not found.";

    assertEquals(404, response.getStatusCode().value());
    assertEquals(expectedMessage, response.getBody().getErrorMessage());
  }

  @Test
  @Order(1)
  void shouldGetRankingByUserWhenUserNotHaveIndications() {
    UserIn indicatorUser = new UserIn("Integration Test", "integration.test@test.com");
    SubscriptionOut subscription =
        subscriptionService.createSubscription(prettyName, null, indicatorUser);

    int userId = subscription.subscriptionNumber();
    ResponseEntity<ApiErrorResponse> response =
        restTemplate.getForEntity(
            "/subscription/{prettyName}/ranking/{userId}",
            ApiErrorResponse.class,
            prettyName,
            String.valueOf(userId));

    if (response.getBody() == null) {
      fail("Response body is null");
    }
    String expectedMessage = "There are no entries indicating the user " + userId;

    assertEquals(404, response.getStatusCode().value());
    assertEquals(expectedMessage, response.getBody().getErrorMessage());
  }

  @Test
  @Order(2)
  void shouldGetRankingByUser() {
    ResponseEntity<SubscriptionRankingByUser> response =
        restTemplate.getForEntity(
            "/subscription/{prettyName}/ranking/{userId}",
            SubscriptionRankingByUser.class,
            title.toLowerCase().replace(" ", "-"),
            1);
    if (response.getBody() == null) {
      fail("Response body is null");
    }
    SubscriptionRankingByUser rankingByUser =
        new SubscriptionRankingByUser(new SubscriptionRankingItem(1, "Integration Test1", 10L), 1);

    assertEquals(200, response.getStatusCode().value());
    assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(rankingByUser);
  }
}
