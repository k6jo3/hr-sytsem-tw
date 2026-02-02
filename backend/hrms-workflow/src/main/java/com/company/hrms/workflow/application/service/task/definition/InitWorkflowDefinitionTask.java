package com.company.hrms.workflow.application.service.task.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.workflow.api.request.CreateWorkflowDefinitionRequest;
import com.company.hrms.workflow.application.service.context.CreateWorkflowDefinitionContext;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;
import com.company.hrms.workflow.domain.model.enums.DefinitionStatus;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowEdge;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitWorkflowDefinitionTask implements PipelineTask<CreateWorkflowDefinitionContext> {

    private final ObjectMapper objectMapper;

    @Override
    public void execute(CreateWorkflowDefinitionContext context) throws Exception {
        CreateWorkflowDefinitionRequest req = context.getRequest();

        List<WorkflowNode> nodes = new ArrayList<>();
        List<WorkflowEdge> edges = new ArrayList<>();

        try {
            if (req.getNodes() != null && !req.getNodes().isBlank()) {
                nodes = objectMapper.readValue(req.getNodes(), new TypeReference<List<WorkflowNode>>() {
                });
            }
            if (req.getEdges() != null && !req.getEdges().isBlank()) {
                edges = objectMapper.readValue(req.getEdges(), new TypeReference<List<WorkflowEdge>>() {
                });
            }
        } catch (Exception e) {
            log.error("Error parsing workflow definition JSON", e);
            throw new IllegalArgumentException("流程定義 JSON 格式不正確");
        }

        WorkflowDefinition definition = WorkflowDefinition.builder()
                .id(new WorkflowDefinitionId(UUID.randomUUID().toString()))
                .flowName(req.getFlowName())
                .flowType(req.getFlowType() != null ? FlowType.valueOf(req.getFlowType()) : null)
                .nodes(nodes)
                .edges(edges)
                .status(DefinitionStatus.DRAFT)
                .version(1)
                .createdBy(context.getCurrentUser().getUserId())
                .updatedBy(context.getCurrentUser().getUserId())
                .build();

        context.setDefinition(definition);
    }
}
