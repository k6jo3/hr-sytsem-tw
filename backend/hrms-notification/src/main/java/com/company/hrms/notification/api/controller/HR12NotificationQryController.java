package com.company.hrms.notification.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.response.notification.GetMyNotificationsResponse;
import com.company.hrms.notification.api.response.notification.NotificationDetailResponse;
import com.company.hrms.notification.api.response.notification.UnreadCountResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HR12 通知查詢 Controller
 * <p>
 * 職責：
 * <ul>
 * <li>查詢我的通知列表</li>
 * <li>查詢通知詳情</li>
 * <li>查詢未讀通知數量</li>
 * </ul>
 * </p>
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "HR12 通知管理 - 查詢操作", description = "通知查詢相關 API")
public class HR12NotificationQryController extends QueryBaseController {

        /**
         * 查詢我的通知列表
         */
        @GetMapping("/me")
        @Operation(summary = "查詢我的通知列表", description = "查詢當前使用者的通知列表，支援狀態、類型、日期區間篩選")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查詢成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<GetMyNotificationsResponse> getMyNotifications(
                        @Parameter(description = "狀態篩選（SENT/READ）") @RequestParam(required = false) String status,
                        @Parameter(description = "通知類型篩選") @RequestParam(required = false) String notificationType,
                        @Parameter(description = "開始日期") @RequestParam(required = false) String startDate,
                        @Parameter(description = "結束日期") @RequestParam(required = false) String endDate,
                        @Parameter(description = "頁碼") @RequestParam(defaultValue = "1") Integer page,
                        @Parameter(description = "每頁筆數") @RequestParam(defaultValue = "20") Integer pageSize,
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
                log.info("[HR12NotificationQry] 查詢我的通知列表 - 使用者: {}, 狀態: {}, 類型: {}",
                                currentUser.getEmployeeNumber(), status, notificationType);

                GetMyNotificationsResponse response = getResponse(null, currentUser,
                                status, notificationType, startDate, endDate,
                                String.valueOf(page), String.valueOf(pageSize));

                log.info("[HR12NotificationQry] 通知列表查詢完成 - 總筆數: {}, 未讀: {}",
                                response.getPagination() != null ? response.getPagination().getTotalItems() : 0,
                                response.getSummary() != null ? response.getSummary().getTotalUnread() : 0);

                return ResponseEntity.ok(response);
        }

        /**
         * 查詢通知詳情
         */
        @GetMapping("/{id}")
        @Operation(summary = "查詢通知詳情", description = "查詢指定通知的完整內容")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查詢成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "403", description = "無權查看此通知"),
                        @ApiResponse(responseCode = "404", description = "通知不存在"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<NotificationDetailResponse> getNotificationDetail(
                        @Parameter(description = "通知 ID", required = true) @PathVariable String id,
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
                log.info("[HR12NotificationQry] 查詢通知詳情 - 通知ID: {}, 使用者: {}",
                                id, currentUser.getEmployeeNumber());

                NotificationDetailResponse response = getResponse(null, currentUser, id);

                log.info("[HR12NotificationQry] 通知詳情查詢完成 - 通知ID: {}", id);

                return ResponseEntity.ok(response);
        }

        /**
         * 查詢未讀通知數量
         */
        @GetMapping("/unread-count")
        @Operation(summary = "查詢未讀通知數量", description = "查詢當前使用者的未讀通知數量，按類型分組")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查詢成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<UnreadCountResponse> getUnreadCount(
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
                log.info("[HR12NotificationQry] 查詢未讀數量 - 使用者: {}", currentUser.getEmployeeNumber());

                UnreadCountResponse response = getResponse(null, currentUser);

                log.info("[HR12NotificationQry] 未讀數量查詢完成 - 總未讀: {}", response.getUnreadCount());

                return ResponseEntity.ok(response);
        }
}
