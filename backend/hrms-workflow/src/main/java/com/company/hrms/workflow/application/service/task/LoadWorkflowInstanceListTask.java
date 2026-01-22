package com.company.hrms.workflow.application.service.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.workflow.application.service.context.GetWorkflowInstanceListContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入流程實例列表 Task
 * 使用 QueryBuilder 建立查詢條件
 */
@Component
@RequiredArgsConstructor
public class LoadWorkflowInstanceListTask implements PipelineTask<GetWorkflowInstanceListContext> {

    private final IWorkflowInstanceRepository instanceRepository;

    @Override
    public void execute(GetWorkflowInstanceListContext ctx) throws Exception {
        // 使用 QueryBuilder 建立查詢條件
        QueryBuilder queryBuilder = QueryBuilder.where();

        // 根據條件動態添加過濾
        if (ctx.getFlowType() != null && !ctx.getFlowType().isBlank()) {
            queryBuilder.and("flowType", Operator.EQ, ctx.getFlowType());
        }

        if (ctx.getStatus() != null && !ctx.getStatus().isBlank()) {
            queryBuilder.and("status", Operator.EQ, ctx.getStatus());
        }

        if (ctx.getApplicantId() != null && !ctx.getApplicantId().isBlank()) {
            queryBuilder.and("applicantId", Operator.EQ, ctx.getApplicantId());
        }

        if (ctx.getStartDateFrom() != null && !ctx.getStartDateFrom().isBlank()) {
            queryBuilder.and("startedAt", Operator.GTE, ctx.getStartDateFrom());
        }

        if (ctx.getStartDateTo() != null && !ctx.getStartDateTo().isBlank()) {
            queryBuilder.and("startedAt", Operator.LTE, ctx.getStartDateTo());
        }

        // 建立 QueryGroup
        QueryGroup queryGroup = queryBuilder.build();

        // 建立分頁參數
        Pageable pageable = PageRequest.of(
                ctx.getPage() != null ? ctx.getPage() - 1 : 0,
                ctx.getPageSize() != null ? ctx.getPageSize() : 20,
                Sort.by(Sort.Direction.DESC, "startedAt"));

        // 執行查詢
        Page<WorkflowInstance> instancePage = instanceRepository.search(queryGroup, pageable);
        ctx.setInstancePage(instancePage);
    }
}
