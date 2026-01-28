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
import com.company.hrms.notification.api.response.announcement.AnnouncementDetailResponse;
import com.company.hrms.notification.api.response.announcement.AnnouncementListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HR12 公告查詢 Controller
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications/announcements")
@RequiredArgsConstructor
@Tag(name = "HR12 公告管理 - 查詢操作", description = "公告查詢相關 API")
public class HR12AnnouncementQryController extends QueryBaseController {

        /**
         * 查詢公告列表
         */
        @GetMapping
        @Operation(summary = "查詢公告列表", description = "查詢當前使用者可見的公告列表")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查詢成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<AnnouncementListResponse> getAnnouncementList(
                        @Parameter(description = "是否包含已過期公告") @RequestParam(defaultValue = "false") Boolean includeExpired,
                        @Parameter(description = "頁碼") @RequestParam(defaultValue = "1") Integer page,
                        @Parameter(description = "每頁筆數") @RequestParam(defaultValue = "20") Integer pageSize,
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
                log.info("[HR12AnnouncementQry] 查詢公告列表 - 使用者: {}, 包含過期: {}",
                                currentUser.getEmployeeNumber(), includeExpired);

                AnnouncementListResponse response = getResponse(null, currentUser,
                                String.valueOf(includeExpired), String.valueOf(page), String.valueOf(pageSize));

                log.info("[HR12AnnouncementQry] 公告列表查詢完成 - 總筆數: {}",
                                response.getPagination() != null ? response.getPagination().getTotalItems() : 0);

                return ResponseEntity.ok(response);
        }

        /**
         * 查詢公告詳情
         */
        @GetMapping("/{id}")
        @Operation(summary = "查詢公告詳情", description = "查詢指定公告的完整內容")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查詢成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "403", description = "無權查看此公告"),
                        @ApiResponse(responseCode = "404", description = "公告不存在"),
                        @ApiResponse(responseCode = "500", description = "系統錯誤")
        })
        public ResponseEntity<AnnouncementDetailResponse> getAnnouncementDetail(
                        @Parameter(description = "公告 ID", required = true) @PathVariable String id,
                        @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser) throws Exception {
                log.info("[HR12AnnouncementQry] 查詢公告詳情 - 公告ID: {}, 使用者: {}",
                                id, currentUser.getEmployeeNumber());

                AnnouncementDetailResponse response = getResponse(null, currentUser, id);

                log.info("[HR12AnnouncementQry] 公告詳情查詢完成 - 公告ID: {}", id);

                return ResponseEntity.ok(response);
        }
}
