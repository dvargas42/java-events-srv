package br.com.nlw.events.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.dto.EventOut;
import br.com.nlw.events.service.EventService;
import java.time.LocalDate;
import java.time.LocalTime;
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

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EventControllerGetIT {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private EventService eventService;

  private LocalDate startDate = LocalDate.now().plusDays(1);
  private LocalDate endDate = LocalDate.now().plusDays(2);
  private LocalTime startTime = LocalTime.of(19, 9, 9);
  private LocalTime endTime = LocalTime.of(21, 9, 9);
  private String location = "Online";
  private Double price = 0.0;

  @Test
  @Order(1)
  void shouldGetAllEvents() {
    for (int i = 0; i < 10; i++) {
      String title = "Codecraft Summit 202" + i;
      EventIn eventIn = new EventIn(title, location, price, startDate, endDate, startTime, endTime);
      eventService.addNewEvent(eventIn);
    }

    ResponseEntity<EventOut[]> response = restTemplate.getForEntity("/event", EventOut[].class);

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().length).isEqualTo(10);
  }

  @Test
  @Order(2)
  void shouldGetEventsByPrettyName() {
    String title = "Codecraft Summit 2020";
    EventOut eventOut =
        new EventOut(
            1,
            title,
            title.toLowerCase().replace(" ", "-"),
            location,
            price,
            startDate,
            endDate,
            startTime,
            endTime);

    ResponseEntity<EventOut> response =
        restTemplate.getForEntity(
            "/event/{prettyName}", EventOut.class, title.toLowerCase().replace(" ", "-"));

    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(eventOut);
  }

  @Test
  @Order(3)
  void shouldToBeNotAbleToGetEventWhenPrettyIsInvalid() {
    ResponseEntity<EventOut[]> response =
        restTemplate.getForEntity("/event/{prettyName}", EventOut[].class, "invalidPrettyName");

    assertEquals(404, response.getStatusCode().value());
    assertThat(response.getBody()).isNull();
  }
}
