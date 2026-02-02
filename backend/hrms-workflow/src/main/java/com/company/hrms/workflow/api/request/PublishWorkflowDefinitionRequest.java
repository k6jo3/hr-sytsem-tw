package com.company.hrms.workflow.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "發佈流程定義請求")
public class PublishWorkflowDefinitionRequest {
    @Schema(description = "流程定義ID")
    private String definitionId;
}
