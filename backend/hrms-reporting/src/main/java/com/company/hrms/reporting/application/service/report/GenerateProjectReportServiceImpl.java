package com.company.hrms.reporting.application.service.report;

import org.springframework.stereotype.Service;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.reporting.api.request.GenerateProjectReportRequest;
import com.company.hrms.reporting.api.response.GenerateReportResponse;

/**
 * 生成專案成本報表 Service - RPT_CMD_002
 */
@Service("generateProjectReportServiceImpl")
public class GenerateProjectReportServiceImpl 
        implements CommandApiService<GenerateProjectReportRequest, GenerateReportResponse> {

    @Override
    public GenerateReportResponse execCommand(GenerateProjectReportRequest req, JWTModel currentUser, String... args) 
            throws Exception {
        // TODO: 實作專案成本報表生成邏輯
        // 1. 從 ProjectCostSnapshotRepository 查詢專案成本資料
        // 2. 彙總人力成本、直接成本、間接成本
        // 3. 生成報表並儲存到 ReportRepository
        throw new UnsupportedOperationException("待實作：需要 ProjectCostSnapshotRepository 和 ReportRepository");
    }
}
