package com.company.hrms.reporting.api.request;

import lombok.Data;

/**
 * 生成專案成本報表請求
 */
@Data
public class GenerateProjectReportRequest {
    /** 專案 ID */
    private String projectId;
    
    /** 組織 ID */
    private String organizationId;
    
    /** 開始年月 (YYYY-MM) */
    private String startYearMonth;
    
    /** 結束年月 (YYYY-MM) */
    private String endYearMonth;
}
