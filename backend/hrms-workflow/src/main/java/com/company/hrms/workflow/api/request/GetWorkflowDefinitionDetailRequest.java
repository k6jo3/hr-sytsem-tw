package com.company.hrms.workflow.api.request;

import lombok.Data;

/**
 * 查詢流程定義詳情請求
 * GET /api/v1/workflows/definitions/{id}
 */
@Data
public class GetWorkflowDefinitionDetailRequest {
    /**
     * 流程定義ID (由 PathVariable 注入)
     */
    private String definitionId;
}
