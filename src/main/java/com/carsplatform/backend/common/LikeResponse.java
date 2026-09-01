package com.carsplatform.backend.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Common response DTO for like operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {
    private boolean isLiked;
    private long likesCount;
}
