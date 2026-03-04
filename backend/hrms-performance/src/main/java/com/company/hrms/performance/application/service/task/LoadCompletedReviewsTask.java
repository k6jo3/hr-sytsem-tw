package com.company.hrms.performance.application.service.task;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.performance.application.service.context.GetDistributionContext;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.ReviewStatus;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入已完成的考核記錄 Task
 */
@Component
@RequiredArgsConstructor
public class LoadCompletedReviewsTask implements PipelineTask<GetDistributionContext> {

    private final IPerformanceReviewRepository reviewRepository;

    @Override
    public void execute(GetDistributionContext context) throws Exception {
        // 查詢特定週期下所有已完成的考核（Entity 欄位為 UUID/Enum，需傳入正確型態）
        QueryGroup query = QueryBuilder.where()
                .and("cycleId", Operator.EQ, UUID.fromString(context.getCycleId()))
                .and("status", Operator.EQ, ReviewStatus.FINALIZED)
                .build();

        // 注意：這裡假設 reviewRepository 支援 QueryGroup
        List<PerformanceReview> reviews = reviewRepository.findAll(query, PageRequest.of(0, 10000)).getContent();

        context.setReviews(reviews);
    }

    @Override
    public String getName() {
        return "載入已完成考核記錄";
    }
}
