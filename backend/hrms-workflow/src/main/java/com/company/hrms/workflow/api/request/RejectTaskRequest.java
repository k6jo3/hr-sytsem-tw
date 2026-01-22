package com.company.hrms.workflow.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 駁回任務請求 DTO
 */
@Data
@Schema(description = "駁回任務請求")
public class RejectTaskRequest {

    @Schema(description = "流程實例 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "INS-20231020-001")
    private String instanceId;

    @Schema(description = "任務 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "TASK-001")
    private String taskId;

    @Schema(description = "簽核者 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "EMP055")
    private String approverId;

    @Schema(description = "駁回原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "資料不全，請補件")
    private String reason;
}
