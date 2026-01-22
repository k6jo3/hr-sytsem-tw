package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 流程實例列表項目回應
 */
@Data
public class WorkflowInstanceListItemResponse {

    /**
     * 流程實例ID
     */
    private String instanceId;

    /**
     * 流程類型
     */
    private String flowType;

    /**
     * 流程名稱
     */
    private String flowName;

    /**
     * 業務類型
     */
    private String businessType;

    /**
     * 業務單據ID
     */
    private String businessId;

    /**
     * 申請人員工ID
     */
    private String applicantId;

    /**
     * 申請人姓名
     */
    private String applicantName;

    /**
     * 申請人部門
     */
    private String departmentName;

    /**
     * 申請摘要
     */
    private String summary;

    /**
     * 流程狀態
     */
    private String status;

    /**
     * 當前節點名稱
     */
    private String currentNodeName;

    /**
     * 開始時間
     */
    private LocalDateTime startedAt;

    /**
     * 完成時間
     */
    private LocalDateTime completedAt;

    /**
     * 執行時長（文字描述）
     */
    private String duration;
}
