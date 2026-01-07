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
import com.company.hrms.performance.application.service.task.StartCycleTask;

import lombok.RequiredArgsConstructor;

/**
 * 啟動考核週期 Service (Business Pipeline 架構)
 */
@Service("startCycleServiceImpl")
@RequiredArgsConstructor
@Transactional
public class StartCycleServiceImpl implements CommandApiService<StartCycleRequest, SuccessResponse> {

        // 注入 Pipeline Tasks
        private final LoadCycleTask loadCycleTask;
        private final StartCycleTask startCycleTask;
        private final SaveCycleTask saveCycleTask;
        private final PublishCycleEventsTask publishEventsTask;

        @Override
        public SuccessResponse execCommand(StartCycleRequest req, JWTModel currentUser, String... args)
                        throws Exception {

                // 1. 建立 Context
                StartCycleContext ctx = new StartCycleContext(req.getCycleId());

                // 2. 執行 Pipeline
                BusinessPipeline.start(ctx)
                                .next(loadCycleTask) // 載入考核週期
                                .next(startCycleTask) // 啟動週期 (Domain)
                                .next(saveCycleTask) // 儲存週期
                                .next(publishEventsTask) // 發布事件
                                .execute();

                // 3. 回傳結果
                return SuccessResponse.of("考核週期已啟動");
        }
}
