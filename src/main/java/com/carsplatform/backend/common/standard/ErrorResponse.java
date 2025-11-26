package com.carsplatform.backend.common.standard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
    private Map<String, String> errors;
}
