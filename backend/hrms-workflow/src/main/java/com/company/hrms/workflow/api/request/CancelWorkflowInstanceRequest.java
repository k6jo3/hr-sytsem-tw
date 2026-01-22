package com.company.hrms.workflow.api.request;

import lombok.Data;

/**
 * 取消流程實例請求
 * PUT /api/v1/workflows/instances/{id}/cancel
 */
@Data
public class CancelWorkflowInstanceRequest {

    /**
     * 流程實例ID (由 PathVariable 注入)
     */
    private String instanceId;

    /**
     * 取消原因
     */
    private String reason;
}
