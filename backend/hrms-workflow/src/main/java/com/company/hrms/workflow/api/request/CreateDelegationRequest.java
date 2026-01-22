package com.company.hrms.workflow.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "建立代理人請求")
public class CreateDelegationRequest {

    @Schema(description = "代理人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @com.fasterxml.jackson.annotation.JsonProperty("delegatee_id")
    private String delegateeId;

    @Schema(description = "開始日期 (YYYY-MM-DD)", requiredMode = Schema.RequiredMode.REQUIRED)
    @com.fasterxml.jackson.annotation.JsonProperty("start_date")
    private String startDate;

    @Schema(description = "結束日期 (YYYY-MM-DD)", requiredMode = Schema.RequiredMode.REQUIRED)
    @com.fasterxml.jackson.annotation.JsonProperty("end_date")
    private String endDate;

    @Schema(description = "原因")
    private String reason;
}
