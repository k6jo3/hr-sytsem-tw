package com.company.hrms.iam.api.controller.role;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.api.response.ApiResponse;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.api.request.role.GetRoleListRequest;
import com.company.hrms.iam.api.response.role.RoleDetailResponse;
import com.company.hrms.iam.api.response.role.RoleListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * IAM - 角色管理 Query Controller
 * 負責角色的查詢操作
 */
@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "HR01-Role-Query", description = "角色管理查詢操作")
public class HR01RoleQryController extends QueryBaseController {

        /**
         * 查詢角色列表
         */
        @Operation(summary = "查詢角色列表", operationId = "getRoleList")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping
        @PreAuthorize("hasAuthority('role:read')")
        public ResponseEntity<List<RoleListResponse>> getRoleList(
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false, name = "isSystemRole") Boolean isSystemRole,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

                GetRoleListRequest request = GetRoleListRequest.builder()
                                .name(name)
                                .status(status)
                                .isSystemRole(isSystemRole)
                                .build();

                return ResponseEntity.ok(getResponse(request, currentUser));
        }

        /**
         * 查詢單一角色
         */
        @Operation(summary = "查詢單一角色", operationId = "getRole")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授權"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "角色不存在")
        })
        @GetMapping("/{roleId}")
        @PreAuthorize("hasAuthority('role:read')")
        public ResponseEntity<ApiResponse<RoleDetailResponse>> getRole(
                        @PathVariable String roleId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                RoleDetailResponse detail = getResponse(new GetRoleRequest(), currentUser, roleId);
                return ResponseEntity.ok(ApiResponse.success(detail));
        }

        /**
         * 查詢系統角色列表
         */
        @Operation(summary = "查詢系統角色列表", operationId = "getSystemRoles")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping("/system")
        @PreAuthorize("hasAuthority('role:read')")
        public ResponseEntity<List<RoleListResponse>> getSystemRoles(
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(new GetSystemRolesRequest(), currentUser));
        }

        /**
         * 取得角色請求 (內部類別)
         */
        public static class GetRoleRequest {
        }

        /**
         * 取得系統角色請求 (內部類別)
         */
        public static class GetSystemRolesRequest {
        }
}
