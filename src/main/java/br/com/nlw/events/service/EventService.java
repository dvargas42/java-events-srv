package br.com.nlw.events.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.dto.EventOut;
import br.com.nlw.events.exception.EventConflictException;
import br.com.nlw.events.mapper.IEventMapper;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.repository.EventRepo;

@Service
public class EventService {

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private IEventMapper eventMapper;

    public EventOut addNewEvent(EventIn eventIn) {
        String prettyName = eventIn.title().toLowerCase().replaceAll(" ", "-");
        Event foundEvent = eventRepo.findByPrettyName(prettyName);
        if (foundEvent != null) {
            throw new EventConflictException("Já existe cadastro para o evento " + foundEvent.getTitle());
        }
        Event event = eventMapper.toEntity(eventIn);
        event.setPrettyName(prettyName);
        return eventMapper.toEventOut(eventRepo.save(event));
    }

    public List<EventOut> getAllEvents() {
        List<EventOut> events = ((List<Event>) eventRepo.findAll()).stream()
                .map(event -> {
                    return eventMapper.toEventOut(event);
                }).toList();
        return events;
    }

    public EventOut getByPrettyName(String prettyName) {
        return eventMapper.toEventOut(eventRepo.findByPrettyName(prettyName));
    }
}
