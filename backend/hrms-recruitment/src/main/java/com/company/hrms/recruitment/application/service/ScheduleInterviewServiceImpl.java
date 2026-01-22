package com.company.hrms.recruitment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.recruitment.application.context.ScheduleInterviewContext;
import com.company.hrms.recruitment.application.dto.interview.InterviewResponse;
import com.company.hrms.recruitment.application.dto.interview.ScheduleInterviewRequest;
import com.company.hrms.recruitment.application.task.AssembleInterviewResponseTask;
import com.company.hrms.recruitment.application.task.CreateInterviewTask;
import com.company.hrms.recruitment.application.task.ValidateScheduleInterviewTask;

import lombok.RequiredArgsConstructor;

/**
 * 安排面試 Service
 */
@Service("scheduleInterviewServiceImpl")
@Transactional
@RequiredArgsConstructor
public class ScheduleInterviewServiceImpl
        implements CommandApiService<ScheduleInterviewRequest, InterviewResponse> {

    private final ValidateScheduleInterviewTask validateTask;
    private final CreateInterviewTask createTask;
    private final AssembleInterviewResponseTask assembleTask;

    @Override
    public InterviewResponse execCommand(
            ScheduleInterviewRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        ScheduleInterviewContext ctx = ScheduleInterviewContext.of(request, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(validateTask)
                .next(createTask)
                .next(assembleTask)
                .execute();

        return ctx.getResponse();
    }
}
