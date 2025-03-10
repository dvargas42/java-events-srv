package br.com.nlw.events.controller;

import br.com.nlw.events.dto.ErrorMessage;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.UserIn;
import br.com.nlw.events.exception.UserIndicatorNotFoundException;
import br.com.nlw.events.service.SubscriptionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController implements ISubscriptionController {

  private final SubscriptionService service;

  public SubscriptionController(SubscriptionService service) {
    this.service = service;
  }

  @PostMapping({"/{prettyName}", "/{prettyName}/{userId}"})
  public ResponseEntity<?> createSubscription(
      @PathVariable String prettyName,
      @PathVariable(required = false) Integer userId,
      @Valid @RequestBody UserIn subscriber) {
    return ResponseEntity.ok(service.createSubscription(prettyName, userId, subscriber));
  }

  @GetMapping("/{prettyName}/ranking")
  public ResponseEntity<List<SubscriptionRankingItem>> generateRankingByEvent(
      @PathVariable String prettyName) {
    List<SubscriptionRankingItem> completeRanking = service.getCompleteRanking(prettyName);
    if (completeRanking.size() < 3) {
      return ResponseEntity.ok(completeRanking);
    }
    return ResponseEntity.ok(completeRanking.subList(0, 3));
  }

  @GetMapping("/{prettyName}/ranking/{userId}")
  public ResponseEntity<?> generateRankingByUserId(
      @PathVariable String prettyName, @PathVariable Integer userId) {
    try {
      return ResponseEntity.ok(service.getRankingByUser(prettyName, userId));
    } catch (UserIndicatorNotFoundException ex) {
      return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
    }
  }
}
