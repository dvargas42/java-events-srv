package br.com.nlw.events.dto;

import br.com.nlw.events.validation.Email;
import br.com.nlw.events.validation.NotHtml;
import br.com.nlw.events.validation.NotSql;
import jakarta.validation.constraints.NotBlank;

public record UserIn(
    @NotSql(message = "The field 'name' must not have SQL")
        @NotHtml(message = "The field 'name' must not have HTML tags")
        @NotBlank(message = "The field 'name' must not be null or blank")
        String name,
    @Email(message = "The field 'email' must be valid")
        @NotBlank(message = "The field 'email' must not be null or blank")
        String email) {}
