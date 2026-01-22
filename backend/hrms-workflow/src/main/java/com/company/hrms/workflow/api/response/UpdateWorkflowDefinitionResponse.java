package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 更新流程定義回應
 */
@Data
public class UpdateWorkflowDefinitionResponse {

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
     * 版本號
     */
    private Integer version;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;
}
