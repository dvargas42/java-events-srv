package br.com.nlw.events.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.dto.EventOut;
import br.com.nlw.events.model.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING)
public interface IEventMapper {

  Event toEntity(EventIn eventIn);

  EventOut toEventOut(Event event);
}
