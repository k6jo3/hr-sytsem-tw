package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.FinalizeReviewRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.application.service.context.FinalizeReviewContext;
import com.company.hrms.performance.application.service.task.FinalizeReviewTask;
import com.company.hrms.performance.application.service.task.LoadReviewForFinalizeTask;
import com.company.hrms.performance.application.service.task.PublishReviewEventsForFinalizeTask;
import com.company.hrms.performance.application.service.task.SaveReviewForFinalizeTask;

import lombok.RequiredArgsConstructor;

/**
 * 確認最終評等 Service (Business Pipeline 架構)
 */
@Service("finalizeReviewServiceImpl")
@RequiredArgsConstructor
@Transactional
public class FinalizeReviewServiceImpl implements CommandApiService<FinalizeReviewRequest, SuccessResponse> {

    // 注入 Pipeline Tasks
    private final LoadReviewForFinalizeTask loadReviewTask;
    private final FinalizeReviewTask finalizeReviewTask;
    private final SaveReviewForFinalizeTask saveReviewTask;
    private final PublishReviewEventsForFinalizeTask publishEventsTask;

    @Override
    public SuccessResponse execCommand(FinalizeReviewRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // 1. 建立 Context
        FinalizeReviewContext ctx = new FinalizeReviewContext(
                req.getReviewId(),
                req.getFinalScore(),
                req.getFinalRating(),
                req.getAdjustmentReason());

        // 2. 執行 Pipeline
        BusinessPipeline.start(ctx)
                .next(loadReviewTask) // 載入考核記錄
                .next(finalizeReviewTask) // 確認評等 (Domain)
                .next(saveReviewTask) // 儲存記錄
                .next(publishEventsTask) // 發布事件
                .execute();

        // 3. 回傳結果
        return SuccessResponse.of("考核評等已確認");
    }
}
