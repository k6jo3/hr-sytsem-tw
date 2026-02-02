package com.company.hrms.workflow.api.request;

import com.company.hrms.common.query.QueryCondition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查詢流程定義列表請求")
public class GetWorkflowDefinitionListRequest {

    @Schema(description = "流程名稱")
    @QueryCondition.LIKE
    private String flowName;

    @Schema(description = "流程類型")
    @QueryCondition.EQ
    private String flowType;

    @Schema(description = "是否啟用")
    @QueryCondition.EQ
    private Boolean isActive;
}
