package com.company.hrms.workflow.api.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "代理人列表回應")
public class GetDelegationsResponse {

    @Schema(description = "代理人設定列表")
    private List<DelegationResponse> data;
}
