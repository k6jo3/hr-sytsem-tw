package com.company.hrms.workflow.api.request;

import lombok.Data;

/**
 * 查詢任務詳情請求
 * GET /api/v1/workflows/tasks/{id}
 */
@Data
public class GetTaskDetailRequest {

    /**
     * 任務ID (由 PathVariable 注入)
     */
    private String taskId;
}
