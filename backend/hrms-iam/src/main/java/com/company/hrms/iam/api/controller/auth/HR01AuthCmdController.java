package com.company.hrms.iam.api.controller.auth;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.api.request.auth.AdminResetPasswordRequest;
import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.api.request.auth.RefreshTokenRequest;
import com.company.hrms.iam.api.request.auth.ResetPasswordRequest;
import com.company.hrms.iam.api.response.auth.LoginResponse;
import com.company.hrms.iam.api.response.auth.RefreshTokenResponse;
import com.company.hrms.iam.api.response.auth.ResetPasswordResponse;
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
 * IAM - 認證管理 Command Controller
 * 負責登入、登出、Token 刷新、密碼重設等操作
 *
 * <p>命名規範：HR{DD}{Screen}CmdController</p>
 * <p>DD = 01 (IAM Domain)</p>
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "HR01-Auth-Command", description = "認證管理操作")
public class HR01AuthCmdController extends CommandBaseController {

    /**
     * 使用者登入
     */
    @Operation(summary = "使用者登入", operationId = "login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登入成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "帳號或密碼錯誤"),
            @ApiResponse(responseCode = "423", description = "帳號已被鎖定")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request) throws Exception {
        return ResponseEntity.ok(execCommand(request, null));
    }

    /**
     * Token 刷新
     */
    @Operation(summary = "刷新 Token", operationId = "refreshToken")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "刷新成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "Token 無效或已過期")
    })
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @RequestBody @Valid RefreshTokenRequest request) throws Exception {
        return ResponseEntity.ok(execCommand(request, null));
    }

    /**
     * 使用者登出
     */
    @Operation(summary = "使用者登出", operationId = "logout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "登出成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(null, currentUser);
        return ResponseEntity.noContent().build();
    }

    /**
     * 使用者自行變更密碼
     */
    @Operation(summary = "變更密碼", operationId = "resetPassword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "密碼變更成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤或密碼不符合規則"),
            @ApiResponse(responseCode = "401", description = "未授權或當前密碼不正確")
    })
    @PostMapping("/password/reset")
    public ResponseEntity<ResetPasswordResponse> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 管理員重設使用者密碼
     */
    @Operation(summary = "管理員重設使用者密碼", operationId = "adminResetPassword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "密碼重設成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "無權限"),
            @ApiResponse(responseCode = "404", description = "使用者不存在")
    })
    @PostMapping("/users/{userId}/password/reset")
    @PreAuthorize("hasAuthority('user:reset-password')")
    public ResponseEntity<ResetPasswordResponse> adminResetPassword(
            @PathVariable String userId,
            @RequestBody @Valid AdminResetPasswordRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, userId));
    }
}
