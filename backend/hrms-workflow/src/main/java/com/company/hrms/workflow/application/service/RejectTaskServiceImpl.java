package com.company.hrms.workflow.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.workflow.api.request.RejectTaskRequest;
import com.company.hrms.workflow.api.response.RejectTaskResponse;
import com.company.hrms.workflow.application.service.context.RejectTaskContext;
import com.company.hrms.workflow.application.service.task.ExecuteRejectionLogicTask;
import com.company.hrms.workflow.application.service.task.LoadRejectInstanceTask;
import com.company.hrms.workflow.application.service.task.SaveRejectTask;
import com.company.hrms.workflow.application.service.task.ValidateRejectTaskOwnershipTask;

import lombok.RequiredArgsConstructor;

/**
 * Service: 駁回任務
 */
@Service("rejectTaskServiceImpl")
@Transactional
@RequiredArgsConstructor
public class RejectTaskServiceImpl implements CommandApiService<RejectTaskRequest, RejectTaskResponse> {

    private final LoadRejectInstanceTask loadTask;
    private final ValidateRejectTaskOwnershipTask validateTask;
    private final ExecuteRejectionLogicTask executeTask;
    private final SaveRejectTask saveTask;

    @Override
    public RejectTaskResponse execCommand(RejectTaskRequest request, JWTModel currentUser, String... args)
            throws Exception {

        if (currentUser != null) {
            request.setApproverId(currentUser.getEmployeeNumber());
        }

        RejectTaskContext context = new RejectTaskContext(request);

        BusinessPipeline.start(context)
                .next(loadTask)
                .next(validateTask)
                .next(executeTask)
                .next(saveTask)
                .execute();

        return context.getResponse();
    }
}
