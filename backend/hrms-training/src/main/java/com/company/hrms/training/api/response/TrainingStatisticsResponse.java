package com.company.hrms.training.api.response;

import java.math.BigDecimal;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "訓練統計報告")
public class TrainingStatisticsResponse {

    @Schema(description = "總課程數")
    private Integer totalCourses;

    @Schema(description = "總報名人次")
    private Integer totalEnrollments;

    @Schema(description = "總訓練時數")
    private BigDecimal totalTrainingHours;

    @Schema(description = "完成率")
    private Double completionRate;

    @Schema(description = "按類別統計")
    private Map<String, Integer> coursesByCategory;

    @Schema(description = "按部門統計時數")
    private Map<String, BigDecimal> hoursByDepartment;
}
