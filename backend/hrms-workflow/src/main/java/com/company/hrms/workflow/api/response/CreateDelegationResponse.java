package com.company.hrms.workflow.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "建立代理人回應")
public class CreateDelegationResponse {

    @Schema(description = "代理設定ID")
    private String delegationId;

    @Schema(description = "設定狀態")
    private String status;
}
