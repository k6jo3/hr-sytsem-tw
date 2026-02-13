package com.company.hrms.reporting.api.request;

import lombok.Data;

/**
 * 匯出 PDF 報表請求
 */
@Data
public class ExportPdfRequest {
    /** 報表類型 */
    private String reportType;

    /** 查詢條件 */
    private java.util.Map<String, Object> filters;

    /** 檔案名稱 */
    private String fileName;
}
