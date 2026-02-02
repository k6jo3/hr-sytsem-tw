package com.company.hrms.workflow.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.CreateWorkflowDefinitionRequest;
import com.company.hrms.workflow.api.response.CreateWorkflowDefinitionResponse;
import com.company.hrms.workflow.application.service.context.CreateWorkflowDefinitionContext;
import com.company.hrms.workflow.application.service.task.definition.ConstructCreateDefinitionResponseTask;
import com.company.hrms.workflow.application.service.task.definition.InitWorkflowDefinitionTask;
import com.company.hrms.workflow.application.service.task.definition.SaveNewWorkflowDefinitionTask;

import lombok.RequiredArgsConstructor;

/**
 * 建立流程定義服務
 */
@Service("createWorkflowDefinitionServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CreateWorkflowDefinitionServiceImpl
        implements CommandApiService<CreateWorkflowDefinitionRequest, CreateWorkflowDefinitionResponse> {

    private final InitWorkflowDefinitionTask initTask;
    private final SaveNewWorkflowDefinitionTask saveTask;
    private final ConstructCreateDefinitionResponseTask responseTask;

    @Override
    public CreateWorkflowDefinitionResponse execCommand(CreateWorkflowDefinitionRequest req, JWTModel currentUser,
            String... args) throws Exception {

        CreateWorkflowDefinitionContext context = new CreateWorkflowDefinitionContext(req, currentUser);

        BusinessPipeline.start(context)
                .next(initTask)
                .next(saveTask)
                .next(responseTask)
                .execute();

        return context.getResponse();
    }
}
