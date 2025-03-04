package br.com.nlw.events.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {

    private Pattern pattern = Pattern.compile(
        "^(?!\\.)(?!.*\\.\\.)[a-zA-Z0-9\\u0080-\\uFFFF._%+-]*[a-zA-Z0-9\\u0080-\\uFFFF]@[a-zA-Z0-9\\u0080-\\uFFFF](?:[a-zA-Z0-9\\u0080-\\uFFFF-]{0,61}[a-zA-Z0-9\\u0080-\\uFFFF])?(?:\\.[a-zA-Z0-9\\u0080-\\uFFFF](?:[a-zA-Z0-9\\u0080-\\uFFFF-]{0,61}[a-zA-Z0-9\\u0080-\\uFFFF])?)*\\.[a-zA-Z\\u0080-\\uFFFF]{2,}$"
    );

    @Override
    public void initialize(Email constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        String[] emailParts = value.split("@");

        if (emailParts.length != 2 || emailParts[0].length() >= 100 || emailParts[1].length() >= 120) {
            return false;
        }

        Matcher matcher = pattern.matcher(value);
        return matcher.find();
    }
}
