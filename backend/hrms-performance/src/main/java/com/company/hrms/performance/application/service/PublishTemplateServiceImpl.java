package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.application.service.context.StartCycleContext;
import com.company.hrms.performance.application.service.task.LoadCycleTask;
import com.company.hrms.performance.application.service.task.PublishCycleEventsTask;
import com.company.hrms.performance.application.service.task.SaveCycleTask;

import lombok.RequiredArgsConstructor;

/**
 * 發布考核範本 Service (Business Pipeline 架構 - 簡化版)
 */
@Service("publishTemplateServiceImpl")
@RequiredArgsConstructor
@Transactional
public class PublishTemplateServiceImpl implements CommandApiService<StartCycleRequest, SuccessResponse> {

    private final LoadCycleTask loadCycleTask;
    private final SaveCycleTask saveCycleTask;
    private final PublishCycleEventsTask publishEventsTask;

    @Override
    public SuccessResponse execCommand(StartCycleRequest req, JWTModel currentUser, String... args)
            throws Exception {

        StartCycleContext ctx = new StartCycleContext(req.getCycleId());

        // Note: Simplified - actual publish logic should be in a PublishTemplateTask
        BusinessPipeline.start(ctx)
                .next(loadCycleTask)
                .next(saveCycleTask)
                .next(publishEventsTask)
                .execute();

        return SuccessResponse.of("考核範本已發布");
    }
}
