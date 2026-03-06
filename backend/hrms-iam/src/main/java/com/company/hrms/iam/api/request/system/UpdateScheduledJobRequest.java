package com.company.hrms.iam.api.request.system;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新排程任務配置 Request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduledJobRequest {

    @NotBlank(message = "Cron 表達式不可為空")
    private String cronExpression;

    private Boolean enabled;
}
