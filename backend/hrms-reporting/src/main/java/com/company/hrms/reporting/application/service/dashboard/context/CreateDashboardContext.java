package com.company.hrms.reporting.application.service.dashboard.context;

import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.reporting.api.request.CreateDashboardRequest;
import com.company.hrms.reporting.domain.model.dashboard.Dashboard;
import com.company.hrms.reporting.domain.model.dashboard.DashboardWidget;

import lombok.Getter;
import lombok.Setter;

/**
 * 建立儀表板 Context
 * 
 * <p>
 * 用於 Business Pipeline 的資料傳遞
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Getter
@Setter
public class CreateDashboardContext extends PipelineContext {

    private CreateDashboardRequest request;
    private String tenantId;
    private String userId;

    // 處理過程中的資料
    private List<DashboardWidget> widgets;
    private Dashboard dashboard;

    public CreateDashboardContext(CreateDashboardRequest request, String tenantId, String userId) {
        this.request = request;
        this.tenantId = tenantId;
        this.userId = userId;
    }
}
