package br.com.nlw.events.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.mapstruct.Mapper;

import br.com.nlw.events.dto.UserIn;
import br.com.nlw.events.model.User;

@Mapper(componentModel = SPRING)
public interface ISubscriptionMapper {
    
    User toEntity(UserIn userIn);

}
