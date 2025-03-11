package br.com.nlw.events.controller;

import org.springframework.http.ResponseEntity;

import br.com.nlw.events.dto.AISearchIn;
import br.com.nlw.events.dto.AISearchJsonOut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AI Search", description = "Natural Language search")
public interface IAISearchController {

  @Operation(
      summary = "Natural Language search",
      description = "This feature is responsible for searching anything in the database using natural language.")
  @ApiResponse(
      responseCode = "200",
      content = {@Content(schema = @Schema(implementation = AISearchJsonOut.class))})
  @ApiResponse(responseCode = "401", description = "Invalid API Key")
  @ApiResponse(responseCode = "503", description = "Service Unavailable")
  ResponseEntity<?> generateResponse(AISearchIn aiSearchIn) throws Exception;
}
