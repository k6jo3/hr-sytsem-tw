package com.company.hrms.organization.api.controller.organization;

import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.security.CurrentUser;
import com.company.hrms.organization.api.response.organization.OrganizationDetailResponse;
import com.company.hrms.organization.api.response.organization.OrganizationListResponse;
import com.company.hrms.organization.api.response.organization.OrganizationTreeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 組織管理查詢控制器
 */
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR02-Organization-Query", description = "組織管理查詢操作")
public class HR02OrganizationQryController extends QueryBaseController {

    @GetMapping
    @Operation(summary = "查詢組織清單", operationId = "getOrganizationList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功")
    })
    public ResponseEntity<OrganizationListResponse> getOrganizationList(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        log.info("Getting organization list");
        return ResponseEntity.ok(getResponse(null, currentUser));
    }

    @GetMapping("/{organizationId}")
    @Operation(summary = "查詢組織詳情", operationId = "getOrganizationDetail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "404", description = "組織不存在")
    })
    public ResponseEntity<OrganizationDetailResponse> getOrganizationDetail(
            @PathVariable String organizationId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        log.info("Getting organization detail: {}", organizationId);
        return ResponseEntity.ok(getResponse(null, currentUser, organizationId));
    }

    @GetMapping("/{organizationId}/tree")
    @Operation(summary = "查詢組織樹狀結構", operationId = "getOrganizationTree")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "404", description = "組織不存在")
    })
    public ResponseEntity<OrganizationTreeResponse> getOrganizationTree(
            @PathVariable String organizationId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        log.info("Getting organization tree: {}", organizationId);
        return ResponseEntity.ok(getResponse(null, currentUser, organizationId));
    }
}
