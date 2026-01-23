package com.company.hrms.notification.api.controller;

import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.template.SearchTemplateRequest;
import com.company.hrms.notification.api.response.template.TemplateDetailResponse;
import com.company.hrms.notification.api.response.template.TemplateListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * HR12 通知範本管理 Query Controller
 * <p>
 * 處理通知範本的查詢操作 (GET)
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@RestController
@RequestMapping("/api/v1/notifications/templates")
@Tag(name = "HR12-通知範本管理", description = "通知範本查詢操作")
public class HR12TemplateQryController extends QueryBaseController {

    /**
     * 查詢通知範本列表
     * <p>
     * 業務場景：管理員查看系統中的通知範本
     * </p>
     *
     * @param request     查詢請求
     * @param currentUser 當前使用者
     * @return 範本列表
     * @throws Exception 業務邏輯例外
     */
    @GetMapping
    @Operation(
            summary = "查詢通知範本列表",
            description = "支援關鍵字搜尋、類型過濾、狀態過濾及分頁",
            operationId = "getNotificationTemplateList"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<TemplateListResponse> getNotificationTemplateList(
            @ModelAttribute SearchTemplateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser
    ) throws Exception {
        TemplateListResponse response = getResponse(request, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * 查詢通知範本詳情
     * <p>
     * 業務場景：查看範本的完整內容與變數定義
     * </p>
     *
     * @param id          範本 ID
     * @param currentUser 當前使用者
     * @return 範本詳情
     * @throws Exception 業務邏輯例外
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "查詢通知範本詳情",
            description = "查詢範本的完整內容、變數定義及設定",
            operationId = "getNotificationTemplateDetail"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "404", description = "範本不存在")
    })
    public ResponseEntity<TemplateDetailResponse> getNotificationTemplateDetail(
            @PathVariable String id,
            @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser
    ) throws Exception {
        TemplateDetailResponse response = getResponse(null, currentUser, id);
        return ResponseEntity.ok(response);
    }
}
