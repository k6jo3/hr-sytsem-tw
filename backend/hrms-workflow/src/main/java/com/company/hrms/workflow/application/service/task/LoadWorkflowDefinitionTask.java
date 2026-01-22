package com.company.hrms.workflow.application.service.task;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.application.service.context.StartWorkflowContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;
import com.company.hrms.workflow.domain.repository.IWorkflowDefinitionRepository;

import lombok.RequiredArgsConstructor;

/**
 * Task: 載入流程定義
 */
@Component
@RequiredArgsConstructor
public class LoadWorkflowDefinitionTask implements PipelineTask<StartWorkflowContext> {

    private final IWorkflowDefinitionRepository definitionRepository;

    @Override
    public void execute(StartWorkflowContext context) {
        var request = context.getRequest();
        Optional<WorkflowDefinition> defOpt = Optional.empty();

        if (request.getDefinitionId() != null) {
            // 指定 ID 查找
            defOpt = definitionRepository.findById(new WorkflowDefinitionId(request.getDefinitionId()));
        } else if (request.getFlowType() != null) {
            // 依 FlowType 查找最新 Active 版本
            defOpt = definitionRepository.findLatestActive(request.getFlowType());
        }

        if (defOpt.isEmpty()) {
            throw new IllegalArgumentException("找不到符合的流程定義 (FlowType: "
                    + request.getFlowType() + ", ID: " + request.getDefinitionId() + ")");
        }

        context.setDefinition(defOpt.get());
    }
}
