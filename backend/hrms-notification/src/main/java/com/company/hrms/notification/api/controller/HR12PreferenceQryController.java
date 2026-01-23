package com.company.hrms.notification.api.controller;

import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.response.preference.NotificationPreferenceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HR12 通知偏好設定 Query Controller
 * <p>
 * 處理通知偏好設定的查詢操作 (GET)
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@RestController
@RequestMapping("/api/v1/notifications/preferences")
@Tag(name = "HR12-通知偏好設定", description = "通知偏好設定查詢操作")
public class HR12PreferenceQryController extends QueryBaseController {

    /**
     * 查詢通知偏好設定
     * <p>
     * 業務場景：員工查看自己的通知偏好設定
     * </p>
     *
     * @param currentUser 當前使用者
     * @return 偏好設定
     * @throws Exception 業務邏輯例外
     */
    @GetMapping
    @Operation(
            summary = "查詢通知偏好設定",
            description = "查詢目前登入使用者的通知偏好設定，若未設定則回傳預設值",
            operationId = "getNotificationPreference"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<NotificationPreferenceResponse> getNotificationPreference(
            @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser
    ) throws Exception {
        NotificationPreferenceResponse response = getResponse(null, currentUser);
        return ResponseEntity.ok(response);
    }
}
