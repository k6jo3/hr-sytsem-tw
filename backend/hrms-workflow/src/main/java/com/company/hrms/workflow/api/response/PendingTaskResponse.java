package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 待辦任務回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "待辦任務回應")
public class PendingTaskResponse {

    @Schema(description = "任務 ID")
    private String taskId;

    @Schema(description = "流程實例 ID")
    private String instanceId;

    @Schema(description = "節點名稱 (任務名稱)")
    private String taskName;

    @Schema(description = "申請人")
    private String applicantName;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "到期時間")
    private LocalDateTime dueDate;

    @Schema(description = "業務單據連結")
    private String businessUrl;
}
