package com.company.hrms.reporting.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 匯出檔案回應
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportFileResponse {
    /** 匯出 ID */
    private String exportId;
    
    /** 檔案名稱 */
    private String fileName;
    
    /** 檔案 URL (可選) */
    private String fileUrl;
    
    /** 狀態 (PENDING, COMPLETED, FAILED) */
    private String status;
}
