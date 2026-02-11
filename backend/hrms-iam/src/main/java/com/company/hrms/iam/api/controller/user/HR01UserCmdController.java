package com.company.hrms.iam.api.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.api.request.user.AssignUserRolesRequest;
import com.company.hrms.iam.api.request.user.BatchDeactivateUsersRequest;
import com.company.hrms.iam.api.request.user.CreateUserRequest;
import com.company.hrms.iam.api.request.user.UpdateUserRequest;
import com.company.hrms.iam.api.response.user.AssignUserRolesResponse;
import com.company.hrms.iam.api.response.user.BatchDeactivateUsersResponse;
import com.company.hrms.iam.api.response.user.CreateUserResponse;
import com.company.hrms.iam.api.response.user.UserDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * IAM - 使用者管理 Command Controller
 * 負責使用者的新增、修改、刪除等寫入操作
 * 
 * <p>
 * 命名規範：HR{DD}{Screen}CmdController
 * </p>
 * <p>
 * DD = 01 (IAM Domain)
 * </p>
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "HR01-User-Command", description = "使用者管理寫入操作")
public class HR01UserCmdController extends CommandBaseController {

    /**
     * 新增使用者
     */
    @Operation(summary = "新增使用者", operationId = "createUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "409", description = "使用者名稱或 Email 已存在")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<CreateUserResponse> createUser(
            @RequestBody @Valid CreateUserRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        CreateUserResponse response = execCommand(request, currentUser);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * 更新使用者
     */
    @Operation(summary = "更新使用者", operationId = "updateUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "使用者不存在")
    })
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<UserDetailResponse> updateUser(
            @PathVariable String userId,
            @RequestBody @Valid UpdateUserRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, userId));
    }

    /**
     * 啟用使用者
     */
    @Operation(summary = "啟用使用者", operationId = "activateUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "使用者不存在")
    })
    @PutMapping("/{userId}/activate")
    @PreAuthorize("hasAuthority('user:activate')")
    public ResponseEntity<Void> activateUser(
            @PathVariable String userId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(null, currentUser, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 停用使用者
     */
    @Operation(summary = "停用使用者", operationId = "deactivateUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "使用者不存在")
    })
    @PutMapping("/{userId}/deactivate")
    @PreAuthorize("hasAuthority('user:deactivate')")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable String userId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(null, currentUser, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 指派角色給使用者
     */
    @Operation(summary = "指派角色給使用者", operationId = "assignUserRoles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "使用者或角色不存在")
    })
    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('user:assign-role')")
    public ResponseEntity<AssignUserRolesResponse> assignUserRoles(
            @PathVariable String userId,
            @RequestBody @Valid AssignUserRolesRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, userId));
    }

    /**
     * 批次停用使用者
     */
    @Operation(summary = "批次停用使用者", operationId = "batchDeactivateUsers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @PutMapping("/batch-deactivate")
    @PreAuthorize("hasAuthority('user:deactivate')")
    public ResponseEntity<BatchDeactivateUsersResponse> batchDeactivateUsers(
            @RequestBody @Valid BatchDeactivateUsersRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

}
