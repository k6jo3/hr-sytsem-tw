package com.company.hrms.iam.api.response.system;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系統參數 Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemParameterResponse {

    private String paramCode;
    private String paramName;
    private String paramValue;
    private String paramType;
    private String module;
    private String category;
    private String description;
    private String defaultValue;
    private boolean isEncrypted;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
