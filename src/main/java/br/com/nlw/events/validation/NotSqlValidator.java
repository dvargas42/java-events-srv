package br.com.nlw.events.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotSqlValidator implements ConstraintValidator<NotSql, String> {

  private Pattern pattern =
      Pattern.compile(
          "(--|\\b(OR|AND)\\b.+\\b(SELECT|INSERT|DELETE|UPDATE|DROP|FROM|WHERE)\\b|\\b(SELECT|INSERT|DELETE|UPDATE|DROP)\\b.+\\b(FROM|WHERE)\\b)",
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
