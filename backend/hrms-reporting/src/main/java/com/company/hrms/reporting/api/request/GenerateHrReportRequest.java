package com.company.hrms.reporting.api.request;

import lombok.Data;

/**
 * 生成人力資源報表請求
 */
@Data
public class GenerateHrReportRequest {
    /** 報表類型 (EMPLOYEE_ROSTER, ATTENDANCE_STATISTICS, HEADCOUNT, etc.) */
    private String reportType;
    
    /** 組織 ID */
    private String organizationId;
    
    /** 開始年月 (YYYY-MM) */
    private String startYearMonth;
    
    /** 結束年月 (YYYY-MM) */
    private String endYearMonth;
}
