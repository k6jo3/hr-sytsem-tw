package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowInstanceDetailResponse {
    private String instanceId;
    private String definitionId;
    private String businessType;
    private String businessId;
    private String applicantId;
    private String applicantName;
    private String currentNodeName;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    // Simple timeline/tasks view
    private List<TaskHistoryResponse> timeline;
}
