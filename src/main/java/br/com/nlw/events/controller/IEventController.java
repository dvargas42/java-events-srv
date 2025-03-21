package br.com.nlw.events.controller;

import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.model.Event;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Event", description = "Event information")
public interface IEventController {

  @Operation(
      summary = "Event register",
      description = "This functionality is responsible for creating a event")
  @ApiResponse(
      responseCode = "200",
      content = {@Content(schema = @Schema(implementation = Event.class))})
  ResponseEntity<?> addNewEvent(EventIn eventIn, UriComponentsBuilder uriBuilder);

  @Operation(
      summary = "Event search all",
      description = "This functionality is responsible for search all events")
  @ApiResponse(
      responseCode = "200",
      content = {@Content(schema = @Schema(implementation = Event.class))})
  @ApiResponse(responseCode = "404", description = "Event not found")
  ResponseEntity<?> getAllEvents();

  @Operation(
      summary = "Event search by prettyName",
      description = "This functionality is responsible for search a event by prettyName")
  @ApiResponse(
      responseCode = "200",
      content = {@Content(schema = @Schema(implementation = Event.class))})
  @ApiResponse(responseCode = "404", description = "Event not found")
  ResponseEntity<?> getByPrettyName(String id);
}
