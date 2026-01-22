package com.company.hrms.workflow.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.CreateWorkflowDefinitionRequest;
import com.company.hrms.workflow.api.response.CreateWorkflowDefinitionResponse;
import com.company.hrms.workflow.infrastructure.entity.WorkflowDefinitionEntity;
import com.company.hrms.workflow.infrastructure.repository.IWorkflowDefinitionJpaRepository;

import lombok.RequiredArgsConstructor;

@Service("createWorkflowDefinitionServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CreateWorkflowDefinitionServiceImpl
        implements CommandApiService<CreateWorkflowDefinitionRequest, CreateWorkflowDefinitionResponse> {

    private final IWorkflowDefinitionJpaRepository formRepository;

    @Override
    public CreateWorkflowDefinitionResponse execCommand(CreateWorkflowDefinitionRequest req, JWTModel currentUser,
            String... args) throws Exception {

        WorkflowDefinitionEntity entity = new WorkflowDefinitionEntity();
        entity.setDefinitionId(UUID.randomUUID().toString());
        entity.setFlowName(req.getFlowName());
        entity.setFlowType(req.getFlowType());
        entity.setNodesJson(req.getNodes());
        entity.setEdgesJson(req.getEdges());
        entity.setActive(false); // Default inactive until published
        entity.setVersion(1);
        entity.setCreatedAt(LocalDateTime.now());

        formRepository.save(entity);

        return new CreateWorkflowDefinitionResponse(entity.getDefinitionId());
    }
}
