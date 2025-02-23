package br.com.nlw.events.dto;

import br.com.nlw.events.validation.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventIn(

    @NotHtml(message = "The field 'title' must not have HTML tags")
    @NotSql(message = "The field 'title' must not have SQL")
    @NotBlank(message = "The field 'title' must not be null or blank")
    String title,

    @NotHtml(message = "The field 'location' must not have HTML tags")
    @NotSql(message = "The field 'location' must not have SQL")
    @NotBlank(message = "The field 'location' must not be null or blank")
    String location,

    @Price(message = "The field 'price' must be positive")
    @NotNull(message = "The field 'price' must not be null")
    Double price,

    @Future(message = "The field 'startDate' must be in the Future")
    @Date(message = "The field 'startDate' must have format yyyy-mm-dd")
    @NotNull(message = "The field 'starDate' must not be null")
    LocalDate startDate,

     @Future(message = "The field 'endDate' must not be in Past")
     @Date(message = "The field 'endDate' must have format yyyy-MM-dd")
     @NotNull(message = "The field 'endDate' must not be null")
     LocalDate endDate,

    @Time(message = "The field 'startTime' must have format hh:mm:ss")
    @NotNull(message = "The field 'startTime' must not be null")
    LocalTime startTime,

    @Time(message = "The field 'endTime' must have format hh:mm:ss")
    @NotNull(message = "The field 'endTime' must not be null")
    LocalTime endTime    
) {
}
