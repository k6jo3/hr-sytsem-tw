package com.company.hrms.iam.api.controller.user;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.api.request.user.CreateUserRequest;
import com.company.hrms.iam.api.request.user.UpdateUserRequest;
import com.company.hrms.iam.api.response.user.CreateUserResponse;
import com.company.hrms.iam.api.response.user.UserDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * IAM - 使用者管理 Command Controller
 * 負責使用者的新增、修改、刪除等寫入操作
 * 
 * <p>命名規範：HR{DD}{Screen}CmdController</p>
 * <p>DD = 01 (IAM Domain)</p>
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
    public ResponseEntity<CreateUserResponse> createUser(
            @RequestBody @Valid CreateUserRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
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
    public ResponseEntity<UserDetailResponse> updateUser(
            @PathVariable String userId,
            @RequestBody @Valid UpdateUserRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, userId));
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
    public ResponseEntity<Void> deactivateUser(
            @PathVariable String userId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(null, currentUser, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 刪除使用者
     */
    @Operation(summary = "刪除使用者", operationId = "deleteUser")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "成功"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "404", description = "使用者不存在")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String userId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(null, currentUser, userId);
        return ResponseEntity.noContent().build();
    }
}
