package com.company.hrms.organization.api.response.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 組織清單回應 DTO
 */
@Data
@Builder
@Schema(description = "組織清單回應")
public class OrganizationListResponse {

    @Schema(description = "組織清單")
    private List<OrganizationListItemResponse> items;

    @Schema(description = "總筆數")
    private long totalCount;
}
