package br.com.nlw.events.error;

import br.com.nlw.events.exception.AISearchInvalidQueryException;
import br.com.nlw.events.exception.EventConflictException;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.RankingEventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.SubscriptionEventNotFoundException;
import br.com.nlw.events.exception.UserIndicatorNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    return buildResponseEntity(errorResponse);
  }

  @ExceptionHandler(EventConflictException.class)
  public ResponseEntity<Object> handleEventConflictException(
      EventConflictException ex, WebRequest request) {
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    return buildResponseEntity(errorResponse);
  }

  @ExceptionHandler(EventNotFoundException.class)
  public ResponseEntity<Void> handleEventNotFoundException(
      EventNotFoundException ex, WebRequest request) {
    new ApiErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(SubscriptionEventNotFoundException.class)
  public ResponseEntity<Object> handleSubscriptionEventNotFoundException(
      SubscriptionEventNotFoundException ex, WebRequest request) {
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    return buildResponseEntity(errorResponse);
  }

  @ExceptionHandler(RankingEventNotFoundException.class)
  public ResponseEntity<Object> handleRankingEventNotFoundException(
      RankingEventNotFoundException ex, WebRequest request) {
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    return buildResponseEntity(errorResponse);
  }

  @ExceptionHandler(UserIndicatorNotFoundException.class)
  public ResponseEntity<Object> handleUserIndicatorNotFoundException(
      UserIndicatorNotFoundException ex, WebRequest request) {
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    return buildResponseEntity(errorResponse);
  }

  @ExceptionHandler(SubscriptionConflictException.class)
  public ResponseEntity<Object> handleSubscriptionConflictException(
      SubscriptionConflictException ex, WebRequest request) {
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    return buildResponseEntity(errorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Object> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, WebRequest request) {
    ApiErrorResponse errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    return buildResponseEntity(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, WebRequest request) {
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getBindingResult(),
            ex.getBindingResult().getFieldErrors(),
            request);
    return buildResponseEntity(errorResponse);
  }

  @ExceptionHandler(AISearchInvalidQueryException.class)
  public ResponseEntity<Object> handleAIGenerateQueryInvalidException(
      AISearchInvalidQueryException ex, WebRequest request) {
    ApiErrorResponse errorResponse =
        new ApiErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    return buildResponseEntity(errorResponse);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiErrorResponse errorResponse) {
    return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
  }
}
