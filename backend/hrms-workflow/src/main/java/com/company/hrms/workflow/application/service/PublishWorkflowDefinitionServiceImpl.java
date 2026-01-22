package com.company.hrms.workflow.application.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.infrastructure.entity.WorkflowDefinitionEntity;
import com.company.hrms.workflow.infrastructure.repository.IWorkflowDefinitionJpaRepository;

import lombok.RequiredArgsConstructor;

// Request DTO (inline for simplicity or use separate file if strictly required, but for ID-only usually PathVariable is enough, request body empty)
// However, interface requires a Request type.
// Let's assume we pass ID in URL but standard pattern might use DTO.
// Let's create a wrapper DTO.

@Service("publishWorkflowDefinitionServiceImpl")
@Transactional
@RequiredArgsConstructor
public class PublishWorkflowDefinitionServiceImpl implements CommandApiService<PublishWorkflowDefinitionRequest, Void> {

    private final IWorkflowDefinitionJpaRepository repository;

    @Override
    public Void execCommand(PublishWorkflowDefinitionRequest req, JWTModel currentUser, String... args)
            throws Exception {
        // Since we might get ID from PathVariable, we usually expect it in args[0] or
        // we construct Request object in Controller.
        // Let's assume controller constructs Request or uses args. Ideally Request
        // object.

        // If args[0] is present, use it as ID (Convention override)
        String id = (args.length > 0) ? args[0] : req.getDefinitionId();

        WorkflowDefinitionEntity entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Workflow Definition not found: " + id));

        entity.setActive(true);
        repository.save(entity);

        return null;
    }
}
