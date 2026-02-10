package com.company.hrms.reporting.application.service.report;

import org.springframework.stereotype.Service;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetTurnoverAnalysisRequest;
import com.company.hrms.reporting.api.response.TurnoverAnalysisResponse;

/**
 * 查詢離職率分析 Service - RPT_QRY_003
 */
@Service("getTurnoverAnalysisServiceImpl")
public class GetTurnoverAnalysisServiceImpl 
        implements QueryApiService<GetTurnoverAnalysisRequest, TurnoverAnalysisResponse> {

    @Override
    public TurnoverAnalysisResponse getResponse(GetTurnoverAnalysisRequest req, JWTModel currentUser, String... args)
            throws Exception {
        // TODO: 實作離職率分析查詢邏輯
        // 1. 從 MonthlyHrStatsRepository 查詢離職統計
        // 2. 計算離職率（離職人數 / 總人數）
        // 3. 轉換為 TurnoverAnalysisResponse
        throw new UnsupportedOperationException("待實作：需要 MonthlyHrStatsRepository");
    }
}
