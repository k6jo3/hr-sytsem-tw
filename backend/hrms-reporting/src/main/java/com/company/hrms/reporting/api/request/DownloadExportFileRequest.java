package com.company.hrms.reporting.api.request;

import lombok.Data;

/**
 * 下載匯出檔案請求
 */
@Data
public class DownloadExportFileRequest {
    /** 匯出 ID */
    private String exportId;
}
