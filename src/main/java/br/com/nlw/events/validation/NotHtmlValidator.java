package br.com.nlw.events.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotHtmlValidator implements ConstraintValidator<NotHtml, String> {

    private Pattern pattern = Pattern.compile("<[^>]*>");

    @Override
    public void initialize(NotHtml constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Matcher matcher = pattern.matcher(value);
        return !matcher.find();
    }
}
