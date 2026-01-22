package com.company.hrms.workflow.api.request;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 核准任務請求 DTO
 */
@Data
@Schema(description = "核准任務請求")
public class ApproveTaskRequest {

    @Schema(description = "流程實例 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "INS-20231020-001")
    private String instanceId;

    @Schema(description = "任務 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "TASK-001")
    private String taskId;

    @Schema(description = "簽核者 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "EMP055")
    private String approverId;

    @Schema(description = "簽核意見", example = "同意，請盡速辦理")
    private String comment;

    @Schema(description = "流程輸出變數 (可能影響後續路由)", example = "{\"approved\": true}")
    private Map<String, Object> variables;
}
