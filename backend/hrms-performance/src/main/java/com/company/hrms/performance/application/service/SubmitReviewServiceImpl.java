package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.SubmitReviewRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.application.service.context.SubmitReviewContext;
import com.company.hrms.performance.application.service.task.LoadReviewTask;
import com.company.hrms.performance.application.service.task.PublishReviewEventsTask;
import com.company.hrms.performance.application.service.task.SaveReviewTask;
import com.company.hrms.performance.application.service.task.SubmitEvaluationTask;

import lombok.RequiredArgsConstructor;

/**
 * 提交考核 Service (Business Pipeline 架構)
 */
@Service("submitReviewServiceImpl")
@RequiredArgsConstructor
@Transactional
public class SubmitReviewServiceImpl implements CommandApiService<SubmitReviewRequest, SuccessResponse> {

        // 注入 Pipeline Tasks
        private final LoadReviewTask loadReviewTask;
        private final SubmitEvaluationTask submitEvaluationTask;
        private final SaveReviewTask saveReviewTask;
        private final PublishReviewEventsTask publishEventsTask;

        @Override
        public SuccessResponse execCommand(SubmitReviewRequest req, JWTModel currentUser, String... args)
                        throws Exception {

                // 1. 建立 Context
                SubmitReviewContext ctx = new SubmitReviewContext(
                                req.getReviewId(),
                                req.getEvaluationItems(),
                                req.getComments());

                // 2. 執行 Pipeline
                BusinessPipeline.start(ctx)
                                .next(loadReviewTask) // 載入考核記錄
                                .next(submitEvaluationTask) // 提交評估 (Domain)
                                .next(saveReviewTask) // 儲存記錄
                                .next(publishEventsTask) // 發布事件
                                .execute();

                // 3. 回傳結果
                return SuccessResponse.of("考核已提交");
        }
}
