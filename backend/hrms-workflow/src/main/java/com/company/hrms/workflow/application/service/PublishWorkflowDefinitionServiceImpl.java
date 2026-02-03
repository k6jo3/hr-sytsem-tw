package com.company.hrms.workflow.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.PublishWorkflowDefinitionRequest;
import com.company.hrms.workflow.application.service.context.PublishWorkflowDefinitionContext;
import com.company.hrms.workflow.application.service.task.definition.LoadWorkflowDefinitionForPublishTask;
import com.company.hrms.workflow.application.service.task.definition.PublishWorkflowDefinitionTask;
import com.company.hrms.workflow.application.service.task.definition.SaveWorkflowDefinitionForPublishTask;

import lombok.RequiredArgsConstructor;

/**
 * 發佈流程定義服務
 */
@Service("publishWorkflowDefinitionServiceImpl")
@Transactional
@RequiredArgsConstructor
public class PublishWorkflowDefinitionServiceImpl implements CommandApiService<PublishWorkflowDefinitionRequest, Void> {

    private final LoadWorkflowDefinitionForPublishTask loadTask;
    private final PublishWorkflowDefinitionTask publishTask;
    private final SaveWorkflowDefinitionForPublishTask saveTask;

    @Override
    public Void execCommand(PublishWorkflowDefinitionRequest req, JWTModel currentUser, String... args)
            throws Exception {

        String id = (args.length > 0) ? args[0] : req.getDefinitionId();

        PublishWorkflowDefinitionContext context = new PublishWorkflowDefinitionContext(req, id, currentUser);

        BusinessPipeline.start(context)
                .next(loadTask)
                .next(publishTask)
                .next(saveTask)
                .execute();

        return null;
    }
}
