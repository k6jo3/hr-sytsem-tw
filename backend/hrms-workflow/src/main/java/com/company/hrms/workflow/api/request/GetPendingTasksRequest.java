package com.company.hrms.workflow.api.request;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查詢待辦任務請求
 */
@Data
@Schema(description = "查詢待辦任務請求")
public class GetPendingTasksRequest {

    @Schema(description = "使用者 ID (自動填入)", hidden = true)
    @QueryFilter(property = "assigneeId", operator = Operator.EQ)
    private String userId;

    // Status is implicitly PENDING for this service, or we can allow filtering
    @Schema(description = "任務狀態", hidden = true)
    @QueryFilter(property = "status", operator = Operator.EQ)
    private String status = "PENDING";

    @Schema(description = "頁碼 (0-indexed)", example = "0")
    private Integer page;

    @Schema(description = "每頁筆數", example = "20")
    private Integer size;
}
