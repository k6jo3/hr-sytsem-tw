package com.company.hrms.iam.api.controller.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.api.request.profile.ChangePasswordRequest;
import com.company.hrms.iam.api.request.profile.UpdateProfileRequest;
import com.company.hrms.iam.api.response.profile.ProfileResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * IAM - 個人資料 Command Controller
 * 負責個人資料的更新操作
 * 
 * <p>
 * 命名規範：HR{DD}{Screen}CmdController
 * </p>
 * <p>
 * DD = 01 (IAM Domain)
 * </p>
 */
@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "HR01-Profile-Command", description = "個人資料寫入操作")
public class HR01ProfileCmdController extends CommandBaseController {

    /**
     * 更新個人資料
     */
    @Operation(summary = "更新個人資料", operationId = "updateProfile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestBody @Valid UpdateProfileRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 變更密碼
     */
    @Operation(summary = "變更密碼", operationId = "changePassword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤或密碼不符合規則"),
            @ApiResponse(responseCode = "401", description = "未授權或目前密碼不正確")
    })
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(request, currentUser);
        return ResponseEntity.noContent().build();
    }
}
