package com.company.hrms.workflow.application.service.context;

import org.springframework.data.domain.Page;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.workflow.api.response.WorkflowInstanceListItemResponse;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查詢流程實例列表 Context
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetWorkflowInstanceListContext extends PipelineContext {

    // === 輸入 ===
    private String flowType;
    private String status;
    private String applicantId;
    private String startDateFrom;
    private String startDateTo;
    private Integer page;
    private Integer pageSize;

    // === 中間數據 ===
    private Page<WorkflowInstance> instancePage;

    // === 輸出 ===
    private Page<WorkflowInstanceListItemResponse> result;
}
