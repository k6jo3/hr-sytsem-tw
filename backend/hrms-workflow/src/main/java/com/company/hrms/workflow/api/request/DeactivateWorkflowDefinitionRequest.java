package com.company.hrms.workflow.api.request;

import lombok.Data;

/**
 * 停用流程定義請求
 * PUT /api/v1/workflows/definitions/{id}/deactivate
 */
@Data
public class DeactivateWorkflowDefinitionRequest {

    /**
     * 流程定義ID (由 PathVariable 注入)
     */
    private String definitionId;
}
