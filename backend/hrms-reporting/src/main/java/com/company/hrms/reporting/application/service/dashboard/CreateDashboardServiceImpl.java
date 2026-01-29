package com.company.hrms.reporting.application.service.dashboard;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.reporting.api.request.CreateDashboardRequest;
import com.company.hrms.reporting.api.response.CreateDashboardResponse;
import com.company.hrms.reporting.application.service.dashboard.context.CreateDashboardContext;
import com.company.hrms.reporting.application.service.dashboard.task.CreateDashboardAggregateTask;
import com.company.hrms.reporting.application.service.dashboard.task.SaveDashboardTask;
import com.company.hrms.reporting.application.service.dashboard.task.ValidateWidgetConfigTask;

import lombok.RequiredArgsConstructor;

/**
 * 建立儀表板 Service
 * 
 * <p>
 * 使用 Business Pipeline 編排業務流程
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service("createDashboardServiceImpl")
@RequiredArgsConstructor
public class CreateDashboardServiceImpl
        implements CommandApiService<CreateDashboardRequest, CreateDashboardResponse> {

    private final ValidateWidgetConfigTask validateWidgetConfigTask;
    private final CreateDashboardAggregateTask createDashboardAggregateTask;
    private final SaveDashboardTask saveDashboardTask;

    @Override
    @Transactional
    public CreateDashboardResponse execCommand(
            CreateDashboardRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 建立 Context
        CreateDashboardContext context = new CreateDashboardContext(
                request,
                currentUser.getTenantId(),
                currentUser.getUserId());

        // 執行 Business Pipeline
        BusinessPipeline.start(context)
                .next(validateWidgetConfigTask) // 1. 驗證與轉換 Widget 配置
                .next(createDashboardAggregateTask) // 2. 建立 Dashboard 聚合根
                .next(saveDashboardTask) // 3. 儲存到資料庫
                .execute();

        // 建立回應
        return CreateDashboardResponse.builder()
                .dashboardId(context.getDashboard().getId().getValue())
                .dashboardName(context.getDashboard().getDashboardName())
                .createdAt(context.getDashboard().getCreatedAt())
                .message("儀表板建立成功")
                .build();
    }
}
