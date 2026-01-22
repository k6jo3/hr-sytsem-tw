package com.company.hrms.recruitment.application.dto.job;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新職缺請求")
public class UpdateJobOpeningRequest implements Serializable {

    @Schema(description = "需求人數")
    private Integer numberOfPositions;

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

    @Schema(description = "雇用類型 (FULL_TIME/PART_TIME/CONTRACT)")
    private String employmentType;

    @Schema(description = "工作地點")
    private String workLocation;
}
