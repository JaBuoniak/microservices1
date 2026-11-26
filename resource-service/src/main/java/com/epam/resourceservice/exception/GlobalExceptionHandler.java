package com.epam.resourceservice.exception;

import com.epam.resourceservice.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not supported: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        String contentType = ex.getContentType() != null ? ex.getContentType().toString() : "unknown";
        log.warn("Unsupported media type: {}", contentType);
        ErrorResponse error = new ErrorResponse(
                "Invalid file format: " + contentType + ". Only MP3 files are allowed",
                "400"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidAudioFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAudioFormat(InvalidAudioFormatException ex) {
        log.warn("Audio validation failed: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "404");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidResourceIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResourceId(InvalidResourceIdException ex) {
        log.warn("Invalid resource ID: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidCsvParameterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCsvParameter(InvalidCsvParameterException ex) {
        log.warn("Invalid CSV parameter: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        log.warn("Missing required parameter: {}", ex.getParameterName());
        ErrorResponse error = new ErrorResponse(
                "Missing required parameter: " + ex.getParameterName(),
                "400"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Argument type mismatch: name='{}', value='{}'", ex.getName(), ex.getValue());
        ErrorResponse error = new ErrorResponse(
                "Invalid value '" + ex.getValue() + "' for ID. Must be a positive integer",
                "400"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("An error occurred on the server", "500");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
