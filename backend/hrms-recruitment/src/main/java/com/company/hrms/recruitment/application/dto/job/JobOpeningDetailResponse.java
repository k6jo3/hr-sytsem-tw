package com.company.hrms.recruitment.application.dto.job;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "職缺詳細資訊回應")
public class JobOpeningDetailResponse implements Serializable {

    @Schema(description = "職缺 ID")
    private String id;

    @Schema(description = "職缺標題")
    private String title;

    @Schema(description = "部門 ID")
    private String departmentId;

    @Schema(description = "需求人數")
    private Integer numberOfPositions;

    @Schema(description = "狀態")
    private String status;

    @Schema(description = "薪資範圍 - 最低")
    private BigDecimal minSalary;

    @Schema(description = "薪資範圍 - 最高")
    private BigDecimal maxSalary;

    @Schema(description = "薪資幣別")
    private String currency;

    @Schema(description = "職位要求")
    private String requirements;

    @Schema(description = "工作職責")
    private String responsibilities;

    @Schema(description = "雇用類型")
    private String employmentType;

    @Schema(description = "工作地點")
    private String workLocation;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;
}
