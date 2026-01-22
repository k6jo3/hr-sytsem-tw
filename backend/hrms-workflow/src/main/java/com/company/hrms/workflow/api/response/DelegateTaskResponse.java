package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 任務轉交回應
 */
@Data
public class DelegateTaskResponse {

    private String taskId;
    private String delegateToId;
    private String delegateToName;
    private LocalDateTime delegatedAt;
}
