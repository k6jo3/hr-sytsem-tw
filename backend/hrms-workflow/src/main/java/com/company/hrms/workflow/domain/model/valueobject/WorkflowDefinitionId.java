package com.company.hrms.workflow.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 流程定義 ID
 */
public class WorkflowDefinitionId extends Identifier<String> {

    public WorkflowDefinitionId(String value) {
        super(value);
    }

    public static WorkflowDefinitionId create() {
        return new WorkflowDefinitionId(generateUUID());
    }

    public static WorkflowDefinitionId from(String id) {
        return new WorkflowDefinitionId(id);
    }
}
