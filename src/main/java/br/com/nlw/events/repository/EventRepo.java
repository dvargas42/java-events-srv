package br.com.nlw.events.repository;

import br.com.nlw.events.model.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepo extends CrudRepository<Event, Integer> {

    public Event findByPrettyName(String prettyName);
}
