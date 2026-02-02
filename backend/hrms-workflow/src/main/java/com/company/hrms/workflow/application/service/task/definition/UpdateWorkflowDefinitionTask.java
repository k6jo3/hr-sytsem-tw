package com.company.hrms.workflow.application.service.task.definition;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.api.request.UpdateWorkflowDefinitionRequest;
import com.company.hrms.workflow.application.service.context.UpdateWorkflowDefinitionContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;
import com.company.hrms.workflow.domain.model.enums.DefinitionStatus;

@Component
public class UpdateWorkflowDefinitionTask implements PipelineTask<UpdateWorkflowDefinitionContext> {

    @Override
    public void execute(UpdateWorkflowDefinitionContext context) throws Exception {
        WorkflowDefinition definition = context.getDefinition();
        UpdateWorkflowDefinitionRequest request = context.getRequest();

        // 驗證狀態：只能更新 DRAFT 或 INACTIVE
        if (definition.getStatus() == DefinitionStatus.ACTIVE) {
            throw new IllegalStateException("無法修改已發布的流程定義，請先停用");
        }

        // 更新屬性
        if (request.getFlowName() != null) {
            definition.setFlowName(request.getFlowName());
        }
        if (request.getDescription() != null) {
            definition.setDescription(request.getDescription());
        }
        if (request.getNodes() != null) {
            definition.setNodes(request.getNodes());
        }
        if (request.getEdges() != null) {
            definition.setEdges(request.getEdges());
        }
        if (request.getDefaultDueDays() != null) {
            definition.setDefaultDueDays(request.getDefaultDueDays());
        }

        // 設定更新資訊
        definition.setUpdatedBy(context.getCurrentUser().getUserId());
    }
}
