package com.company.hrms.workflow.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.workflow.api.response.MyApplicationsResponse;
import com.company.hrms.workflow.infrastructure.entity.WorkflowInstanceEntity;

@Component
public class WorkflowInstanceAssembler {

    public static MyApplicationsResponse toMyApplicationsResponse(WorkflowInstanceEntity entity) {
        if (entity == null) {
            return null;
        }
        return MyApplicationsResponse.builder()
                .instanceId(entity.getInstanceId())
                .businessType(entity.getBusinessType())
                .businessId(entity.getBusinessId())
                .businessUrl(entity.getBusinessUrl())
                .currentNodeName(entity.getCurrentNodeName())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .startedAt(entity.getStartedAt())
                .completedAt(entity.getCompletedAt())
                .summary(entity.getSummary())
                .build();
    }
}
