package com.company.hrms.reporting.application.service.dashboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetDashboardDetailRequest;
import com.company.hrms.reporting.api.response.DashboardDetailResponse;
import com.company.hrms.reporting.domain.model.dashboard.Dashboard;
import com.company.hrms.reporting.domain.model.dashboard.DashboardId;
import com.company.hrms.reporting.domain.model.dashboard.DashboardWidget;
import com.company.hrms.reporting.domain.repository.IDashboardRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢儀表板詳情 Service - RPT_QRY_011
 */
@Service("getDashboardDetailServiceImpl")
@RequiredArgsConstructor
public class GetDashboardDetailServiceImpl
        implements QueryApiService<GetDashboardDetailRequest, DashboardDetailResponse> {

    private final IDashboardRepository dashboardRepository;
    private final WidgetDataOrchestrator widgetDataOrchestrator;

    @Override
    @Transactional(readOnly = true)
    public DashboardDetailResponse getResponse(GetDashboardDetailRequest req, JWTModel currentUser, String... args)
            throws Exception {

        DashboardId dashboardId = DashboardId.of(req.getDashboardId());

        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new DomainException("儀表板不存在: " + req.getDashboardId()));

        boolean isOwner = dashboard.getOwnerId().equals(UUID.fromString(currentUser.getUserId()));
        boolean isAdmin = currentUser.hasRole("ADMIN");

        if (!dashboard.isPublic() && !isOwner && !isAdmin) {
            throw new DomainException("無權存取此儀表板");
        }

        DashboardDetailResponse response = new DashboardDetailResponse();
        response.setDashboardId(dashboard.getId().getValue().toString());
        response.setDashboardName(dashboard.getDashboardName());
        response.setDescription(dashboard.getDescription());
        response.setIsDefault(dashboard.isDefault());

        if (dashboard.getWidgets() != null) {
            response.setWidgets(dashboard.getWidgets().stream()
                    .map(w -> toWidgetDetail(w, currentUser))
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private DashboardDetailResponse.WidgetDetail toWidgetDetail(DashboardWidget widget, JWTModel currentUser) {
        DashboardDetailResponse.WidgetDetail detail = new DashboardDetailResponse.WidgetDetail();
        detail.setWidgetId(widget.getWidgetId());
        detail.setWidgetType(widget.getWidgetType().name());
        detail.setTitle(widget.getTitle());

        Map<String, Object> config = new HashMap<>();
        if (widget.getPosition() != null) {
            Map<String, Integer> pos = new HashMap<>();
            pos.put("x", widget.getPosition().getX());
            pos.put("y", widget.getPosition().getY());
            pos.put("w", widget.getPosition().getW());
            pos.put("h", widget.getPosition().getH());
            config.put("position", pos);
        }
        config.put("dataSource", widget.getDataSource());
        config.put("refreshInterval", widget.getRefreshInterval());
        config.put("styleConfig", widget.getStyleConfig());
        config.put("chartConfig", widget.getChartConfig());

        detail.setConfig(config);

        // 抓取組件實際數據
        detail.setData(widgetDataOrchestrator.fetchData(widget.getDataSource(), currentUser));

        return detail;
    }
}
