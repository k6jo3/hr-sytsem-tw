package com.company.hrms.reporting.application.service.export;

import org.springframework.stereotype.Service;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.ExportPdfRequest;

/**
 * 匯出 PDF 報表 Service - RPT_CMD_004
 */
@Service("exportPdfServiceImpl")
public class ExportPdfServiceImpl 
        implements QueryApiService<ExportPdfRequest, byte[]> {

    @Override
    public byte[] getResponse(ExportPdfRequest req, JWTModel currentUser, String... args)
            throws Exception {
        // TODO: 實作 PDF 匯出邏輯
        // 1. 根據 reportType 查詢報表資料
        // 2. 使用 PDF 生成工具（如 iText）生成 PDF
        // 3. 儲存匯出記錄到 ExportRecordRepository
        throw new UnsupportedOperationException("待實作：需要 PDF 生成工具和相關 Repository");
    }
}
