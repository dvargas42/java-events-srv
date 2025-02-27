package br.com.nlw.events.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.dto.EventOut;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class EventControllerGetTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime ;
    private String title;
    private String location;
    private Double price ;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.now().plusDays(1);
        endDate = LocalDate.now().plusDays(2);
        startTime = LocalTime.of(19, 9, 9);
        endTime = LocalTime.of(21, 9, 9);
        title = "Codecraft Summit " + System.currentTimeMillis();
        location = "Online";
        price = 0.0;
    }

    @Test
    void shouldToBeAbleToGetAllEvents() {
        for (int i = 0; i < 10; i++) {
            String title = "Codecraft Summit 202" + i;
            EventIn eventIn = new EventIn(title, location, price, startDate, endDate, startTime, endTime);
            restTemplate.postForEntity("/event", eventIn, String.class);
        }
        ResponseEntity<EventOut[]> response = restTemplate.getForEntity("/event", EventOut[].class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(10);
    }

    @Test
    void shouldToBeAbleToGetEventsByPrettyName() {
        EventIn eventIn = new EventIn(title, location, price, startDate, endDate, startTime, endTime);
        EventOut eventOut = new EventOut(1, title, title.toLowerCase().replace(" ", "-"),
                location, price, startDate, endDate, startTime, endTime);

        restTemplate.postForEntity("/event", eventIn, String.class);
        ResponseEntity<EventOut> response = restTemplate.getForEntity(
                "/event/{prettyName}", EventOut.class, title.toLowerCase().replace(" ", "-"));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(eventOut);
    }

    @Test
    void shouldToBeNotAbleToGetEventWhenPrettyIsInvalid() {
        EventIn eventIn = new EventIn(title, location, price, startDate, endDate, startTime, endTime);

        restTemplate.postForEntity("/event", eventIn, String.class);
        ResponseEntity<EventOut[]> response = restTemplate.getForEntity(
                "/event/{prettyName}", EventOut[].class, "invalidPrettyName");

        assertEquals( 404, response.getStatusCode().value());
        assertThat(response.getBody()).isNull();
    }
}
