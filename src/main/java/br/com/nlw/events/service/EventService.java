package br.com.nlw.events.service;

import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.dto.EventOut;
import br.com.nlw.events.exception.EventConflictException;
import br.com.nlw.events.mapper.IEventMapper;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.repository.EventRepo;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EventService {

  private final EventRepo eventRepo;
  private final IEventMapper eventMapper;

  public EventService(EventRepo eventRepo, IEventMapper eventMapper) {
    this.eventRepo = eventRepo;
    this.eventMapper = eventMapper;
  }

  public EventOut addNewEvent(EventIn eventIn) {
    String prettyName = eventIn.title().toLowerCase().replace(" ", "-");
    Event foundEvent = eventRepo.findByPrettyName(prettyName);
    if (foundEvent != null) {
      throw new EventConflictException("Já existe cadastro para o evento " + foundEvent.getTitle());
    }
    Event event = eventMapper.toEntity(eventIn);
    event.setPrettyName(prettyName);
    return eventMapper.toEventOut(eventRepo.save(event));
  }

  public List<EventOut> getAllEvents() {
    return ((List<Event>) eventRepo.findAll()).stream().map(eventMapper::toEventOut).toList();
  }

  public EventOut getByPrettyName(String prettyName) {
    return eventMapper.toEventOut(eventRepo.findByPrettyName(prettyName));
  }
}
