package br.com.nlw.events.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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

import br.com.nlw.events.dto.ErrorMessage;
import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.dto.EventOut;
import br.com.nlw.events.dto.SubscriptionOut;
import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.UserIn;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SubscriptionControllerGetTest {
    
    @Autowired
    private TestRestTemplate restTemplate;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String title;
    private String location;
    private Double price;
    private EventIn eventIn;
    ResponseEntity<EventOut> eventResponse;
    String prettyName;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.now().plusDays(1);
        endDate = LocalDate.now().plusDays(2);
        startTime = LocalTime.of(19, 9, 9);
        endTime = LocalTime.of(21, 9, 9);
        title = "Codecraft Summit 2025";
        location = "Online";
        price = 0.0;

        eventIn = new EventIn(title, location, price, startDate, endDate, startTime, endTime);

        eventResponse = restTemplate.postForEntity("/event", eventIn, EventOut.class);
        if (eventResponse.getBody() == null) {
            fail();
        }
        prettyName = eventResponse.getBody().prettyName();
        int max = 0;

        for (int i = 1; i <= 4; i++) {
            UserIn indicatorUser = new UserIn("Integration Test" + i, "integration.test" + i + "@test.com");
            restTemplate.postForEntity("/subscription/" + prettyName, indicatorUser, SubscriptionOut.class);
        }

        for (int i = 1; i <= 4; i++) {
            for (int j = (10 * i); j < (10 * (i + 1) - max); j++) {
                UserIn user = new UserIn("Integration Test" + j, "integration.test" + j + "@test.com");
                restTemplate.postForEntity("/subscription/" + prettyName + "/" + i, user, SubscriptionOut.class);
            }
            max++;
        }
    }

    @Test
    void shouldToBeAbleToGetRanking() {
        ResponseEntity<SubscriptionRankingItem[]> response = restTemplate.getForEntity(
                "/subscription/{prettyName}/ranking", 
                SubscriptionRankingItem[].class, 
                title.toLowerCase().replace(" ", "-"));
        if (response.getBody() == null) {
            fail();
        }
        assertEquals(200, response.getStatusCode().value());
        assertEquals(3, response.getBody().length);
    }

    @Test
    void shouldToBeNotAbleToGetRankingWhenEventNotExists() {
        String invalidPrettyName = title.toLowerCase().replace(" ", "-") + "1";
        ResponseEntity<ErrorMessage> response = restTemplate.getForEntity(
                "/subscription/{prettyName}/ranking", 
                ErrorMessage.class, 
                invalidPrettyName);
        if (response.getBody() == null) {
            fail();
        }
        String expectedMessage = "Ranking do evento " + invalidPrettyName + " não existe.";
        assertEquals(404, response.getStatusCode().value());
        assertEquals(expectedMessage, response.getBody().message());
    }
    
    @Test
    void shouldToBeAbleToGetRankingByUser() {
        ResponseEntity<SubscriptionRankingByUser> response = restTemplate.getForEntity(
                "/subscription/{prettyName}/ranking/{userId}", 
                SubscriptionRankingByUser.class, 
                title.toLowerCase().replace(" ", "-"),
                1);
        if (response.getBody() == null) {
            fail();
        }
        SubscriptionRankingByUser rankingByUser = new SubscriptionRankingByUser(
                new SubscriptionRankingItem(1, "Integration Test1", 10L), 
                1);

        assertEquals(200, response.getStatusCode().value());
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(rankingByUser);
    }

    @Test
    void shouldToBeAbleToGetRankingByUserWhenUserNotHaveIndications() {
        UserIn indicatorUser = new UserIn("Integration Test", "integration.test@test.com");
        var createUserRes = restTemplate.postForEntity("/subscription/" + prettyName, indicatorUser, SubscriptionOut.class);
        if (createUserRes.getBody() == null) {
            fail();
        }
        int userId = createUserRes.getBody().subscriptionNumber();
        ResponseEntity<ErrorMessage> response = restTemplate.getForEntity(
            "/subscription/{prettyName}/ranking/{userId}", 
            ErrorMessage.class, 
            prettyName, String.valueOf(userId));
        if (response.getBody() == null) {
            fail();
        }
        String expectedMessage = "Não há inscrições com indicação para o usuario " + userId;
        
        assertEquals(404, response.getStatusCode().value());
        assertEquals(expectedMessage, response.getBody().message());
    }
    
}
