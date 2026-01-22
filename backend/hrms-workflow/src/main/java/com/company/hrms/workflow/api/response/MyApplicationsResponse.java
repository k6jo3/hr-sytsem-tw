package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "我的申請回應")
public class MyApplicationsResponse {
    private String instanceId;
    private String businessType;
    private String businessId;
    private String businessUrl;
    private String currentNodeName;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String summary;
}
