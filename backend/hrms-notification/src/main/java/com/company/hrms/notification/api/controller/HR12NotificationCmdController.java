package com.company.hrms.notification.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.notification.SendBatchNotificationRequest;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.company.hrms.notification.api.response.notification.MarkAllReadResponse;
import com.company.hrms.notification.api.response.notification.MarkNotificationReadResponse;
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

/**
 * HR12 通知管理 Command Controller
 * <p>
 * 職責：
 * <ul>
 * <li>發送通知</li>
 * <li>批次發送通知</li>
 * <li>測試發送通知</li>
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
         * <li>若指定 templateCode，載入範本並替換變數</li>
         * <li>根據 recipientId 查詢通知偏好設定</li>
         * <li>過濾禁用的渠道</li>
         * <li>若在靜音時段且優先級非 URGENT，延後發送</li>
         * <li>建立通知記錄</li>
         * <li>依渠道發送通知（並行處理）</li>
         * <li>更新發送狀態</li>
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
        @Operation(summary = "發送通知", description = "發送通知給指定員工，支援範本替換、渠道過濾、靜音時段處理")
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
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
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
         * <li>取得收件人列表（直接指定或查詢）</li>
         * <li>驗證收件人數量（上限 500 人）</li>
         * <li>分批處理（每批 50 人）</li>
         * <li>並行發送通知</li>
         * <li>彙總發送結果</li>
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
        @Operation(summary = "批次發送通知", description = "發送通知給多位員工，支援直接指定收件人或使用過濾條件查詢，批次上限 500 人")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "批次發送完成"),
                        @ApiResponse(responseCode = "400", description = "請求參數錯誤或收件人超過上限"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "403", description = "權限不足"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<SendBatchNotificationResponse> sendBatchNotification(
                        @Valid @RequestBody SendBatchNotificationRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
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
         * <li>測試發送只發送給當前使用者</li>
         * <li>強制使用 IN_APP 渠道（避免發送真實郵件/推播）</li>
         * <li>可用於測試範本渲染效果</li>
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
        @Operation(summary = "測試發送通知", description = "測試發送通知給當前使用者，僅使用 IN_APP 渠道，用於測試範本渲染效果")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "測試發送成功"),
                        @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "403", description = "權限不足"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<SendNotificationResponse> sendTestNotification(
                        @Valid @RequestBody SendNotificationRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
                log.info("[HR12NotificationCmd] 測試發送通知 - 使用者: {}, 範本: {}",
                                currentUser.getEmployeeNumber(),
                                request.getTemplateCode());

                SendNotificationResponse response = execCommand(request, currentUser);

                log.info("[HR12NotificationCmd] 測試發送完成 - 通知ID: {}",
                                response.getNotificationId());

                return ResponseEntity.ok(response);
        }

        /**
         * 標記通知為已讀
         * <p>
         * 業務邏輯：
         * <ol>
         * <li>驗證通知存在且屬於當前使用者</li>
         * <li>更新通知狀態為 READ</li>
         * <li>設置 readAt 時間</li>
         * <li>發布 NotificationReadEvent</li>
         * </ol>
         * </p>
         *
         * @param id          通知 ID
         * @param currentUser 當前使用者
         * @return 標記已讀結果
         * @throws Exception 業務異常
         */
        @PutMapping("/{id}/read")
        @Operation(summary = "標記通知為已讀", description = "將指定通知標記為已讀")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "標記成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "403", description = "無權操作此通知"),
                        @ApiResponse(responseCode = "404", description = "通知不存在"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<MarkNotificationReadResponse> markNotificationRead(
                        @Parameter(description = "通知 ID", required = true) @PathVariable String id,
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
                log.info("[HR12NotificationCmd] 標記通知為已讀 - 通知ID: {}, 使用者: {}",
                                id, currentUser.getEmployeeNumber());

                MarkNotificationReadResponse response = execCommand(id, currentUser);

                log.info("[HR12NotificationCmd] 通知已標記為已讀 - 通知ID: {}", id);

                return ResponseEntity.ok(response);
        }

        /**
         * 標記全部為已讀
         * <p>
         * 業務邏輯：
         * <ol>
         * <li>查詢當前使用者所有未讀通知</li>
         * <li>批次更新狀態為 READ</li>
         * <li>設置 readAt 時間</li>
         * <li>回傳已標記的數量</li>
         * </ol>
         * </p>
         *
         * @param currentUser 當前使用者
         * @return 批次標記已讀結果
         * @throws Exception 業務異常
         */
        @PutMapping("/read-all")
        @Operation(summary = "標記全部為已讀", description = "將當前使用者的所有未讀通知標記為已讀")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "標記成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<MarkAllReadResponse> markAllNotificationsRead(
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
                log.info("[HR12NotificationCmd] 標記全部為已讀 - 使用者: {}", currentUser.getEmployeeNumber());

                MarkAllReadResponse response = execCommand(null, currentUser);

                log.info("[HR12NotificationCmd] 全部通知已標記為已讀 - 已標記數量: {}", response.getMarkedCount());

                return ResponseEntity.ok(response);
        }

        /**
         * 刪除通知
         * <p>
         * 業務邏輯：
         * <ol>
         * <li>驗證通知存在且屬於當前使用者</li>
         * <li>軟刪除通知（設置 is_deleted = true）</li>
         * </ol>
         * </p>
         *
         * @param id          通知 ID
         * @param currentUser 當前使用者
         * @return 刪除結果
         * @throws Exception 業務異常
         */
        @DeleteMapping("/{id}")
        @Operation(summary = "刪除通知", description = "刪除指定通知（軟刪除）")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "刪除成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "403", description = "無權操作此通知"),
                        @ApiResponse(responseCode = "404", description = "通知不存在"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<Void> deleteNotification(
                        @Parameter(description = "通知 ID", required = true) @PathVariable String id,
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
                log.info("[HR12NotificationCmd] 刪除通知 - 通知ID: {}, 使用者: {}",
                                id, currentUser.getEmployeeNumber());

                execCommand(id, currentUser);

                log.info("[HR12NotificationCmd] 通知已刪除 - 通知ID: {}", id);

                return ResponseEntity.ok().build();
        }
}
