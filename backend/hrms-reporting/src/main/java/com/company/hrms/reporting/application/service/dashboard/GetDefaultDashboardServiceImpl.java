package com.company.hrms.reporting.application.service.dashboard;

import org.springframework.stereotype.Service;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetDashboardDetailRequest;
import com.company.hrms.reporting.api.request.GetDefaultDashboardRequest;
import com.company.hrms.reporting.api.response.DashboardDetailResponse;
import com.company.hrms.reporting.domain.model.dashboard.Dashboard;
import com.company.hrms.reporting.domain.repository.IDashboardRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢預設儀表板 Service - RPT_QRY_009
 */
@Service("getDefaultDashboardServiceImpl")
@RequiredArgsConstructor
public class GetDefaultDashboardServiceImpl
        implements QueryApiService<GetDefaultDashboardRequest, DashboardDetailResponse> {

    private final IDashboardRepository dashboardRepository;
    private final GetDashboardDetailServiceImpl detailService;

    @Override
    public DashboardDetailResponse getResponse(GetDefaultDashboardRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // 使用 QueryBuilder 建立查詢條件
        QueryGroup queryGroup = QueryBuilder.where()
                .eq("tenantId", currentUser.getTenantId())
                .eq("isDefault", true)
                .build();

        Dashboard dashboard = dashboardRepository.findOne(queryGroup)
                .orElseThrow(() -> new DomainException("預設儀表板不存在"));

        // 重用 GetDashboardDetailServiceImpl 的轉換邏輯 (包含 Widget 數據抓取)
        GetDashboardDetailRequest detailReq = new GetDashboardDetailRequest();
        detailReq.setDashboardId(dashboard.getId().getValue().toString());

        return detailService.getResponse(detailReq, currentUser);
    }
}
