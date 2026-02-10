package com.company.hrms.reporting.application.service.report;

import org.springframework.stereotype.Service;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.reporting.api.request.GenerateHrReportRequest;
import com.company.hrms.reporting.api.response.GenerateReportResponse;

/**
 * 生成人力資源報表 Service - RPT_CMD_001
 */
@Service("generateHrReportServiceImpl")
public class GenerateHrReportServiceImpl 
        implements CommandApiService<GenerateHrReportRequest, GenerateReportResponse> {

    @Override
    public GenerateReportResponse execCommand(GenerateHrReportRequest req, JWTModel currentUser, String... args) 
            throws Exception {
        // TODO: 實作人力資源報表生成邏輯
        // 1. 根據 reportType 選擇對應的 ReadModel Repository
        // 2. 查詢指定期間的資料
        // 3. 生成報表並儲存到 ReportRepository
        throw new UnsupportedOperationException("待實作：需要 ReportRepository 和各種 ReadModel Repository");
    }
}
