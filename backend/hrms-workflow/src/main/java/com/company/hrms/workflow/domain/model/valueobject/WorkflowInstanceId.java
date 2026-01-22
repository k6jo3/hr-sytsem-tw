package com.company.hrms.workflow.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 流程實例 ID
 */
public class WorkflowInstanceId extends Identifier<String> {

    public WorkflowInstanceId(String value) {
        super(value);
    }

    public static WorkflowInstanceId create() {
        return new WorkflowInstanceId(generateUUID());
    }

    public static WorkflowInstanceId from(String id) {
        return new WorkflowInstanceId(id);
    }
}
