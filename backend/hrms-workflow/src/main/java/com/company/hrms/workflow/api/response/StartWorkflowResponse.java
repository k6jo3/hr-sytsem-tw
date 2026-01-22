package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;

import com.company.hrms.workflow.domain.model.enums.InstanceStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 發起流程回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "發起流程回應")
public class StartWorkflowResponse {

    @Schema(description = "流程實例 ID", example = "INS-20231020-001")
    private String instanceId;

    @Schema(description = "流程定義 ID", example = "DEF-20231001-001")
    private String definitionId;

    @Schema(description = "流程狀態", example = "RUNNING")
    private InstanceStatus status;

    @Schema(description = "發起時間")
    private LocalDateTime startedAt;

    @Schema(description = "訊息", example = "流程已成功發起")
    private String message;
}
