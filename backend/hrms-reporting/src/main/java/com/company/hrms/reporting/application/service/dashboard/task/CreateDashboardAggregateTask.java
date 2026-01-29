package com.company.hrms.reporting.application.service.dashboard.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.reporting.application.service.dashboard.context.CreateDashboardContext;
import com.company.hrms.reporting.domain.model.dashboard.Dashboard;

/**
 * 建立 Dashboard 聚合根 Task
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Component
public class CreateDashboardAggregateTask implements PipelineTask<CreateDashboardContext> {

    @Override
    public void execute(CreateDashboardContext context) throws Exception {
        // 建立 Dashboard 聚合根
        Dashboard dashboard = Dashboard.create(
                context.getRequest().getDashboardName(),
                context.getRequest().getDescription(),
                UUID.fromString(context.getUserId()),
                context.getTenantId(),
                context.getRequest().getIsPublic() != null
                        ? context.getRequest().getIsPublic()
                        : false);

        // 新增 Widgets
        if (context.getWidgets() != null && !context.getWidgets().isEmpty()) {
            for (var widget : context.getWidgets()) {
                dashboard.addWidget(widget);
            }
        }

        context.setDashboard(dashboard);
    }
}
