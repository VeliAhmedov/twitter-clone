package com.twittvl.backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException rex) {
        return  buildResponseEntity(HttpStatus.NOT_FOUND, rex.getMessage());
    }

    public ResponseEntity<ApiErrorResponse> handleBadRequest(IllegalArgumentException rex) {
        return  buildResponseEntity(HttpStatus.BAD_REQUEST, rex.getMessage());
    }

    public ResponseEntity<ApiErrorResponse> handleValidationError(MethodArgumentNotValidException rex) {
         String message = rex.getBindingResult().getFieldErrors().stream()
                 .findFirst()
                 .map(f -> f.getField() + " : "+ f.getDefaultMessage())
                 .orElse("Validation failed");
         return  buildResponseEntity(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
    }


    private ResponseEntity<ApiErrorResponse> buildResponseEntity(HttpStatus status, String message) {
         ApiErrorResponse apiErrorResponse = new ApiErrorResponse(status.value(), message, Instant.now());
         return  ResponseEntity.status(status).body(apiErrorResponse);
    }
}
