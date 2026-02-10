package com.company.hrms.reporting.application.service.export;

import org.springframework.stereotype.Service;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.DownloadExportFileRequest;

/**
 * 下載匯出檔案 Service - RPT_QRY_013
 */
@Service("downloadExportFileServiceImpl")
public class DownloadExportFileServiceImpl 
        implements QueryApiService<DownloadExportFileRequest, byte[]> {

    @Override
    public byte[] getResponse(DownloadExportFileRequest req, JWTModel currentUser, String... args)
            throws Exception {
        // TODO: 實作匯出檔案下載邏輯
        // 1. 從 ExportRecordRepository 查詢匯出記錄
        // 2. 檢查權限（userId = currentUser）
        // 3. 從檔案儲存系統讀取檔案內容
        throw new UnsupportedOperationException("待實作：需要 ExportRecordRepository 和檔案儲存服務");
    }
}
