package com.company.hrms.organization.api.controller.organization;

import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.security.CurrentUser;
import com.company.hrms.organization.api.request.organization.CreateOrganizationRequest;
import com.company.hrms.organization.api.request.organization.UpdateOrganizationRequest;
import com.company.hrms.organization.api.response.organization.CreateOrganizationResponse;
import com.company.hrms.organization.api.response.organization.OrganizationDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 組織管理寫入控制器
 */
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR02-Organization-Command", description = "組織管理寫入操作")
public class HR02OrganizationCmdController extends CommandBaseController {

    @PostMapping
    @Operation(summary = "新增組織", operationId = "createOrganization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "409", description = "組織代碼已存在")
    })
    public ResponseEntity<CreateOrganizationResponse> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        log.info("Creating organization: {}", request.getCode());
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @PutMapping("/{organizationId}")
    @Operation(summary = "更新組織", operationId = "updateOrganization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "404", description = "組織不存在")
    })
    public ResponseEntity<OrganizationDetailResponse> updateOrganization(
            @PathVariable String organizationId,
            @Valid @RequestBody UpdateOrganizationRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        log.info("Updating organization: {}", organizationId);
        return ResponseEntity.ok(execCommand(request, currentUser, organizationId));
    }

    @PutMapping("/{organizationId}/deactivate")
    @Operation(summary = "停用組織", operationId = "deactivateOrganization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "組織下有在職員工，無法停用"),
            @ApiResponse(responseCode = "404", description = "組織不存在")
    })
    public ResponseEntity<OrganizationDetailResponse> deactivateOrganization(
            @PathVariable String organizationId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        log.info("Deactivating organization: {}", organizationId);
        return ResponseEntity.ok(execCommand(null, currentUser, organizationId));
    }
}
