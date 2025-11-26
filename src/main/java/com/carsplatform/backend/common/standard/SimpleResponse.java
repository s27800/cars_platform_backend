package com.carsplatform.backend.common.standard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SimpleResponse {
    private String message;
    private boolean success;
}
