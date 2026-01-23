package com.company.hrms.notification.api.controller;

import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.preference.UpdateNotificationPreferenceRequest;
import com.company.hrms.notification.api.response.preference.NotificationPreferenceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HR12 通知偏好設定 Command Controller
 * <p>
 * 處理通知偏好設定的命令操作 (PUT)
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@RestController
@RequestMapping("/api/v1/notifications/preferences")
@Tag(name = "HR12-通知偏好設定", description = "通知偏好設定命令操作")
public class HR12PreferenceCmdController extends CommandBaseController {

    /**
     * 更新通知偏好設定
     * <p>
     * 業務場景：員工調整自己的通知偏好設定
     * </p>
     *
     * @param request     更新請求
     * @param currentUser 當前使用者
     * @return 更新後的偏好設定
     * @throws Exception 業務邏輯例外
     */
    @PutMapping
    @Operation(
            summary = "更新通知偏好設定",
            description = "更新目前登入使用者的通知偏好設定，支援部分更新",
            operationId = "updateNotificationPreference"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<NotificationPreferenceResponse> updateNotificationPreference(
            @RequestBody UpdateNotificationPreferenceRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser
    ) throws Exception {
        NotificationPreferenceResponse response = execCommand(request, currentUser);
        return ResponseEntity.ok(response);
    }
}
