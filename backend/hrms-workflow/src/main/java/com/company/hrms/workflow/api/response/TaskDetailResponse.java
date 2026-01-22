package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

/**
 * 任務詳情回應
 */
@Data
public class TaskDetailResponse {

    private String taskId;
    private String instanceId;
    private String nodeName;
    private String nodeType;

    // 業務資訊
    private String businessType;
    private String businessId;
    private String businessUrl;
    private String summary;

    // 申請人資訊
    private String applicantId;
    private String applicantName;
    private String departmentName;

    // 審核人資訊
    private String approverId;
    private String approverName;
    private String delegatedToId;
    private String delegatedToName;

    // 狀態與時間
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private Boolean isOverdue;
    private Integer overdueHours;

    // 審核結果
    private LocalDateTime approvedAt;
    private String comment;

    // 流程變數
    private Map<String, Object> variables;
}
