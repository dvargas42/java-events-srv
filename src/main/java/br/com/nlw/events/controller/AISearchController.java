package br.com.nlw.events.controller;

import br.com.nlw.events.dto.AISearchIn;
import br.com.nlw.events.service.AISearchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai-search")
public class AISearchController {

  private final AISearchService aiService;

  public AISearchController(AISearchService aiService) {
    this.aiService = aiService;
  }

  @PostMapping
  public ResponseEntity<?> generateResponse(@Valid @RequestBody AISearchIn aiSearchIn)
      throws Exception {
    if (aiSearchIn.formatAsMarkdown()) {
      return ResponseEntity.ok().body(aiService.generateMarkDownResponse(aiSearchIn.prompt()));
    }
    return ResponseEntity.ok().body(aiService.generateJsonResponse(aiSearchIn.prompt()));
  }
}
