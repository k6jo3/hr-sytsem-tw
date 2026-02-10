package com.company.hrms.reporting.api.request;

import lombok.Data;

/**
 * 匯出 PDF 報表請求
 */
@Data
public class ExportPdfRequest {
    /** 報表類型 */
    private String reportType;
    
    /** 組織 ID */
    private String organizationId;
    
    /** 年月 (YYYY-MM) */
    private String yearMonth;
    
    /** 檔案名稱 */
    private String fileName;
}
