package br.com.nlw.events.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.nlw.events.dto.ErrorMessage;
import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.dto.EventOut;
import br.com.nlw.events.exception.EventConflictException;
import br.com.nlw.events.service.EventService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/event")
public class EventController implements IEventController {

    @Autowired
    private EventService service;

    @PostMapping
    public ResponseEntity<?> addNewEvent(@RequestBody @Valid EventIn eventIn, UriComponentsBuilder uriBuilder) {
        EventOut eventOut = null;
        try {
            eventOut = service.addNewEvent(eventIn);
        } catch (EventConflictException ex) {
            return ResponseEntity.status(409).body(new ErrorMessage(ex.getMessage()));
        }
        URI uri = uriBuilder.path("/event/{id}").buildAndExpand(eventOut.eventId()).toUri();
        return ResponseEntity.created(uri).body(eventOut);
    }

    @GetMapping
    public ResponseEntity<List<EventOut>> getAllEvents() {
        return ResponseEntity.ok().body(service.getAllEvents());
    }

    @GetMapping("/{prettyName}")
    public ResponseEntity<?> getByPrettyName(@PathVariable(name = "prettyName") String id) {
        EventOut eventOut = service.getByPrettyName(id);
        if (eventOut != null) {
            return ResponseEntity.ok().body(eventOut);
        }
        return ResponseEntity.notFound().build();
    }
}
