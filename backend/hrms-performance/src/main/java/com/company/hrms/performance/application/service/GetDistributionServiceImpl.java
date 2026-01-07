package com.company.hrms.performance.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.GetDistributionResponse;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢績效分布 Service
 * 統計特定週期的績效評等分布
 */
@Service("getDistributionServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDistributionServiceImpl
        extends AbstractQueryService<StartCycleRequest, GetDistributionResponse> {

    private final IPerformanceReviewRepository reviewRepository;

    @Override
    protected QueryGroup buildQuery(StartCycleRequest request, JWTModel currentUser) {
        // 查詢特定週期下所有已完成的考核
        return QueryBuilder.where()
                .and("cycleId", Operator.EQ, request.getCycleId())
                .and("status", Operator.EQ, "COMPLETED")
                .build();
    }

    @Override
    protected GetDistributionResponse executeQuery(
            QueryGroup query, StartCycleRequest request, JWTModel currentUser, String... args) throws Exception {

        // 查詢所有資料 (無分頁)
        List<PerformanceReview> reviews = reviewRepository.findAll(query, Pageable.unpaged()).getContent();

        // 統計分布
        Map<String, Long> ratingCounts = reviews.stream()
                .collect(Collectors.groupingBy(
                        review -> review.getOverallRating() != null ? review.getOverallRating() : "UNRATED",
                        Collectors.counting()));

        int totalCount = reviews.size();
        Map<String, GetDistributionResponse.DistributionData> distribution = new HashMap<>();

        ratingCounts.forEach((rating, count) -> {
            double percentage = totalCount > 0 ? (count * 100.0 / totalCount) : 0.0;
            distribution.put(rating, GetDistributionResponse.DistributionData.builder()
                    .rating(rating)
                    .count(count.intValue())
                    .percentage(percentage)
                    .build());
        });

        return GetDistributionResponse.builder()
                .distribution(distribution)
                .totalCount(totalCount)
                .build();
    }
}
