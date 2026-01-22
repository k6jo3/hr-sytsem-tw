package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 停用流程定義回應
 */
@Data
public class DeactivateWorkflowDefinitionResponse {

    /**
     * 流程定義ID
     */
    private String definitionId;

    /**
     * 狀態
     */
    private String status;

    /**
     * 停用時間
     */
    private LocalDateTime deactivatedAt;
}
