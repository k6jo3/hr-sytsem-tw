package com.company.hrms.performance.api.request;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查詢團隊考核列表請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢團隊考核列表請求")
public class GetTeamReviewsRequest {

    @QueryFilter(property = "cycleId", operator = Operator.EQ)
    @Schema(description = "週期ID")
    private String cycleId;

    @QueryFilter(property = "reviewerId", operator = Operator.EQ)
    @Schema(description = "評核者ID (系統自動帶入 currentUser)")
    private String reviewerId;

    @Builder.Default
    @Schema(description = "頁碼", example = "1")
    private int page = 1;

    @Builder.Default
    @Schema(description = "每頁筆數", example = "20")
    private int size = 20;
}
