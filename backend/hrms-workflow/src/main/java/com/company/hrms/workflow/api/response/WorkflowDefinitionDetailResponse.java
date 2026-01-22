package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;
import java.util.List;

import com.company.hrms.workflow.domain.model.valueobject.WorkflowEdge;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowNode;

import lombok.Data;

/**
 * 流程定義詳情回應
 */
@Data
public class WorkflowDefinitionDetailResponse {

    /**
     * 流程定義ID
     */
    private String definitionId;

    /**
     * 流程名稱
     */
    private String flowName;

    /**
     * 流程類型
     */
    private String flowType;

    /**
     * 流程說明
     */
    private String description;

    /**
     * 流程狀態 (DRAFT/ACTIVE/INACTIVE)
     */
    private String status;

    /**
     * 版本號
     */
    private Integer version;

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

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 建立人
     */
    private String createdBy;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 發布時間
     */
    private LocalDateTime publishedAt;
}
