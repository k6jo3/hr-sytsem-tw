package com.company.hrms.reporting.application.service.dashboard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetDashboardListRequest;
import com.company.hrms.reporting.api.response.DashboardListResponse;
import com.company.hrms.reporting.api.response.DashboardListResponse.DashboardSummary;
import com.company.hrms.reporting.domain.model.dashboard.Dashboard;
import com.company.hrms.reporting.domain.repository.IDashboardRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢儀表板列表 Service
 * 
 * <p>
 * 使用 Fluent Query Engine 自動建立查詢條件
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service("getDashboardListServiceImpl")
@RequiredArgsConstructor
public class GetDashboardListServiceImpl
        implements QueryApiService<GetDashboardListRequest, DashboardListResponse> {

    private final IDashboardRepository dashboardRepository;

    @Override
    public DashboardListResponse getResponse(
            GetDashboardListRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 設定租戶ID (多租戶隔離)
        request.setTenantId(currentUser.getTenantId());

        // 使用 QueryBuilder 自動從 DTO 建立查詢條件
        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .build();

        // 建立分頁參數
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize());

        // 執行查詢
        Page<Dashboard> page = dashboardRepository.findPage(query, pageable);

        // 轉換為回應
        var summaries = page.getContent().stream()
                .map(this::toDashboardSummary)
                .toList();

        return DashboardListResponse.builder()
                .content(summaries)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .build();
    }

    /**
     * 轉換為儀表板摘要
     */
    private DashboardSummary toDashboardSummary(Dashboard dashboard) {
        return DashboardSummary.builder()
                .dashboardId(dashboard.getId().getValue())
                .dashboardName(dashboard.getDashboardName())
                .description(dashboard.getDescription())
                .isPublic(dashboard.isPublic())
                .isDefault(dashboard.isDefault())
                .widgetCount(dashboard.getWidgets() != null ? dashboard.getWidgets().size() : 0)
                .createdAt(dashboard.getCreatedAt())
                .updatedAt(dashboard.getUpdatedAt())
                .build();
    }
}
