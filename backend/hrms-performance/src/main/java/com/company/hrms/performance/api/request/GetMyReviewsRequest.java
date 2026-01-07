package com.company.hrms.performance.api.request;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查詢我的考核列表請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢我的考核列表請求")
public class GetMyReviewsRequest {

    @QueryFilter(property = "cycleId", operator = Operator.EQ)
    @Schema(description = "週期ID")
    private String cycleId;

    @QueryFilter(property = "employeeId", operator = Operator.EQ)
    @Schema(description = "員工ID (系統自動帶入 currentUser)")
    private String employeeId;

    @QueryFilter(property = "status", operator = Operator.EQ)
    @Schema(description = "考核狀態")
    private com.company.hrms.performance.domain.model.valueobject.ReviewStatus status;

    @Schema(description = "頁碼", example = "1")
    private int page = 1;

    @Schema(description = "每頁筆數", example = "20")
    private int size = 20;
}
