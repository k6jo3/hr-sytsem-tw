package com.company.hrms.reporting.application.service.dashboard.task;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.reporting.api.request.CreateDashboardRequest.WidgetConfigDto;
import com.company.hrms.reporting.application.service.dashboard.context.CreateDashboardContext;
import com.company.hrms.reporting.domain.model.dashboard.DashboardWidget;
import com.company.hrms.reporting.domain.model.dashboard.WidgetPosition;

/**
 * 驗證與轉換 Widget 配置 Task
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Component
public class ValidateWidgetConfigTask implements PipelineTask<CreateDashboardContext> {

    @Override
    public void execute(CreateDashboardContext context) throws Exception {
        if (context.getRequest().getWidgets() == null ||
                context.getRequest().getWidgets().isEmpty()) {
            // 沒有 Widget 也是合法的
            context.setWidgets(List.of());
            return;
        }

        // 轉換 DTO 為 Domain 物件
        List<DashboardWidget> widgets = context.getRequest().getWidgets().stream()
                .map(this::convertToWidget)
                .collect(Collectors.toList());

        // 驗證每個 Widget
        for (DashboardWidget widget : widgets) {
            widget.validate();
        }

        context.setWidgets(widgets);
    }

    /**
     * 轉換 DTO 為 Domain Widget
     */
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
