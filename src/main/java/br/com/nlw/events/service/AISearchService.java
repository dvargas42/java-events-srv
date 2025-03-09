package br.com.nlw.events.service;

import br.com.nlw.events.dto.AISearchJsonOut;
import br.com.nlw.events.dto.AISearchMarkDownOut;
import br.com.nlw.events.enums.AISystemEnum;
import br.com.nlw.events.exception.AISearchInvalidAPIKeyException;
import br.com.nlw.events.exception.AISearchInvalidQueryException;
import br.com.nlw.events.exception.AISearchSendChatException;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AISearchService {

  private static final Logger logger = LoggerFactory.getLogger(AISearchService.class);

  private final OpenAiService openAiService;
  private final JdbcTemplate jdbcTemplate;

  public AISearchService(OpenAiService openAiService, JdbcTemplate jdbcTemplate) {
    this.openAiService = openAiService;
    this.jdbcTemplate = jdbcTemplate;
  }

  public AISearchJsonOut generateJsonResponse(String prompt) throws Exception {
    ChatCompletionRequest chatRequestQuery =
        createChatRequest(prompt, AISystemEnum.SYSTEM_QUERY.value());
    String query = sendChatRequest(chatRequestQuery);
    List<Map<String, Object>> result = executeQuery(query);

    int inputCountTokens = countTokens(prompt + AISystemEnum.SYSTEM_QUERY.value());
    int outputCountTokens = countTokens(result.toString());

    BigDecimal costEstimate = costCalculator(inputCountTokens, outputCountTokens, prompt, query);
    return new AISearchJsonOut(result, inputCountTokens, outputCountTokens, costEstimate);
  }

  public AISearchMarkDownOut generateMarkDownResponse(String prompt) throws Exception {
    ChatCompletionRequest chatRequestQuery =
        createChatRequest(prompt, AISystemEnum.SYSTEM_QUERY.value());
    String query = sendChatRequest(chatRequestQuery);
    List<Map<String, Object>> queryResult = executeQuery(query);

    ChatCompletionRequest chatRequestMarkDown =
        createChatRequest(queryResult.toString(), AISystemEnum.SYSTEM_MARKDOWN.value());
    String markdownResult = sendChatRequest(chatRequestMarkDown);

    int inputCountTokens =
        countTokens(
            prompt + AISystemEnum.SYSTEM_QUERY.value() + AISystemEnum.SYSTEM_MARKDOWN.value());
    int outputCountTokens = countTokens(queryResult.toString() + markdownResult);

    BigDecimal costEstimate = costCalculator(inputCountTokens, outputCountTokens, prompt, query);
    return new AISearchMarkDownOut(
        markdownResult, inputCountTokens, outputCountTokens, costEstimate);
  }

  private ChatCompletionRequest createChatRequest(String prompt, String system) {
    String model = selectModelByConsumption(countTokens(prompt + system));
    return ChatCompletionRequest.builder()
        .model(model)
        .messages(
            Arrays.asList(
                new ChatMessage(ChatMessageRole.USER.value(), prompt),
                new ChatMessage(ChatMessageRole.SYSTEM.value(), system)))
        .maxTokens(300)
        .temperature(0.7)
        .build();
  }

  private String sendChatRequest(ChatCompletionRequest chatCompletionRequest) throws Exception {
    long newTimeAttempt = 1;
    int attempt = 0;

    while (attempt++ < 5) {
      try {
        ChatCompletionResult chatCompletionResult =
            openAiService.createChatCompletion(chatCompletionRequest);
        return chatCompletionResult.getChoices().get(0).getMessage().getContent();
      } catch (OpenAiHttpException ex) {
        switch (ex.statusCode) {
          case 401 -> throw new AISearchInvalidAPIKeyException("Invalid API Key");
          case 429 -> {
            logger.error("Rate limit exceeded! New attempt in a moment");
            Thread.sleep(1000 * newTimeAttempt);
            newTimeAttempt *= 2;
          }
          case 500, 503 -> {
            logger.error("API down! New attempt in a moment");
            Thread.sleep(1000 * newTimeAttempt);
            newTimeAttempt *= 2;
          }
          default -> throw new AISearchSendChatException("Default error: " + ex.statusCode);
        }
      }
    }
    throw new AISearchSendChatException(
        "Failed to get a response from the AI service after multiple attempts.");
  }

  private List<Map<String, Object>> executeQuery(String query) {
    if (!query.substring(0, 6).equalsIgnoreCase("select")) {
      throw new AISearchInvalidQueryException("Desculpe, não posso ajudar com essa solicitação.");
    }
    return jdbcTemplate.queryForList(query);
  }

  private int countTokens(String input) {
    EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
    Encoding encoding = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO);
    return encoding.countTokens(input);
  }

  private BigDecimal costCalculator(
      Integer inputCountTokens, Integer outputCountTokens, String prompt, String system) {
    String model = selectModelByConsumption(inputCountTokens + outputCountTokens);
    final BigDecimal ONE_MILLION_TOKENS = BigDecimal.valueOf(1_000_000.0);

    BigDecimal inputCount = BigDecimal.valueOf(inputCountTokens);
    BigDecimal outputCount = BigDecimal.valueOf(outputCountTokens);

    BigDecimal inputPricing =
        model.equals("gpt-3.5-turbo") ? BigDecimal.valueOf(3.00) : BigDecimal.valueOf(0.60);
    BigDecimal outputPricing =
        model.equals("gpt-3.5-turbo") ? BigDecimal.valueOf(6.00) : BigDecimal.valueOf(2.40);

    BigDecimal inputCost = inputCount.divide(ONE_MILLION_TOKENS).multiply(inputPricing);
    BigDecimal outputCost = outputCount.divide(ONE_MILLION_TOKENS).multiply(outputPricing);

    return inputCost.add(outputCost).setScale(8, RoundingMode.HALF_UP);
  }

  private String selectModelByConsumption(int countTokens) {
    if (countTokens > 4096) {
      return "gpt-3.5-turbo";
    }
    return "gpt-4o-mini-2024-07-18";
  }
}
