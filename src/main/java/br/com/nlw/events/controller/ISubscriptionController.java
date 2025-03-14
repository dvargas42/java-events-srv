package br.com.nlw.events.controller;

import br.com.nlw.events.dto.SubscriptionOut;
import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.UserIn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Subscription", description = "Subscription information")
public interface ISubscriptionController {

  @Operation(
      summary = "Subscription register",
      description = "This functionality is responsible for creating a subscription")
  @ApiResponse(
      responseCode = "200",
      content = {@Content(schema = @Schema(implementation = SubscriptionOut.class))})
  @ApiResponse(responseCode = "404", description = "Event not found or User indicator not found")
  @ApiResponse(responseCode = "409", description = "Subscription conflict")
  ResponseEntity<?> createSubscription(String prettyName, Integer userId, UserIn subscriber);

  @Operation(
      summary = "Subscription ranking list all",
      description =
          "This functionality is responsible for list all subscription ranking from an event")
  @ApiResponse(
      responseCode = "200",
      content = {@Content(schema = @Schema(implementation = SubscriptionRankingItem.class))})
  @ApiResponse(responseCode = "404", description = "Event not found")
  ResponseEntity<?> generateRankingByEvent(String prettyName);

  @Operation(
      summary = "Subscription ranking by userId",
      description = "This functionality is responsible for search subscription ranking by userId")
  @ApiResponse(
      responseCode = "200",
      content = {@Content(schema = @Schema(implementation = SubscriptionRankingByUser.class))})
  @ApiResponse(responseCode = "404", description = "Event not found")
  ResponseEntity<?> generateRankingByUserId(String prettyName, Integer userId);
}
