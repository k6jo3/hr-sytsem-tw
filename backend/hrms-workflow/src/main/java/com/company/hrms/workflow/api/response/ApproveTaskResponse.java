package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;

import com.company.hrms.workflow.domain.model.enums.InstanceStatus;
import com.company.hrms.workflow.domain.model.enums.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 核准任務回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "核准任務回應")
public class ApproveTaskResponse {

    @Schema(description = "流程實例 ID", example = "INS-20231020-001")
    private String instanceId;

    @Schema(description = "任務 ID", example = "TASK-001")
    private String taskId;

    @Schema(description = "任務狀態", example = "COMPLETED")
    private TaskStatus status;

    @Schema(description = "流程實例狀態", example = "RUNNING")
    private InstanceStatus instanceStatus;

    @Schema(description = "完成時間")
    private LocalDateTime completedAt;

    @Schema(description = "訊息", example = "任務已核准")
    private String message;
}
