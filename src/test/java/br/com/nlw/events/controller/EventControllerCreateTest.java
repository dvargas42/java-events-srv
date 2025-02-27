package br.com.nlw.events.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import br.com.nlw.events.error.ApiErrorResponse;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class EventControllerCreateTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime ;
    private String title;
    private String location;
    private Double price ;
    private EventIn eventIn;

    @BeforeEach 
    void setUp() {
        startDate = LocalDate.now().plusDays(1);
        endDate = LocalDate.now().plusDays(2);
        startTime = LocalTime.of(19, 9, 9);
        endTime = LocalTime.of(21, 9, 9);
        title = "Codecraft Summit " + System.currentTimeMillis(); // Garante unicidade
        location = "Online";
        price = 0.0;

        eventIn = new EventIn(title, location, price, startDate, endDate, startTime, endTime);
    }

    @Test
    @SuppressWarnings("null")
    void shouldToBeAbleToCreateEvent() {
        EventOut eventOut = new EventOut(1,title, title.toLowerCase().replace(" ", "-"),
                location, price, startDate, endDate, startTime, endTime);
        ResponseEntity<EventOut> response = restTemplate.postForEntity("/event", eventIn, EventOut.class);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getHeaders());
        assertThat(response.getHeaders().get("location")).isNotEmpty();
        assertThat(response.getHeaders().get("location").get(0)).endsWith("/event/1");
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(eventOut);
    }

    @Test
    void shouldToBeNotAbleToCreateEventWhenAlreadyExists() {
        restTemplate.postForEntity("/event", eventIn, EventOut.class);
        ResponseEntity<String> response = restTemplate.postForEntity( "/event", eventIn, String.class);

        assertEquals(409, response.getStatusCode().value());
        assertThat(response.getBody()).contains("Já existe cadastro para o evento " + eventIn.title());
    }

    static Stream<String>provideInvalidEntries() {
        return Stream.of(
                "1; DROP TABLE users; --",
                "<script>alert(\"Hacked!\")</script>",
                "SELECT * FROM users WHERE name = \"admin\"",
                "<img src=\"x\" onerror=\"alert(1)\">",
                " ",
                ""
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidEntries")
    @NullSource
    void shouldToBeNotAbleToCreateEventWhenTitleIsInvalid(String invalidTitle) {
        EventIn eventIn = new EventIn(invalidTitle, location, price, startDate, endDate, startTime, endTime);
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity("/event", eventIn, ApiErrorResponse.class);

        List<String> expectedMessages = List.of(
            "The field 'title' must not have SQL",
            "The field 'title' must not have HTML tags",
            "The field 'title' must not be null or blank"
        );

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertThat(response.getBody().getValidationErrors()).isNotEmpty();
        try {
            assertTrue(expectedMessages.contains(response.getBody().getValidationErrors().getFirst().message()));
        } catch (IndexOutOfBoundsException e) {
            fail();
        }
    }

    @ParameterizedTest
    @MethodSource("provideInvalidEntries")
    @NullSource
    void shouldToBeNotAbleToCreateEventWhenLocationIsInvalid(String invalidLocation) {
        EventIn eventIn = new EventIn(title, invalidLocation, price, startDate, endDate, startTime, endTime);
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity( "/event", eventIn, ApiErrorResponse.class);

        List<String> expectedMessages = List.of(
            "The field 'location' must not have SQL",
            "The field 'location' must not have HTML tags",
            "The field 'location' must not be null or blank"
        );

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertThat(response.getBody().getValidationErrors()).isNotEmpty();
        try {
            assertTrue(expectedMessages.contains(response.getBody().getValidationErrors().getFirst().message()));
        } catch (IndexOutOfBoundsException e) {
            fail();
        }
    }

    static Stream<String> provideFormattedDates() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalDate nowDate = LocalDate.now();

        return Stream.of(
            futureDate.format(DateTimeFormatter.ofPattern("yy-MM-dd")),
            futureDate.format(DateTimeFormatter.ofPattern("yy-MMM-dd")),
            futureDate.format(DateTimeFormatter.ofPattern("dd/MM/yy")),
            futureDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            pastDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            " ",
            ""
        );
    }

    record EventInDateTest(String title, String location, Double price, String startDate,
                       String endDate, LocalTime startTime, LocalTime endTime){}

    @ParameterizedTest
    @MethodSource({"provideFormattedDates"})
    @NullSource
    void shouldToBeNotAbleToCreateEventWhenStartDateIsInvalid(String invalidStartDate) {
        EventInDateTest eventInTest = new EventInDateTest(title, location, price, invalidStartDate, endDate.toString(),
                startTime, endTime);
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity("/event", eventInTest, ApiErrorResponse.class);

        List<String> expectedMessages = List.of(
            "JSON parse error: The field 'startDate' must have format yyyy-mm-dd",
            "The field 'starDate' must not be null",
            "The field 'startDate' must be in the Future"
        );

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());

        if (response.getBody().getValidationErrors() != null && !response.getBody().getValidationErrors().isEmpty()) {
            assertTrue(expectedMessages.contains(response.getBody().getValidationErrors().getFirst().message()));
        } else {
            assertTrue(expectedMessages.contains(response.getBody().getErrorMessage()));
        }
    }
    
    @ParameterizedTest
    @MethodSource({"provideFormattedDates"})
    @NullSource
    void shouldToBeNotAbleToCreateEventWhenEndDateIsInvalid(String invalidEndDate) {
        EventInDateTest eventInTest = new EventInDateTest(title, location, price, startDate.toString(), invalidEndDate,
                startTime, endTime);
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity("/event", eventInTest, ApiErrorResponse.class);

        List<String> expectedMessages = List.of(
            "JSON parse error: The field 'endDate' must have format yyyy-mm-dd",
            "The field 'endDate' must not be null",
            "The field 'endDate' must be in the Future"
        );

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());

        if (response.getBody().getValidationErrors() != null && !response.getBody().getValidationErrors().isEmpty()) {
            assertTrue(expectedMessages.contains(response.getBody().getValidationErrors().getFirst().message()));
        } else {
            assertTrue(expectedMessages.contains(response.getBody().getErrorMessage()));
        }
    }

    @ParameterizedTest
    @CsvSource(value = {"-1", "-1.99", "a.123", "1,2", "asdf", "' '", "''"}, nullValues = "NULL")
    @NullSource
    void shouldToBeNotAbleToCreateEventWhenPriceIsInvalid(String invalidPrice) {
        record EventInTest(String title, String location, String invalidPrice, LocalDate startDate,
                           LocalDate endDate, LocalTime startTime, LocalTime endTime){}
        EventInTest eventInTest = new EventInTest(title, location, invalidPrice, startDate, endDate, startTime, endTime);
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity("/event", eventInTest, ApiErrorResponse.class);

        List<String> expectedMessages = List.of(
                "The field 'price' must be positive",
                "The field 'price' must not be null",
                "JSON parse error: For input string: \"a.123\"",
                "JSON parse error: For input string: \"1,2\"",
                "JSON parse error: For input string: \"asdf\""
        );

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertThat(response.getBody().getValidationErrors()).isNotEmpty();
        try {
            assertTrue(expectedMessages.contains(response.getBody().getValidationErrors().getFirst().message()));
        } catch (IndexOutOfBoundsException e) {
            fail();
        }
    }

    static Stream<String> provideFormattedTimes() {
        return Stream.of("23:5:59", "23:59:9", "1:59:59", "23:59", "24:59:59", "23:60:59", "23:59:60", " ", "");
    }

    record EventInTimeTest(String title, String location, Double price, LocalDate startDate,
                           LocalDate endDate, String startTime, String endTime){}

    @ParameterizedTest
    @MethodSource({"provideFormattedTimes"})
    @NullSource
    void shouldToBeNotAbleToCreateEventWhenStartTimeIsInvalid(String invalidStartTime) {
        EventInTimeTest eventInTest = new EventInTimeTest(title, location, price, startDate, endDate,
                invalidStartTime, endTime.toString());
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity("/event", eventInTest, ApiErrorResponse.class);

        List<String> expectedMessages = List.of(
                "JSON parse error: The field 'startTime' must have format hh:mm:ss",
                "The field 'startTime' must not be null",
                "JSON parse error: Invalid value for hour (valid values 00 - 23)",
                "JSON parse error: Invalid value for minutes (valid values 00 - 59)",
                "JSON parse error: Invalid value for seconds (valid values 00 - 59)"
        );

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());

        if (response.getBody().getValidationErrors() != null && !response.getBody().getValidationErrors().isEmpty()) {
            assertTrue(expectedMessages.contains(response.getBody().getValidationErrors().getFirst().message()));
        } else {
            System.out.println("TESTE " + response.getBody().getErrorMessage());
            assertTrue(expectedMessages.contains(response.getBody().getErrorMessage()));
        }
    }

    @ParameterizedTest
    @MethodSource({"provideFormattedTimes"})
    @NullSource
    void shouldToBeNotAbleToCreateEventWhenEndTimeIsInvalid(String invalidEndTime) {
        EventInTimeTest eventInTest = new EventInTimeTest(title, location, price, startDate, endDate,
                startTime.toString(), invalidEndTime);
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity("/event", eventInTest, ApiErrorResponse.class);

        List<String> expectedMessages = List.of(
                "JSON parse error: The field 'endTime' must have format hh:mm:ss",
                "The field 'endTime' must not be null",
                "JSON parse error: Invalid value for hour (valid values 00 - 23)",
                "JSON parse error: Invalid value for minutes (valid values 00 - 59)",
                "JSON parse error: Invalid value for seconds (valid values 00 - 59)"
        );

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());

        if (response.getBody().getValidationErrors() != null && !response.getBody().getValidationErrors().isEmpty()) {
            assertTrue(expectedMessages.contains(response.getBody().getValidationErrors().getFirst().message()));
        } else {
            System.out.println("TESTE " + response.getBody().getErrorMessage());
            assertTrue(expectedMessages.contains(response.getBody().getErrorMessage()));
        }
    }

}
