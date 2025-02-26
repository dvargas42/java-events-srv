package br.com.nlw.events.error;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ApiErrorResponse {

    private static final Logger logger = LoggerFactory.getLogger(ApiErrorResponse.class);
    
    private Integer statusCode;
    private HttpStatus httpStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String errorMessage;
    private List<ValidationError> validationErrors;

    private ApiErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiErrorResponse(HttpStatus httpStatus, String errorMessage) {
        this();
        this.statusCode = httpStatus.value();
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
        logger.error("API Error - Status: {}, Code: {}, Message: {}", this.httpStatus, this.statusCode, this.errorMessage);
    }

    public ApiErrorResponse(HttpStatus httpStatus, String errorMessage, WebRequest request) {
        this();
        this.statusCode = httpStatus.value();
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
        logger.error("API Error -Status: {}, Code: {}, Message: {}, Path: {}", this.httpStatus, this.statusCode, this.errorMessage, request.getDescription(false));
    }

    public ApiErrorResponse(HttpStatus httpStatus, BindingResult bindingResult, List<FieldError> fieldErrors, WebRequest request) {
        this();
        this.statusCode = httpStatus.value();
        this.httpStatus = httpStatus;
        this.errorMessage = "Validation failed for object='" + bindingResult.getObjectName() + "'. Error count: " + bindingResult.getErrorCount();
        this.validationErrors = fieldErrors.stream().map(ValidationError::new).toList();
        logger.error("API Validation Error - Status: {}, Code: {}, Message: {}, Erros: {}, Path: {}", this.httpStatus, this.statusCode, this.errorMessage, this.validationErrors, request.getDescription(false));
    }
    
    public record ValidationError(String field, String message) {
        public ValidationError(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }
}