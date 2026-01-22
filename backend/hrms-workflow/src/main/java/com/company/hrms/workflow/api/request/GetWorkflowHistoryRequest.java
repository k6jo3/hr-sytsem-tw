package com.company.hrms.workflow.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查詢流程歷史請求
 */
@Data
@Schema(description = "查詢流程歷史請求")
public class GetWorkflowHistoryRequest {

    @Schema(description = "流程實例 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "INS-20231020-001")
    private String instanceId;
}
