package com.company.hrms.performance.application.service.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.api.response.GetDistributionResponse;
import com.company.hrms.performance.application.service.context.GetDistributionContext;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;

/**
 * 計算績效分布 Task
 */
@Component
public class CalculateDistributionTask implements PipelineTask<GetDistributionContext> {

    @Override
    public void execute(GetDistributionContext context) {
        List<PerformanceReview> reviews = context.getReviews();
        if (reviews == null) {
            reviews = List.of();
        }

        // 統計分布 (Group by Rating)
        Map<String, Long> ratingCounts = reviews.stream()
                .collect(Collectors.groupingBy(
                        review -> review.getOverallRating() != null ? review.getOverallRating() : "UNRATED",
                        Collectors.counting()));

        int totalCount = reviews.size();
        Map<String, GetDistributionResponse.DistributionData> distribution = new HashMap<>();

        // 計算百分比並建立 Response Item
        ratingCounts.forEach((rating, count) -> {
            double percentage = totalCount > 0 ? (count * 100.0 / totalCount) : 0.0;
            distribution.put(rating, GetDistributionResponse.DistributionData.builder()
                    .rating(rating)
                    .count(count.intValue())
                    .percentage(percentage)
                    .build());
        });

        // 計算平均分數
        double averageScore = reviews.stream()
                .filter(r -> r.getFinalScore() != null)
                .mapToDouble(r -> r.getFinalScore().doubleValue())
                .average()
                .orElse(0.0);

        GetDistributionResponse response = GetDistributionResponse.builder()
                .distribution(distribution)
                .totalCount(totalCount)
                .totalEmployees(totalCount)
                .averageScore(Math.round(averageScore * 100.0) / 100.0)
                .build();

        context.setResponse(response);
    }

    @Override
    public String getName() {
        return "計算績效分布";
    }
}
