package com.company.hrms.workflow.api.request;

import com.company.hrms.common.query.QueryCondition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查詢代理人請求")
public class GetDelegationsRequest {
    // Usually retrieved from Token, but can be explicit for Admin
    @Schema(description = "使用者ID (若為空則查自己)")
    @QueryCondition.EQ("delegatorId")
    private String userId;
}
