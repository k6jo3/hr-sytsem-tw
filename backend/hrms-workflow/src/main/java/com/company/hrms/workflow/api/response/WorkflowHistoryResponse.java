package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;
import java.util.List;

import com.company.hrms.workflow.domain.model.enums.InstanceStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程歷史回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "流程歷史回應")
public class WorkflowHistoryResponse {

    @Schema(description = "流程實例 ID")
    private String instanceId;

    @Schema(description = "流程定義 ID")
    private String definitionId;

    @Schema(description = "申請人 ID")
    private String applicantId;

    @Schema(description = "申請人姓名")
    private String applicantName;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "目前狀態")
    private InstanceStatus status;

    @Schema(description = "建立時間 (開始時間)")
    private LocalDateTime startedAt;

    @Schema(description = "結束時間")
    private LocalDateTime completedAt;

    @Schema(description = "任務歷程列表")
    private List<TaskHistoryDTO> tasks;
}
