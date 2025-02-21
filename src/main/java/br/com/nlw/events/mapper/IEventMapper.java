package br.com.nlw.events.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.mapstruct.Mapper;

import br.com.nlw.events.dto.EventIn;
import br.com.nlw.events.dto.EventOut;
import br.com.nlw.events.model.Event;

@Mapper(componentModel = SPRING)
public interface IEventMapper {
    
    Event toEntity(EventIn eventIn);
    
    EventOut toEventOut(Event event);
}
