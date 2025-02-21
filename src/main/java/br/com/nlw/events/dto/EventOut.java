package br.com.nlw.events.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventOut (

    Integer eventId,
    
    String title,

    String prettyName,

    String location,

    Double price,

    LocalDate startDate,

    LocalDate endDate,

    LocalTime startTime,

    LocalTime endTime
){
    
}
