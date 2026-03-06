package com.company.hrms.iam.api.request.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 切換功能開關 Request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToggleFeatureRequest {

    private Boolean enabled;
}
