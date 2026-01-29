package com.company.hrms.reporting.application.service.dashboard;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.reporting.api.request.UpdateDashboardWidgetsRequest;
import com.company.hrms.reporting.api.request.UpdateDashboardWidgetsRequest.WidgetConfigDto;
import com.company.hrms.reporting.api.response.UpdateDashboardWidgetsResponse;
import com.company.hrms.reporting.domain.model.dashboard.Dashboard;
import com.company.hrms.reporting.domain.model.dashboard.DashboardId;
import com.company.hrms.reporting.domain.model.dashboard.DashboardWidget;
import com.company.hrms.reporting.domain.model.dashboard.WidgetPosition;
import com.company.hrms.reporting.domain.repository.IDashboardRepository;

import lombok.RequiredArgsConstructor;

/**
 * 更新 Widget 配置 Service
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service("updateDashboardWidgetsServiceImpl")
@RequiredArgsConstructor
public class UpdateDashboardWidgetsServiceImpl
        implements CommandApiService<UpdateDashboardWidgetsRequest, UpdateDashboardWidgetsResponse> {

    private final IDashboardRepository dashboardRepository;

    @Override
    @Transactional
    public UpdateDashboardWidgetsResponse execCommand(
            UpdateDashboardWidgetsRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 查詢 Dashboard
        DashboardId dashboardId = DashboardId.of(UUID.fromString(request.getDashboardId()));
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new IllegalArgumentException("儀表板不存在"));

        // 轉換 Widget 配置
        List<DashboardWidget> widgets = request.getWidgets().stream()
                .map(this::convertToWidget)
                .collect(Collectors.toList());

        // 更新 Widget
        dashboard.updateWidgets(widgets);

        // 儲存
        dashboardRepository.save(dashboard);

        return UpdateDashboardWidgetsResponse.builder()
                .message("Widget 配置更新成功")
                .updatedCount(widgets.size())
                .build();
    }

    private DashboardWidget convertToWidget(WidgetConfigDto dto) {
        WidgetPosition position = new WidgetPosition(
                dto.getX() != null ? dto.getX() : 0,
                dto.getY() != null ? dto.getY() : 0,
                dto.getW() != null ? dto.getW() : 3,
                dto.getH() != null ? dto.getH() : 2);

        DashboardWidget.WidgetType widgetType = dto.getWidgetType() != null
                ? DashboardWidget.WidgetType.valueOf(dto.getWidgetType())
                : DashboardWidget.WidgetType.KPI_CARD;

        return new DashboardWidget(
                dto.getWidgetId(),
                widgetType,
                dto.getTitle(),
                dto.getDataSource(),
                position,
                dto.getRefreshInterval() != null ? dto.getRefreshInterval() : 3600,
                dto.getStyleConfig(),
                dto.getChartConfig());
    }
}
