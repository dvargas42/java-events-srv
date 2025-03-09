package br.com.nlw.events.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.com.nlw.events.dto.AISearchIn;
import br.com.nlw.events.dto.AISearchJsonOut;
import br.com.nlw.events.dto.AISearchMarkDownOut;
import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.service.AISearchService;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AISearchControllerTest {

  @Autowired private TestRestTemplate restTemplate;
  @Mock private OpenAiService openAiService;
  @InjectMocks private AISearchService aiService;

  private HttpHeaders headers;

  private final LocalDate START_DATE = LocalDate.now().plusDays(1);
  private final LocalDate END_DATE = LocalDate.now().plusDays(2);
  private final LocalTime START_TIME = LocalTime.of(19, 9, 9);
  private final LocalTime END_TIME = LocalTime.of(21, 9, 9);
  private final String TITLE = "Codecraft Summit 2025";
  private final String LOCATION = "Online";
  private final Double PRICE = 0.0;
  private final EventIn EVENT_IN =
      new EventIn(TITLE, LOCATION, PRICE, START_DATE, END_DATE, START_TIME, END_TIME);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
  }

  @Test
  void shouldGenerateJsonAndReturnEmptyList() {
    String mockQuery = "SELECT * FROM events";
    AISearchIn aiSearchIn = new AISearchIn("Return all events available", false);

    when(openAiService.createChatCompletion(any(ChatCompletionRequest.class)))
        .thenReturn(mockChatCompletionResult(mockQuery));

    HttpEntity<AISearchIn> request = new HttpEntity<>(aiSearchIn, headers);
    ResponseEntity<AISearchJsonOut> response =
        restTemplate.exchange("/ai-search", HttpMethod.POST, request, AISearchJsonOut.class);

    if (response.getBody() == null) {
      fail("Response body is null");
    }

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody().result()).isNullOrEmpty();
  }

  @Test
  void shouldGenerateJsonAndReturnFullList() {
    String mockQuery = "SELECT * FROM events";

    HttpEntity<EventIn> eventRequest = new HttpEntity<>(EVENT_IN, headers);
    restTemplate.exchange("/event", HttpMethod.POST, eventRequest, String.class);

    when(openAiService.createChatCompletion(any(ChatCompletionRequest.class)))
        .thenReturn(mockChatCompletionResult(mockQuery));

    AISearchIn aiSearchIn = new AISearchIn("Return all events available.", false);
    HttpEntity<AISearchIn> request = new HttpEntity<>(aiSearchIn, headers);
    ResponseEntity<AISearchJsonOut> response =
        restTemplate.exchange("/ai-search", HttpMethod.POST, request, AISearchJsonOut.class);

    if (response.getBody() == null) {
      fail("Response body is null");
    }

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody().result()).isNotNull();
  }

  @Test
  void shouldGenerateMarkDownAndReturnFullList() {
    HttpEntity<EventIn> eventRequest = new HttpEntity<>(EVENT_IN, headers);
    restTemplate.exchange("/event", HttpMethod.POST, eventRequest, String.class);

    String mockQuery = "| EVENT_ID |";
    when(openAiService.createChatCompletion(any(ChatCompletionRequest.class)))
        .thenReturn(mockChatCompletionResult(mockQuery));

    AISearchIn aiSearchIn =
        new AISearchIn("Return all events available and include event_id in objects.", true);
    HttpEntity<AISearchIn> request = new HttpEntity<>(aiSearchIn, headers);
    ResponseEntity<AISearchMarkDownOut> response =
        restTemplate.exchange("/ai-search", HttpMethod.POST, request, AISearchMarkDownOut.class);

    if (response.getBody() == null) {
      fail("Response body is null");
    }

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody().result()).contains("| EVENT_ID |");
  }

  @Test
  void shouldGenerateMarkDownAndReturnEmptyList() {
    String mockQuery = "|";
    when(openAiService.createChatCompletion(any(ChatCompletionRequest.class)))
        .thenReturn(mockChatCompletionResult(mockQuery));

    AISearchIn aiSearchIn =
        new AISearchIn("Return all events available for registration in March", true);
    HttpEntity<AISearchIn> request = new HttpEntity<>(aiSearchIn, headers);
    ResponseEntity<AISearchMarkDownOut> response =
        restTemplate.exchange("/ai-search", HttpMethod.POST, request, AISearchMarkDownOut.class);

    if (response.getBody() == null) {
      fail("Response body is null");
    }

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody().result()).contains("|");
  }

  private ChatCompletionResult mockChatCompletionResult(String responseText) {
    ChatMessage message = new ChatMessage(ChatMessageRole.ASSISTANT.value(), responseText);

    ChatCompletionChoice choice = new ChatCompletionChoice();
    choice.setMessage(message);

    ChatCompletionResult result = new ChatCompletionResult();
    result.setChoices(Collections.singletonList(choice));

    return result;
  }
}
