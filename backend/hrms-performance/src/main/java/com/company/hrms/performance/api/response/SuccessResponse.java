package com.company.hrms.performance.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用成功回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {
    private String message;
    private boolean success;

    public static SuccessResponse of(String message) {
        return SuccessResponse.builder()
                .success(true)
                .message(message)
                .build();
    }
}
