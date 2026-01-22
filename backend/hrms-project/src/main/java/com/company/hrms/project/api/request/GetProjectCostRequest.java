package com.company.hrms.project.api.request;

import lombok.Data;

/**
 * 查詢專案成本分析請求
 */
@Data
public class GetProjectCostRequest {

    /**
     * 專案 ID
     */
    private String projectId;

    /**
     * 期間起 (YYYY-MM)
     */
    private String periodFrom;

    /**
     * 期間迄 (YYYY-MM)
     */
    private String periodTo;
}
