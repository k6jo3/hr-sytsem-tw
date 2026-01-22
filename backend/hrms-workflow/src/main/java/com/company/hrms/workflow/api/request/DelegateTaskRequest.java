package com.company.hrms.workflow.api.request;

import lombok.Data;

/**
 * 任務轉交請求
 * PUT /api/v1/workflows/tasks/{id}/delegate
 */
@Data
public class DelegateTaskRequest {

    /**
     * 任務ID (由 PathVariable 注入)
     */
    private String taskId;

    /**
     * 轉交目標人員ID
     */
    private String delegateToId;

    /**
     * 轉交原因
     */
    private String reason;
}
