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
 * 駁回任務回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "駁回任務回應")
public class RejectTaskResponse {

    @Schema(description = "流程實例 ID", example = "INS-20231020-001")
    private String instanceId;

    @Schema(description = "任務 ID", example = "TASK-001")
    private String taskId;

    @Schema(description = "任務狀態", example = "REJECTED")
    private TaskStatus status;

    @Schema(description = "流程實例狀態", example = "REJECTED")
    private InstanceStatus instanceStatus;

    @Schema(description = "完成時間")
    private LocalDateTime completedAt;

    @Schema(description = "訊息", example = "任務已駁回")
    private String message;
}
