package br.com.nlw.events.error;

import br.com.nlw.events.exception.AISearchInvalidQueryException;
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
