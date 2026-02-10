package com.company.hrms.reporting.application.service.report;

import org.springframework.stereotype.Service;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetUtilizationRateRequest;
import com.company.hrms.reporting.api.response.UtilizationRateResponse;

/**
 * 查詢稼動率分析 Service - RPT_QRY_005
 */
@Service("getUtilizationRateServiceImpl")
public class GetUtilizationRateServiceImpl 
        implements QueryApiService<GetUtilizationRateRequest, UtilizationRateResponse> {

    @Override
    public UtilizationRateResponse getResponse(GetUtilizationRateRequest req, JWTModel currentUser, String... args)
            throws Exception {
        // TODO: 實作稼動率分析查詢邏輯
        // 1. 從 ProjectCostSnapshotRepository 查詢專案工時統計
        // 2. 計算稼動率（計費工時 / 總工時）
        // 3. 轉換為 UtilizationRateResponse
        throw new UnsupportedOperationException("待實作：需要 ProjectCostSnapshotRepository");
    }
}
