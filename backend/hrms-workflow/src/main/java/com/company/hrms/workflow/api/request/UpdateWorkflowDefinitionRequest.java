package com.company.hrms.workflow.api.request;

import java.util.List;

import com.company.hrms.workflow.domain.model.valueobject.WorkflowEdge;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowNode;

import lombok.Data;

/**
 * 更新流程定義請求
 * PUT /api/v1/workflows/definitions/{id}
 */
@Data
public class UpdateWorkflowDefinitionRequest {

    /**
     * 流程定義ID (由 PathVariable 注入)
     */
    private String definitionId;

    /**
     * 流程名稱
     */
    private String flowName;

    /**
     * 流程說明
     */
    private String description;

    /**
     * 節點定義
     */
    private List<WorkflowNode> nodes;

    /**
     * 邊線定義
     */
    private List<WorkflowEdge> edges;

    /**
     * 預設處理天數
     */
    private Integer defaultDueDays;
}
