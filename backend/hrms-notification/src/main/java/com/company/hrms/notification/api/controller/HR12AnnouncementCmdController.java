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
import com.company.hrms.notification.api.request.announcement.CreateAnnouncementRequest;
import com.company.hrms.notification.api.request.announcement.UpdateAnnouncementRequest;
import com.company.hrms.notification.api.response.announcement.CreateAnnouncementResponse;
import com.company.hrms.notification.api.response.announcement.UpdateAnnouncementResponse;
import com.company.hrms.notification.api.response.announcement.WithdrawAnnouncementResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HR12 公告管理 Command Controller
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications/announcements")
@RequiredArgsConstructor
@Tag(name = "HR12 公告管理 - 命令操作", description = "公告發布與管理 API")
public class HR12AnnouncementCmdController extends CommandBaseController {

        /**
         * 發布公告
         */
        @PostMapping
        @PreAuthorize("hasAuthority('NOTIFICATION:ANNOUNCEMENT:CREATE')")
        @Operation(summary = "發布公告", description = "發布公告給目標對象（全員、部門、角色）")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "發布成功"),
                        @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "403", description = "權限不足"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<CreateAnnouncementResponse> createAnnouncement(
                        @Valid @RequestBody CreateAnnouncementRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
                log.info("[HR12AnnouncementCmd] 發布公告 - 標題: {}, 優先級: {}, 目標: {}",
                                request.getTitle(),
                                request.getPriority(),
                                request.getTargetAudience() != null ? request.getTargetAudience().getType() : "ALL");

                CreateAnnouncementResponse response = execCommand(request, currentUser);

                log.info("[HR12AnnouncementCmd] 公告發布成功 - 公告ID: {}, 收件人數: {}",
                                response.getAnnouncementId(),
                                response.getRecipientCount());

                return ResponseEntity.ok(response);
        }

        /**
         * 更新公告
         */
        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('NOTIFICATION:ANNOUNCEMENT:UPDATE')")
        @Operation(summary = "更新公告", description = "更新已發布的公告內容")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "更新成功"),
                        @ApiResponse(responseCode = "400", description = "公告已過期或撤銷"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "403", description = "權限不足"),
                        @ApiResponse(responseCode = "404", description = "公告不存在"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<UpdateAnnouncementResponse> updateAnnouncement(
                        @Parameter(description = "公告 ID", required = true) @PathVariable String id,
                        @Valid @RequestBody UpdateAnnouncementRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
                log.info("[HR12AnnouncementCmd] 更新公告 - 公告ID: {}", id);

                UpdateAnnouncementResponse response = execCommand(request, currentUser, id);

                log.info("[HR12AnnouncementCmd] 公告更新成功 - 公告ID: {}", response.getAnnouncementId());

                return ResponseEntity.ok(response);
        }

        /**
         * 撤銷公告
         */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('NOTIFICATION:ANNOUNCEMENT:DELETE')")
        @Operation(summary = "撤銷公告", description = "撤銷已發布的公告（軟刪除）")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "撤銷成功"),
                        @ApiResponse(responseCode = "400", description = "公告已撤銷"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "403", description = "權限不足"),
                        @ApiResponse(responseCode = "404", description = "公告不存在"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<WithdrawAnnouncementResponse> withdrawAnnouncement(
                        @Parameter(description = "公告 ID", required = true) @PathVariable String id,
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
                log.info("[HR12AnnouncementCmd] 撤銷公告 - 公告ID: {}", id);

                WithdrawAnnouncementResponse response = execCommand(id, currentUser);

                log.info("[HR12AnnouncementCmd] 公告已撤銷 - 公告ID: {}, 狀態: {}",
                                response.getAnnouncementId(), response.getStatus());

                return ResponseEntity.ok(response);
        }
}
