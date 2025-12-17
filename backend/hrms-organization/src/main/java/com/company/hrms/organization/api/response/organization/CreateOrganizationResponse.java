package com.company.hrms.organization.api.response.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 新增組織回應 DTO
 */
@Data
@Builder
@Schema(description = "新增組織回應")
public class CreateOrganizationResponse {

    @Schema(description = "組織ID")
    private String organizationId;

    @Schema(description = "組織代碼")
    private String code;

    @Schema(description = "組織名稱")
    private String name;

    @Schema(description = "訊息")
    private String message;

    public static CreateOrganizationResponse success(String organizationId, String code, String name) {
        return CreateOrganizationResponse.builder()
                .organizationId(organizationId)
                .code(code)
                .name(name)
                .message("組織新增成功")
                .build();
    }
}
