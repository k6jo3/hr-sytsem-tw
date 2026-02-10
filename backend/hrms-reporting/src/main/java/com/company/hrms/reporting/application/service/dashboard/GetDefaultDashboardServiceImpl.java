package com.company.hrms.reporting.application.service.dashboard;

import org.springframework.stereotype.Service;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetDefaultDashboardRequest;
import com.company.hrms.reporting.api.response.DashboardDetailResponse;

/**
 * 查詢預設儀表板 Service - RPT_QRY_009
 */
@Service("getDefaultDashboardServiceImpl")
public class GetDefaultDashboardServiceImpl 
        implements QueryApiService<GetDefaultDashboardRequest, DashboardDetailResponse> {

    @Override
    public DashboardDetailResponse getResponse(GetDefaultDashboardRequest req, JWTModel currentUser, String... args)
            throws Exception {
        // TODO: 實作預設儀表板查詢邏輯
        // 1. 從 DashboardRepository 查詢 isDefault = true 的儀表板
        // 2. 轉換為 DashboardDetailResponse
        throw new UnsupportedOperationException("待實作：需要 DashboardRepository 完整實作");
    }
}
