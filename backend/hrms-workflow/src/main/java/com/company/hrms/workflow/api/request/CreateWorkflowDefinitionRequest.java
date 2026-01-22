package com.company.hrms.workflow.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "建立流程定義請求")
public class CreateWorkflowDefinitionRequest {

    @Schema(description = "流程名稱")
    private String flowName;

    @Schema(description = "流程類型 (唯一代碼)")
    private String flowType;

    @Schema(description = "節點設定 (JSON)")
    private String nodes;

    @Schema(description = "連線設定 (JSON)")
    private String edges;
}
