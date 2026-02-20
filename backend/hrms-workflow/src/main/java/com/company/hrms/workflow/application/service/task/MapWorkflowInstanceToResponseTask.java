package com.company.hrms.workflow.application.service.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.api.response.WorkflowInstanceListItemResponse;
import com.company.hrms.workflow.application.service.context.GetWorkflowInstanceListContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;

import lombok.RequiredArgsConstructor;

/**
 * 轉換流程實例為回應 DTO Task
 * 使用 ObjectMapper 進行物件轉換
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class MapWorkflowInstanceToResponseTask implements PipelineTask<GetWorkflowInstanceListContext> {

    @Override
    public void execute(GetWorkflowInstanceListContext ctx) throws Exception {
        Page<WorkflowInstance> instancePage = ctx.getInstancePage();

        // 使用 ObjectMapper 轉換
        List<WorkflowInstanceListItemResponse> responseDtos = instancePage.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        // 建立分頁回應
        Page<WorkflowInstanceListItemResponse> resultPage = new PageImpl<>(
                responseDtos,
                instancePage.getPageable(),
                instancePage.getTotalElements());

        ctx.setResult(resultPage);
    }

    /**
     * 轉換單一 Domain 物件為 Response DTO
     */
    private WorkflowInstanceListItemResponse toResponse(WorkflowInstance instance) {
        WorkflowInstanceListItemResponse response = new WorkflowInstanceListItemResponse();

        response.setInstanceId(instance.getInstanceId());
        response.setFlowType(instance.getFlowType() != null ? instance.getFlowType().name() : null);
        // flowName 不存在於 WorkflowInstance，設為空或從 flowType 推導
        response.setFlowName(instance.getFlowType() != null ? instance.getFlowType().name() : null);
        response.setBusinessType(instance.getBusinessType());
        response.setBusinessId(instance.getBusinessId());
        response.setApplicantId(instance.getApplicantId());
        response.setApplicantName(instance.getApplicantName());
        response.setDepartmentName(instance.getDepartmentName());
        response.setSummary(instance.getSummary());
        response.setStatus(instance.getStatus() != null ? instance.getStatus().name() : null);
        response.setCurrentNodeName(instance.getCurrentNodeName());
        response.setStartedAt(instance.getStartedAt());
        response.setCompletedAt(instance.getCompletedAt());

        // 計算執行時長
        if (instance.getStartedAt() != null) {
            if (instance.getCompletedAt() != null) {
                long days = Duration.between(instance.getStartedAt(), instance.getCompletedAt()).toDays();
                response.setDuration(days + " 天");
            } else {
                long days = Duration.between(instance.getStartedAt(), LocalDateTime.now()).toDays();
                response.setDuration("進行中 " + days + " 天");
            }
        }

        return response;
    }
}
