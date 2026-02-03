package com.company.hrms.workflow.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.UpdateWorkflowDefinitionRequest;
import com.company.hrms.workflow.api.response.UpdateWorkflowDefinitionResponse;
import com.company.hrms.workflow.application.service.context.UpdateWorkflowDefinitionContext;
import com.company.hrms.workflow.application.service.task.definition.ConstructUpdateDefinitionResponseTask;
import com.company.hrms.workflow.application.service.task.definition.LoadWorkflowDefinitionTask;
import com.company.hrms.workflow.application.service.task.definition.SaveWorkflowDefinitionTask;
import com.company.hrms.workflow.application.service.task.definition.UpdateWorkflowDefinitionTask;

import lombok.RequiredArgsConstructor;

/**
 * 更新流程定義服務
 * PUT /api/v1/workflows/definitions/{id}
 * 
 * 使用 Business Pipeline 建立結構化業務流程
 */
@Service("updateWorkflowDefinitionServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UpdateWorkflowDefinitionServiceImpl
        implements CommandApiService<UpdateWorkflowDefinitionRequest, UpdateWorkflowDefinitionResponse> {

    private final LoadWorkflowDefinitionTask loadWorkflowDefinitionTask;
    private final UpdateWorkflowDefinitionTask updateWorkflowDefinitionTask;
    private final SaveWorkflowDefinitionTask saveWorkflowDefinitionTask;
    private final ConstructUpdateDefinitionResponseTask constructUpdateDefinitionResponseTask;

    @Override
    public UpdateWorkflowDefinitionResponse execCommand(
            UpdateWorkflowDefinitionRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        String definitionId = args.length > 0 ? args[0] : request.getDefinitionId();

        UpdateWorkflowDefinitionContext context = new UpdateWorkflowDefinitionContext(request, definitionId,
                currentUser);

        BusinessPipeline.start(context)
                .next(loadWorkflowDefinitionTask)
                .next(updateWorkflowDefinitionTask)
                .next(saveWorkflowDefinitionTask)
                .next(constructUpdateDefinitionResponseTask)
                .execute();

        return context.getResponse();
    }
}
