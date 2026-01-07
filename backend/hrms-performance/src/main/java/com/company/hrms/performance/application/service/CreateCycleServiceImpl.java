package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.CreateCycleRequest;
import com.company.hrms.performance.api.response.CreateCycleResponse;
import com.company.hrms.performance.application.service.context.CreateCycleContext;
import com.company.hrms.performance.application.service.task.CreateCycleTask;
import com.company.hrms.performance.application.service.task.PublishCycleEventsForCreateTask;
import com.company.hrms.performance.application.service.task.SaveCycleForCreateTask;

import lombok.RequiredArgsConstructor;

/**
 * 建立考核週期 Service (Business Pipeline 架構)
 */
@Service("createCycleServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CreateCycleServiceImpl implements CommandApiService<CreateCycleRequest, CreateCycleResponse> {

    private final CreateCycleTask createCycleTask;
    private final SaveCycleForCreateTask saveCycleTask;
    private final PublishCycleEventsForCreateTask publishEventsTask;

    @Override
    public CreateCycleResponse execCommand(CreateCycleRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // 1. 建立 Context
        CreateCycleContext ctx = new CreateCycleContext(
                req.getCycleName(),
                req.getCycleType(),
                req.getStartDate(),
                req.getEndDate(),
                req.getSelfEvalDeadline(),
                req.getManagerEvalDeadline());

        // 2. 執行 Pipeline
        BusinessPipeline.start(ctx)
                .next(createCycleTask) // 建立週期 (Domain)
                .next(saveCycleTask) // 儲存週期
                .next(publishEventsTask) // 發布事件
                .execute();

        // 3. 回傳結果
        return new CreateCycleResponse(ctx.getCycleId());
    }
}
