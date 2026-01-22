package com.company.hrms.workflow.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.StartWorkflowRequest;
import com.company.hrms.workflow.api.response.StartWorkflowResponse;
import com.company.hrms.workflow.application.service.context.StartWorkflowContext;
import com.company.hrms.workflow.application.service.task.CreateWorkflowInstanceTask;
import com.company.hrms.workflow.application.service.task.LoadWorkflowDefinitionTask;
import com.company.hrms.workflow.application.service.task.SaveWorkflowInstanceTask;

import lombok.RequiredArgsConstructor;

/**
 * Service: 發起流程
 */
@Service("startWorkflowServiceImpl")
@Transactional
@RequiredArgsConstructor
public class StartWorkflowServiceImpl implements CommandApiService<StartWorkflowRequest, StartWorkflowResponse> {

    private final LoadWorkflowDefinitionTask loadWorkflowDefinitionTask;
    private final CreateWorkflowInstanceTask createWorkflowInstanceTask;
    private final SaveWorkflowInstanceTask saveWorkflowInstanceTask;

    @Override
    public StartWorkflowResponse execCommand(StartWorkflowRequest request,
            JWTModel currentUser, String... args) throws Exception {

        StartWorkflowContext context = new StartWorkflowContext(request);

        BusinessPipeline.start(context)
                .next(loadWorkflowDefinitionTask)
                .next(createWorkflowInstanceTask)
                .next(saveWorkflowInstanceTask)
                .execute();

        return context.getResponse();
    }
}
