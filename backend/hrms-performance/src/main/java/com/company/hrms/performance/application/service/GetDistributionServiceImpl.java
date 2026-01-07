```java
package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.GetDistributionResponse;
import com.company.hrms.performance.application.service.context.GetDistributionContext;
import com.company.hrms.performance.application.service.task.CalculateDistributionTask;
import com.company.hrms.performance.application.service.task.LoadCompletedReviewsTask;

import lombok.RequiredArgsConstructor;

/**
 * 查詢績效分布 Service
 * 統計特定週期的績效評等分布
 */
@Service("getDistributionServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDistributionServiceImpl implements QueryApiService<StartCycleRequest, GetDistributionResponse> {

    private final LoadCompletedReviewsTask loadReviewsTask;
    private final CalculateDistributionTask calculateDistributionTask;

    @Override
    public GetDistributionResponse getResponse(StartCycleRequest req, JWTModel currentUser, String... args)
            throws Exception {

        GetDistributionContext ctx = new GetDistributionContext(req.getCycleId());

        BusinessPipeline.start(ctx)
                .next(loadReviewsTask)
                .next(calculateDistributionTask)
                .execute();

        return ctx.getResponse();
    }
}
