package com.company.hrms.workflow.api.request;

import java.util.Map;

import com.company.hrms.workflow.domain.model.enums.FlowType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 發起流程請求 DTO
 */
@Data
@Schema(description = "發起流程請求")
public class StartWorkflowRequest {

    @Schema(description = "流程類型 (若未指定 definitionId 則依此查找最新版)", example = "LEAVE_REQUEST")
    private FlowType flowType;

    @Schema(description = "流程定義 I", example = "DEF-20231001-001")
    private String definitionId;

    @Schema(description = "申請人 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "EMP001")
    private String applicantId;

    @Schema(description = "業務單據 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "LEA-20231020-005")
    private String businessId;

    @Schema(description = "業務單據類型", requiredMode = Schema.RequiredMode.REQUIRED, example = "LEAVE")
    private String businessType;

    @Schema(description = "流程變數 (用於路由判斷)", example = "{\"days\": 3}")
    private Map<String, Object> variables;

    @Schema(description = "摘要說明", example = "事假申請 - 3天")
    private String summary;
}
