package com.company.hrms.reporting.application.service.export;

import org.springframework.stereotype.Service;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.ExportGovernmentFormatRequest;
import com.company.hrms.reporting.api.response.ExportFileResponse;

/**
 * 政府申報格式匯出 Service - RPT_CMD_008
 */
@Service("exportGovernmentFormatServiceImpl")
public class ExportGovernmentFormatServiceImpl 
        implements QueryApiService<ExportGovernmentFormatRequest, ExportFileResponse> {

    @Override
    public ExportFileResponse getResponse(ExportGovernmentFormatRequest req, JWTModel currentUser, String... args)
            throws Exception {
        // TODO: 實作政府申報格式匯出邏輯
        // 1. 根據 declarationType 查詢保險申報資料
        // 2. 按照勞保局/健保局格式生成檔案
        // 3. 儲存匯出記錄並返回 ExportFileResponse
        throw new UnsupportedOperationException("待實作：需要保險申報資料 Repository");
    }
}
