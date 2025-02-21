package br.com.nlw.events.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotSqlValidator implements ConstraintValidator<NotSql, String>{

    private Pattern pattern = Pattern.compile(
            "(--|\\bOR\\b|\\bAND\\b|SELECT|INSERT|DELETE|UPDATE|DROP|\\bFROM\\b|\\bWHERE\\b)",
            Pattern.CASE_INSENSITIVE);


    @Override
    public void initialize(NotSql constraintAnnotation) {
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
