package br.com.nlw.events.controller;

import br.com.nlw.events.dto.ErrorMessage;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService service;

    @PostMapping
    public Event addNewEvent(@RequestBody Event newEvent) {
        return service.AddeNewEvent(newEvent);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return service.getAllEvents();
    }

    @GetMapping("/{prettyName}")
    public ResponseEntity<?> getByPrettyName(@PathVariable(name = "prettyName") String id) {
        Event event = service.getByPrettyName(id);
        if (event != null) {
            return ResponseEntity.ok().body(event);
        }

        return ResponseEntity.notFound().build();
    }
}
