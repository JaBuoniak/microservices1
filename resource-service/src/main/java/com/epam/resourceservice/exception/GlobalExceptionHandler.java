package com.epam.resourceservice.exception;

import com.epam.resourceservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        String contentType = ex.getContentType() != null ? ex.getContentType().toString() : "unknown";
        ErrorResponse error = new ErrorResponse(
                "Invalid file format: " + contentType + ". Only MP3 files are allowed",
                "400"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidAudioFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAudioFormat(InvalidAudioFormatException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("An unexpected error occurred", "500");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
