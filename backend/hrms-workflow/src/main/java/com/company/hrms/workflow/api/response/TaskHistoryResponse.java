package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskHistoryResponse {
    private String taskId;
    private String nodeName;
    private String assigneeName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String comments;
}
