package com.company.hrms.reporting.application.service.dashboard;

import org.springframework.stereotype.Service;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetDashboardDetailRequest;
import com.company.hrms.reporting.api.response.DashboardDetailResponse;

/**
 * 查詢儀表板詳情 Service - RPT_QRY_010
 */
@Service("getDashboardDetailServiceImpl")
public class GetDashboardDetailServiceImpl 
        implements QueryApiService<GetDashboardDetailRequest, DashboardDetailResponse> {

    @Override
    public DashboardDetailResponse getResponse(GetDashboardDetailRequest req, JWTModel currentUser, String... args)
            throws Exception {
        // TODO: 實作儀表板詳情查詢邏輯
        // 1. 從 DashboardRepository 根據 dashboardId 查詢
        // 2. 檢查權限（isPublic 或 createdBy = currentUser）
        // 3. 轉換為 DashboardDetailResponse
        throw new UnsupportedOperationException("待實作：需要 DashboardRepository 完整實作");
    }
}
