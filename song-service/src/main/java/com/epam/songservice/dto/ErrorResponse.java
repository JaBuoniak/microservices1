package com.epam.songservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String errorMessage,
        String errorCode,
        Map<String, String> details
) {
    public ErrorResponse(String errorMessage, String errorCode) {
        this(errorMessage, errorCode, null);
    }
}
