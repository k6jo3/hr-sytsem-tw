package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 取消流程實例回應
 */
@Data
public class CancelWorkflowInstanceResponse {

    /**
     * 流程實例ID
     */
    private String instanceId;

    /**
     * 狀態
     */
    private String status;

    /**
     * 取消時間
     */
    private LocalDateTime cancelledAt;
}
