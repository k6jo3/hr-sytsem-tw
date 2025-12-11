package com.company.hrms.iam.api.controller.auth;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.api.request.auth.RefreshTokenRequest;
import com.company.hrms.iam.api.response.auth.LoginResponse;
import com.company.hrms.iam.api.response.auth.RefreshTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * IAM - 認證 Command Controller
 * 負責登入、登出、Token 重新整理等認證操作
 *
 * <p>命名規範：HR{DD}{Screen}CmdController</p>
 * <p>DD = 01 (IAM Domain)</p>
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "HR01-Auth-Command", description = "認證操作 (登入/登出)")
public class HR01AuthCmdController extends CommandBaseController {

    /**
     * 使用者登入
     */
    @Operation(summary = "使用者登入", operationId = "login",
            description = "使用帳號密碼登入系統，成功後返回 JWT Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登入成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "帳號或密碼錯誤"),
            @ApiResponse(responseCode = "423", description = "帳號已鎖定")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request) throws Exception {
        return ResponseEntity.ok(execCommand(request, null));
    }

    /**
     * 重新整理 Token
     */
    @Operation(summary = "重新整理 Token", operationId = "refreshToken",
            description = "使用 Refresh Token 取得新的 Access Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "Refresh Token 無效或已過期")
    })
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @RequestBody @Valid RefreshTokenRequest request) throws Exception {
        return ResponseEntity.ok(execCommand(request, null));
    }

    /**
     * 使用者登出
     */
    @Operation(summary = "使用者登出", operationId = "logout",
            description = "登出系統，使 Refresh Token 失效")
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
}
