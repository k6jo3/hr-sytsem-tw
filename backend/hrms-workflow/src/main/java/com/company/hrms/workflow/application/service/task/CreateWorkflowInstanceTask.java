package com.company.hrms.workflow.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.StartWorkflowContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;

/**
 * Task: 建立流程實例 (Factory)
 */
@Component
public class CreateWorkflowInstanceTask implements PipelineTask<StartWorkflowContext> {

    @Override
    public void execute(StartWorkflowContext context) {
        var definition = context.getDefinition();
        var request = context.getRequest();

        // 使用 Domain Factory 方法建立 Instance
        // 這裡假設 WorkflowInstance 有一個靜態方法 create 或建構子支援
        // 注意：需確認 WorkflowInstance 的 domain 邏輯。
        // 目前先假設可透過 create 方法。

        WorkflowInstance instance = WorkflowInstance.create(
                definition.getId(),
                definition.getFlowType(),
                request.getApplicantId(),
                request.getBusinessId(),
                request.getBusinessType(),
                request.getVariables());

        if (request.getSummary() != null) {
            instance.updateSummary(request.getSummary());
        }

        // 啟動流程 (可能觸發事件、設定初始狀態)
        instance.start();

        context.setInstance(instance);
    }
}
