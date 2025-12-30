package com.company.hrms.iam.api.controller.permission;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.api.response.permission.PermissionListResponse;
import com.company.hrms.iam.api.response.permission.PermissionTreeResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * IAM - 權限管理 Query Controller
 * 負責權限的查詢操作
 * 
 * <p>
 * 命名規範：HR{DD}{Screen}QryController
 * </p>
 * <p>
 * DD = 01 (IAM Domain)
 * </p>
 */
@RestController
@RequestMapping("/api/v1/permissions")
@Tag(name = "HR01-Permission-Query", description = "權限管理查詢操作")
public class HR01PermissionQryController extends QueryBaseController {

    /**
     * 查詢權限列表
     */
    @Operation(summary = "查詢權限列表", operationId = "getPermissionList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<List<PermissionListResponse>> getPermissionList(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(null, currentUser));
    }

    /**
     * 查詢權限樹
     */
    @Operation(summary = "查詢權限樹", operationId = "getPermissionTree")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<List<PermissionTreeResponse>> getPermissionTree(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(null, currentUser));
    }
}
