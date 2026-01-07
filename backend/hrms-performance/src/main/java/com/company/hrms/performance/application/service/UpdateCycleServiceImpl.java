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

import lombok.RequiredArgsConstructor;

/**
 * 更新考核週期 Service (Business Pipeline 架構)
 */
@Service("updateCycleServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UpdateCycleServiceImpl implements CommandApiService<UpdateCycleRequest, SuccessResponse> {

    private final LoadCycleTask loadCycleTask;
    private final SaveCycleTask saveCycleTask;
    private final PublishCycleEventsTask publishEventsTask;

    @Override
    public SuccessResponse execCommand(UpdateCycleRequest req, JWTModel currentUser, String... args)
            throws Exception {

        StartCycleContext ctx = new StartCycleContext(req.getCycleId());

        BusinessPipeline.start(ctx)
                .next(loadCycleTask)
                .next(context -> {
                    // 更新週期名稱 (使用 Domain 方法)
                    if (req.getCycleName() != null) {
                        context.getCycle().updateCycleName(req.getCycleName());
                    }
                    // 更新考核期間 (使用 Domain 方法)
                    if (req.getStartDate() != null && req.getEndDate() != null) {
                        context.getCycle().updatePeriod(req.getStartDate(), req.getEndDate());
                    }
                })
                .next(saveCycleTask)
                .next(publishEventsTask)
                .execute();

        return SuccessResponse.of("考核週期已更新");
    }
}
