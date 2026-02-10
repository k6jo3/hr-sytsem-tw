package com.company.hrms.reporting.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成報表回應
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateReportResponse {
    /** 報表 ID */
    private String reportId;
    
    /** 報表名稱 */
    private String reportName;
    
    /** 生成狀態 (PENDING, COMPLETED, FAILED) */
    private String status;
}
