package br.com.nlw.events.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import br.com.nlw.events.dto.UserIn;
import br.com.nlw.events.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING)
public interface ISubscriptionMapper {

  User toEntity(UserIn userIn);
}
