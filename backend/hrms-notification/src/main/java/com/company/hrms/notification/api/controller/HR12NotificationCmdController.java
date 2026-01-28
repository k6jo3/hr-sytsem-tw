package com.company.hrms.notification.api.controller;

import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.notification.SendBatchNotificationRequest;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.company.hrms.notification.api.response.notification.SendBatchNotificationResponse;
import com.company.hrms.notification.api.response.notification.SendNotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * HR12 通知管理 Command Controller
 * <p>
 * 職責：
 * <ul>
 *     <li>發送通知</li>
 *     <li>批次發送通知</li>
 *     <li>測試發送通知</li>
 * </ul>
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "HR12 通知管理 - 命令操作", description = "通知發送相關 API")
public class HR12NotificationCmdController extends CommandBaseController {

    /**
     * 發送通知
     * <p>
     * 業務邏輯：
     * <ol>
     *     <li>若指定 templateCode，載入範本並替換變數</li>
     *     <li>根據 recipientId 查詢通知偏好設定</li>
     *     <li>過濾禁用的渠道</li>
     *     <li>若在靜音時段且優先級非 URGENT，延後發送</li>
     *     <li>建立通知記錄</li>
     *     <li>依渠道發送通知（並行處理）</li>
     *     <li>更新發送狀態</li>
     * </ol>
     * </p>
     *
     * @param request     發送通知請求
     * @param currentUser 當前使用者
     * @return 發送結果
     * @throws Exception 業務異常
     */
    @PostMapping("/send")
    @PreAuthorize("hasAuthority('NOTIFICATION:SEND')")
    @Operation(
            summary = "發送通知",
            description = "發送通知給指定員工，支援範本替換、渠道過濾、靜音時段處理"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "發送成功"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "權限不足"),
            @ApiResponse(responseCode = "404", description = "收件人或範本不存在"),
            @ApiResponse(responseCode = "500", description = "系統錯誤")
    })
    public ResponseEntity<SendNotificationResponse> sendNotification(
            @Valid @RequestBody SendNotificationRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser
    ) throws Exception {
        log.info("[HR12NotificationCmd] 發送通知 - 收件人: {}, 類型: {}, 渠道: {}",
                request.getRecipientId(),
                request.getNotificationType(),
                request.getChannels());

        SendNotificationResponse response = execCommand(request, currentUser);

        log.info("[HR12NotificationCmd] 通知發送完成 - 通知ID: {}, 狀態: {}",
                response.getNotificationId(),
                response.getStatus());

        return ResponseEntity.ok(response);
    }

    /**
     * 批次發送通知
     * <p>
     * 業務邏輯：
     * <ol>
     *     <li>取得收件人列表（直接指定或查詢）</li>
     *     <li>驗證收件人數量（上限 500 人）</li>
     *     <li>分批處理（每批 50 人）</li>
     *     <li>並行發送通知</li>
     *     <li>彙總發送結果</li>
     * </ol>
     * </p>
     *
     * @param request     批次發送請求
     * @param currentUser 當前使用者
     * @return 批次發送結果
     * @throws Exception 業務異常
     */
    @PostMapping("/send-batch")
    @PreAuthorize("hasAuthority('NOTIFICATION:SEND_BATCH')")
    @Operation(
            summary = "批次發送通知",
            description = "發送通知給多位員工，支援直接指定收件人或使用過濾條件查詢，批次上限 500 人"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "批次發送完成"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤或收件人超過上限"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "權限不足"),
            @ApiResponse(responseCode = "500", description = "系統錯誤")
    })
    public ResponseEntity<SendBatchNotificationResponse> sendBatchNotification(
            @Valid @RequestBody SendBatchNotificationRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser
    ) throws Exception {
        log.info("[HR12NotificationCmd] 批次發送通知 - 類型: {}, 渠道: {}",
                request.getNotificationType(),
                request.getChannels());

        SendBatchNotificationResponse response = execCommand(request, currentUser);

        log.info("[HR12NotificationCmd] 批次發送完成 - 總計: {}, 成功: {}, 失敗: {}",
                response.getTotalRecipients(),
                response.getSuccessCount(),
                response.getFailureCount());

        return ResponseEntity.ok(response);
    }

    /**
     * 測試發送通知
     * <p>
     * 業務邏輯：
     * <ol>
     *     <li>測試發送只發送給當前使用者</li>
     *     <li>強制使用 IN_APP 渠道（避免發送真實郵件/推播）</li>
     *     <li>可用於測試範本渲染效果</li>
     * </ol>
     * </p>
     *
     * @param request     發送通知請求
     * @param currentUser 當前使用者
     * @return 發送結果
     * @throws Exception 業務異常
     */
    @PostMapping("/test")
    @PreAuthorize("hasAuthority('NOTIFICATION:SEND')")
    @Operation(
            summary = "測試發送通知",
            description = "測試發送通知給當前使用者，僅使用 IN_APP 渠道，用於測試範本渲染效果"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "測試發送成功"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "權限不足"),
            @ApiResponse(responseCode = "500", description = "系統錯誤")
    })
    public ResponseEntity<SendNotificationResponse> sendTestNotification(
            @Valid @RequestBody SendNotificationRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser
    ) throws Exception {
        log.info("[HR12NotificationCmd] 測試發送通知 - 使用者: {}, 範本: {}",
                currentUser.getEmployeeNumber(),
                request.getTemplateCode());

        SendNotificationResponse response = execCommand(request, currentUser);

        log.info("[HR12NotificationCmd] 測試發送完成 - 通知ID: {}",
                response.getNotificationId());

        return ResponseEntity.ok(response);
    }
}
