package com.company.hrms.organization.api.response.employee;

import lombok.Builder;
import lombok.Data;

/**
 * 檢查唯一性回應 DTO
 */
@Data
@Builder
public class CheckUniqueResponse {

    private boolean available;
    private String message;

    public static CheckUniqueResponse available() {
        return CheckUniqueResponse.builder()
                .available(true)
                .message("可使用")
                .build();
    }

    public static CheckUniqueResponse notAvailable(String message) {
        return CheckUniqueResponse.builder()
                .available(false)
                .message(message)
                .build();
    }
}
