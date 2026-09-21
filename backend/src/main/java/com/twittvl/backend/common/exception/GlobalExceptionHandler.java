package com.twittvl.backend.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Self4j for log failures
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //catches custom exception that "data isn't in database to work with"
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException notFoundEx, WebRequest request) {
        return  buildResponseEntity(HttpStatus.NOT_FOUND, notFoundEx.getMessage(), request);
    }

    //catches manual validation like already liked, content can't be empty, already liked and etc
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(IllegalArgumentException badReqEx, WebRequest request) {
        return  buildResponseEntity(HttpStatus.BAD_REQUEST, badReqEx.getMessage(), request);
    }

    //catches failure from DTO validation annotations and make it better readable
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationError(MethodArgumentNotValidException validationEx, WebRequest request) {
         String message = validationEx.getBindingResult().getFieldErrors().stream()
                 .findFirst()
                 .map(f -> f.getField() + " : "+ f.getDefaultMessage())
                 .orElse("Validation failed");
         return  buildResponseEntity(HttpStatus.BAD_REQUEST, message, request);
    }

    //it catches wrong type of path variable or request param given to request
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException mismatchEx, WebRequest request) {
        String message = mismatchEx.getName() + " should be type of " + (mismatchEx.getRequiredType() != null ? mismatchEx.getRequiredType().getSimpleName() : "different type");
        return  buildResponseEntity(HttpStatus.BAD_REQUEST, message, request);
    }

    //it catches missing header if not given to request like no x-User
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingHeader(MissingRequestHeaderException missingHeadEx, WebRequest request) {
        return   buildResponseEntity(HttpStatus.BAD_REQUEST, missingHeadEx.getMessage(), request);
    }

    //it catches missing param like if ?userId= doesn't exist, like header but for param
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParam(MissingServletRequestParameterException missingServParamEx, WebRequest request) {
        return  buildResponseEntity(HttpStatus.BAD_REQUEST, missingServParamEx.getMessage(), request);
    }

    //it catches if JSON body has syntax problem like missing , or }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableBody(HttpMessageNotReadableException unreadBodyEx, WebRequest request) {
        return  buildResponseEntity(HttpStatus.BAD_REQUEST, "Malformed JSON request body", request);
    }

    //it catches DB constraint violation like race, unique constraints
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException dataIntegrityEx, WebRequest request) {
        log.warn("Data integrity violation: {}", dataIntegrityEx.getMessage());
        return  buildResponseEntity(HttpStatus.CONFLICT, "Request Conflicts occurred with existing data", request);
    }

    //last catch if nothing here catches exception, didn't anticipated
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, WebRequest request) {
        log.error("Unexcepted exception", ex);
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", request);
    }

    private ResponseEntity<ApiErrorResponse> buildResponseEntity(HttpStatus status, String message, WebRequest webRequest) {
         String pathOfError = webRequest.getDescription(false).replace("uri=", "");
         ApiErrorResponse apiErrorResponse = new ApiErrorResponse(status.value(), status.getReasonPhrase(), message, pathOfError, Instant.now());
         return  ResponseEntity.status(status).body(apiErrorResponse);
    }
}
