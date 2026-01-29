package com.company.hrms.reporting.application.service.dashboard.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.reporting.application.service.dashboard.context.CreateDashboardContext;
import com.company.hrms.reporting.domain.model.dashboard.Dashboard;
import com.company.hrms.reporting.domain.repository.IDashboardRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存 Dashboard Task
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Component
@RequiredArgsConstructor
public class SaveDashboardTask implements PipelineTask<CreateDashboardContext> {

    private final IDashboardRepository dashboardRepository;

    @Override
    public void execute(CreateDashboardContext context) throws Exception {
        // 儲存 Dashboard
        Dashboard savedDashboard = dashboardRepository.save(context.getDashboard());
        context.setDashboard(savedDashboard);
    }
}
