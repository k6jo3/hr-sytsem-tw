package com.company.hrms.iam.api.request.permission;

import com.company.hrms.common.api.request.PageRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Query Permission List Request")
public class GetPermissionListRequest extends PageRequest {

    @Schema(description = "Search keyword")
    private String search;

    @Schema(description = "Resource filter")
    private String resource;

    @Schema(description = "Action filter")
    private String action;

    @Schema(description = "Tenant ID")
    private String tenantId;

    @Schema(description = "Role ID filter")
    private String roleId;

    @Schema(description = "Permission type filter")
    private String type;

    @Schema(description = "Module filter")
    private String module;
}
