package com.company.hrms.iam.api.request.system;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新系統參數 Request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSystemParameterRequest {

    @NotBlank(message = "參數值不可為空")
    private String paramValue;
}
