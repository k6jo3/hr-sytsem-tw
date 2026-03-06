package com.company.hrms.iam.api.response.system;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能開關 Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureToggleResponse {

    private String featureCode;
    private String featureName;
    private String module;
    private boolean enabled;
    private String description;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
