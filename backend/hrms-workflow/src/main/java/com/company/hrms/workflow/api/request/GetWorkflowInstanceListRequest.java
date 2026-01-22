package com.company.hrms.workflow.api.request;

import lombok.Data;

/**
 * 查詢流程實例列表請求
 * GET /api/v1/workflows/instances
 */
@Data
public class GetWorkflowInstanceListRequest {

    /**
     * 流程類型篩選
     */
    private String flowType;

    /**
     * 狀態篩選 (RUNNING/COMPLETED/CANCELLED/REJECTED)
     */
    private String status;

    /**
     * 申請人 ID
     */
    private String applicantId;

    /**
     * 開始日期起
     */
    private String startDateFrom;

    /**
     * 開始日期迄
     */
    private String startDateTo;

    /**
     * 頁碼（從1開始）
     */
    private Integer page;

    /**
     * 每頁筆數
     */
    private Integer pageSize;
}
