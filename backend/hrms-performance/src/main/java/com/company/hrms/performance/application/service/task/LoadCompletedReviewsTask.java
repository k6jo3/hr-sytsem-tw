package com.company.hrms.performance.application.service.task;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.performance.application.service.context.GetDistributionContext;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
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
        // 查詢特定週期下所有已完成的考核
        QueryGroup query = QueryBuilder.where()
                .and("cycleId", Operator.EQ, context.getCycleId())
                .and("status", Operator.EQ, "COMPLETED") // ReviewStatus.FINALIZED? usually mapped to COMPLETED in
                                                         // entity or similar
                .build();

        // 注意：這裡假設 reviewRepository 支援 QueryGroup
        List<PerformanceReview> reviews = reviewRepository.findAll(query, Pageable.unpaged()).getContent();

        context.setReviews(reviews);
    }

    @Override
    public String getName() {
        return "載入已完成考核記錄";
    }
}
