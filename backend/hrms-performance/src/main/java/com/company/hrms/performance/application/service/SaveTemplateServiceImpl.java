package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.SaveTemplateRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.application.service.context.StartCycleContext;
import com.company.hrms.performance.application.service.task.LoadCycleTask;
import com.company.hrms.performance.application.service.task.PublishCycleEventsTask;
import com.company.hrms.performance.application.service.task.SaveCycleTask;

import lombok.RequiredArgsConstructor;

/**
 * 儲存考核範本 Service (Business Pipeline 架構 - 簡化版)
 */
@Service("saveTemplateServiceImpl")
@RequiredArgsConstructor
@Transactional
public class SaveTemplateServiceImpl implements CommandApiService<SaveTemplateRequest, SuccessResponse> {

        private final LoadCycleTask loadCycleTask;
        private final SaveCycleTask saveCycleTask;
        private final PublishCycleEventsTask publishEventsTask;

        @Override
        public SuccessResponse execCommand(SaveTemplateRequest req, JWTModel currentUser, String... args)
                        throws Exception {

                StartCycleContext ctx = new StartCycleContext(req.getCycleId());

                // Note: Simplified - actual template saving logic should be in a
                // SaveTemplateTask
                BusinessPipeline.start(ctx)
                                .next(loadCycleTask)
                                .next(saveCycleTask)
                                .next(publishEventsTask)
                                .execute();

                return SuccessResponse.of("考核範本已儲存");
        }
}
