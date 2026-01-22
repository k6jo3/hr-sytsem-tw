package com.company.hrms.workflow.domain.repository;

import java.util.Optional;

import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;

public interface IWorkflowDefinitionRepository {
        WorkflowDefinition save(WorkflowDefinition domain);

        Optional<WorkflowDefinition> findById(WorkflowDefinitionId id);

        Optional<WorkflowDefinition> findLatestActive(FlowType flowType);

        boolean existsByFlowType(FlowType flowType);
}
