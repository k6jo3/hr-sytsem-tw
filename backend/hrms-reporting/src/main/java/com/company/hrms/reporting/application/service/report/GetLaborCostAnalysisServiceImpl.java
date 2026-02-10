package com.company.hrms.reporting.application.service.report;

import org.springframework.stereotype.Service;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetLaborCostAnalysisRequest;
import com.company.hrms.reporting.api.response.LaborCostAnalysisResponse;

/**
 * 查詢人力成本分析 Service - RPT_QRY_006
 */
@Service("getLaborCostAnalysisServiceImpl")
public class GetLaborCostAnalysisServiceImpl 
        implements QueryApiService<GetLaborCostAnalysisRequest, LaborCostAnalysisResponse> {

    @Override
    public LaborCostAnalysisResponse getResponse(GetLaborCostAnalysisRequest req, JWTModel currentUser, String... args)
            throws Exception {
        // TODO: 實作人力成本分析查詢邏輯
        // 1. 從 LaborCostViewRepository 查詢人力成本統計
        // 2. 包含薪資、加班費、保險費等
        // 3. 轉換為 LaborCostAnalysisResponse
        throw new UnsupportedOperationException("待實作：需要 LaborCostViewRepository");
    }
}
