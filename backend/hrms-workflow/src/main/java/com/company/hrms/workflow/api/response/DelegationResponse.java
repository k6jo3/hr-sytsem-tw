package com.company.hrms.workflow.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "代理人設定詳情")
public class DelegationResponse {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "代理人ID")
    @com.fasterxml.jackson.annotation.JsonProperty("delegatee_id")
    private String delegateeId;

    @Schema(description = "代理人名稱")
    @com.fasterxml.jackson.annotation.JsonProperty("delegatee_name")
    private String delegateeName; // Need to fetch name or just ID? Let's provide ID first. Name might need View
                                  // Model Factory or Join.

    @Schema(description = "開始日期")
    @com.fasterxml.jackson.annotation.JsonProperty("start_date")
    private String startDate;

    @Schema(description = "結束日期")
    @com.fasterxml.jackson.annotation.JsonProperty("end_date")
    private String endDate;

    @Schema(description = "狀態")
    private String status; // Active/Inactive
}
