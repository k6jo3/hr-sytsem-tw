package com.company.hrms.organization.api.response.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 合約清單回應 DTO
 */
@Data
@Builder
@Schema(description = "合約清單回應")
public class ContractListResponse {

    @Schema(description = "合約清單")
    private List<ContractDetailResponse> items;

    @Schema(description = "總筆數")
    private int totalCount;
}
