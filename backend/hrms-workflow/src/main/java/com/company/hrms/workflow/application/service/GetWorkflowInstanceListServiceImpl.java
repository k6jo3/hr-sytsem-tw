package com.company.hrms.workflow.application.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.workflow.api.request.GetWorkflowInstanceListRequest;
import com.company.hrms.workflow.api.response.WorkflowInstanceListItemResponse;
import com.company.hrms.workflow.application.service.context.GetWorkflowInstanceListContext;
import com.company.hrms.workflow.application.service.task.LoadWorkflowInstanceListTask;
import com.company.hrms.workflow.application.service.task.MapWorkflowInstanceToResponseTask;

import lombok.RequiredArgsConstructor;

/**
 * 查詢流程實例列表服務
 * GET /api/v1/workflows/instances
 * 
 * 使用 Business Pipeline 模式實作：
 * 1. LoadWorkflowInstanceListTask - 使用 QueryBuilder 載入資料
 * 2. MapWorkflowInstanceToResponseTask - 使用 ObjectMapper 轉換回應
 */
@Service("getWorkflowInstanceListServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetWorkflowInstanceListServiceImpl
        implements QueryApiService<GetWorkflowInstanceListRequest, Page<WorkflowInstanceListItemResponse>> {

    private final LoadWorkflowInstanceListTask loadWorkflowInstanceListTask;
    private final MapWorkflowInstanceToResponseTask mapWorkflowInstanceToResponseTask;

    @Override
    public Page<WorkflowInstanceListItemResponse> getResponse(
            GetWorkflowInstanceListRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 1. 建立 Context
        GetWorkflowInstanceListContext ctx = new GetWorkflowInstanceListContext();
        ctx.setFlowType(request.getFlowType());
        ctx.setStatus(request.getStatus());
        ctx.setApplicantId(request.getApplicantId());
        ctx.setStartDateFrom(request.getStartDateFrom());
        ctx.setStartDateTo(request.getStartDateTo());
        ctx.setPage(request.getPage());
        ctx.setPageSize(request.getPageSize());

        // 2. 執行 Pipeline
        BusinessPipeline.start(ctx)
                .next(loadWorkflowInstanceListTask)
                .next(mapWorkflowInstanceToResponseTask)
                .execute();

        // 3. 回傳結果
        return ctx.getResult();
    }
}
