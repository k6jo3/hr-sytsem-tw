package com.company.hrms.reporting.api.request;

import lombok.Data;

/**
 * 政府申報格式匯出請求
 */
@Data
public class ExportGovernmentFormatRequest {
    /** 申報類型 (LABOR_INSURANCE, HEALTH_INSURANCE, PENSION) */
    private String declarationType;
    
    /** 組織 ID */
    private String organizationId;
    
    /** 年月 (YYYY-MM) */
    private String yearMonth;
}
