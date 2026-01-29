package com.company.hrms.performance.api.request;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;
import com.company.hrms.performance.domain.model.valueobject.CycleStatus;
import com.company.hrms.performance.domain.model.valueobject.CycleType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查詢考核週期列表請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢考核週期列表請求")
public class GetCyclesRequest {

    @QueryFilter(property = "status", operator = Operator.EQ)
    @Schema(description = "週期狀態")
    private CycleStatus status;

    @QueryFilter(property = "cycleType", operator = Operator.EQ)
    @Schema(description = "考核類型")
    private CycleType cycleType;

    @QueryFilter(property = "year", operator = Operator.EQ)
    @Schema(description = "年份", example = "2025")
    private Integer year;

    @Builder.Default
    @Schema(description = "頁碼", example = "1")
    private int page = 1;

    @Builder.Default
    @Schema(description = "每頁筆數", example = "20")
    private int size = 20;
}
