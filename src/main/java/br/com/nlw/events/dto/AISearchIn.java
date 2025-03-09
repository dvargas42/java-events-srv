package br.com.nlw.events.dto;

import br.com.nlw.events.validation.NotHtml;
import br.com.nlw.events.validation.NotSql;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.bind.DefaultValue;

public record AISearchIn(
    @NotHtml(message = "The field 'prompt' must not have HTML tags")
        @NotSql(message = "The field 'prompt' must not have SQL")
        @NotBlank(message = "The field 'prompt' must not be null or blank")
        String prompt,
    @DefaultValue("false") boolean formatAsMarkdown) {}
