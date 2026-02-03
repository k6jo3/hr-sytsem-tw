package com.company.hrms.workflow.api.request;

import com.company.hrms.common.query.QueryCondition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查詢我的申請請求")
public class GetMyApplicationsRequest {

    @Schema(description = "業務類型")
    @QueryCondition.EQ
    private String businessType;

    @Schema(description = "狀態")
    @QueryCondition.EQ
    private String status;

    @Schema(description = "開始時間 (起)")
    @QueryCondition.GTE("startedAt")
    private String startedAtFrom;

    @Schema(description = "開始時間 (迄)")
    @QueryCondition.LTE("startedAt")
    private String startedAtTo;

    // Internal filter set by service
    @QueryCondition.EQ("applicantId")
    private String applicantId;
}
