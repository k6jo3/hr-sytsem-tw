package com.company.hrms.iam.api.controller.role;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.api.request.role.CreateRoleRequest;
import com.company.hrms.iam.api.request.role.UpdateRoleRequest;
import com.company.hrms.iam.api.response.role.CreateRoleResponse;
import com.company.hrms.iam.api.response.role.RoleDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * IAM - 角色管理 Command Controller
 * 負責角色的新增、修改、刪除等寫入操作
 *
 * <p>命名規範：HR{DD}{Screen}CmdController</p>
 * <p>DD = 01 (IAM Domain)</p>
 */
@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "HR01-Role-Command", description = "角色管理寫入操作")
public class HR01RoleCmdController extends CommandBaseController {

    /**
     * 新增角色
     */
    @Operation(summary = "新增角色", operationId = "createRole")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "無權限"),
            @ApiResponse(responseCode = "409", description = "角色代碼已存在")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<CreateRoleResponse> createRole(
            @RequestBody @Valid CreateRoleRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 更新角色
     */
    @Operation(summary = "更新角色", operationId = "updateRole")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "無權限或系統角色不可修改"),
            @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @PutMapping("/{roleId}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<RoleDetailResponse> updateRole(
            @PathVariable String roleId,
            @RequestBody @Valid UpdateRoleRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, roleId));
    }

    /**
     * 停用角色
     */
    @Operation(summary = "停用角色", operationId = "deactivateRole")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "無權限或系統角色不可停用"),
            @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @PutMapping("/{roleId}/deactivate")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<Void> deactivateRole(
            @PathVariable String roleId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(null, currentUser, roleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 啟用角色
     */
    @Operation(summary = "啟用角色", operationId = "activateRole")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "無權限"),
            @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @PutMapping("/{roleId}/activate")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<Void> activateRole(
            @PathVariable String roleId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(null, currentUser, roleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 刪除角色
     */
    @Operation(summary = "刪除角色", operationId = "deleteRole")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "無權限或系統角色不可刪除"),
            @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<Void> deleteRole(
            @PathVariable String roleId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(null, currentUser, roleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 指派權限給角色
     */
    @Operation(summary = "指派權限給角色", operationId = "assignPermissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "無權限"),
            @ApiResponse(responseCode = "404", description = "角色或權限不存在")
    })
    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:assign-permission')")
    public ResponseEntity<Void> assignPermissions(
            @PathVariable String roleId,
            @RequestBody AssignPermissionsRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(request, currentUser, roleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 指派權限請求
     */
    public record AssignPermissionsRequest(java.util.List<String> permissionIds) {}
}
