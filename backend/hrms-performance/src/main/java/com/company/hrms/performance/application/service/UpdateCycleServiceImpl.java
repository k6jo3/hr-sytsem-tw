package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.UpdateCycleRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.application.service.context.StartCycleContext;
import com.company.hrms.performance.application.service.task.LoadCycleTask;
import com.company.hrms.performance.application.service.task.PublishCycleEventsTask;
import com.company.hrms.performance.application.service.task.SaveCycleTask;
import com.company.hrms.performance.application.service.task.UpdateCycleTask;

import lombok.RequiredArgsConstructor;

/**
 * 更新考核週期 Service (Business Pipeline 架構)
 */
@Service("updateCycleServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UpdateCycleServiceImpl implements CommandApiService<UpdateCycleRequest, SuccessResponse> {

    private final LoadCycleTask loadCycleTask;
    private final UpdateCycleTask updateCycleTask;
    private final SaveCycleTask saveCycleTask;
    private final PublishCycleEventsTask publishEventsTask;

    @Override
    public SuccessResponse execCommand(UpdateCycleRequest req, JWTModel currentUser, String... args)
            throws Exception {

        StartCycleContext ctx = new StartCycleContext(req.getCycleId());
        ctx.setCycleName(req.getCycleName());
        ctx.setCycleType(req.getCycleType());
        ctx.setStartDate(req.getStartDate());
        ctx.setEndDate(req.getEndDate());
        ctx.setSelfEvalDeadline(req.getSelfEvalDeadline());
        ctx.setManagerEvalDeadline(req.getManagerEvalDeadline());

        BusinessPipeline.start(ctx)
                .next(loadCycleTask)
                .next(updateCycleTask)
                .next(saveCycleTask)
                .next(publishEventsTask)
                .execute();

        return SuccessResponse.of("考核週期已更新");
    }
}
