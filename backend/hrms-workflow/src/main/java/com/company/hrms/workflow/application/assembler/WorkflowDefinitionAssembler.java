package com.company.hrms.workflow.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.workflow.api.response.WorkflowDefinitionResponse;
import com.company.hrms.workflow.infrastructure.entity.WorkflowDefinitionEntity;

@Component
public class WorkflowDefinitionAssembler {

    public static WorkflowDefinitionResponse toResponse(WorkflowDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }
        return WorkflowDefinitionResponse.builder()
                .definitionId(entity.getDefinitionId())
                .flowName(entity.getFlowName())
                .flowType(entity.getFlowType())
                .isActive(entity.isActive())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .nodes(entity.getNodesJson())
                .edges(entity.getEdgesJson())
                .build();
    }
}
