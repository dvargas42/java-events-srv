package br.com.nlw.events.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.dto.EventOut;
import br.com.nlw.events.service.EventService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/event")
public class EventController implements IEventController {

  private final EventService service;

  public EventController(EventService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<?> addNewEvent(
      @RequestBody @Valid EventIn eventIn, UriComponentsBuilder uriBuilder) {
    EventOut eventOut = service.addNewEvent(eventIn);
    URI uri = uriBuilder.path("/event/{id}").buildAndExpand(eventOut.eventId()).toUri();
    return ResponseEntity.created(uri).body(eventOut);
  }

  @GetMapping
  public ResponseEntity<List<EventOut>> getAllEvents() {
    return ResponseEntity.ok().body(service.getAllEvents());
  }

  @GetMapping("/{prettyName}")
  public ResponseEntity<EventOut> getByPrettyName(@PathVariable(name = "prettyName") String title) {
    return ResponseEntity.ok().body(service.getByPrettyName(title));
  }
}
