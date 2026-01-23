package com.company.hrms.notification.api.controller;

import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.template.CreateNotificationTemplateRequest;
import com.company.hrms.notification.api.request.template.UpdateNotificationTemplateRequest;
import com.company.hrms.notification.api.response.template.CreateTemplateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * HR12 通知範本管理 Command Controller
 * <p>
 * 處理通知範本的寫入操作 (POST, PUT, DELETE)
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@RestController
@RequestMapping("/api/v1/notifications/templates")
@Tag(name = "HR12-通知範本管理", description = "通知範本 CRUD 操作")
public class HR12TemplateCmdController extends CommandBaseController {

    /**
     * 建立通知範本
     * <p>
     * 業務場景：系統管理員建立新的通知範本
     * </p>
     *
     * @param request     建立請求
     * @param currentUser 當前使用者
     * @return 建立結果
     * @throws Exception 業務邏輯例外
     */
    @PostMapping
    @Operation(
            summary = "建立通知範本",
            description = "系統管理員建立新的通知範本，支援變數替換（使用 {{variableName}} 語法）",
            operationId = "createNotificationTemplate"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "建立成功"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "權限不足"),
            @ApiResponse(responseCode = "409", description = "範本代碼已存在")
    })
    public ResponseEntity<CreateTemplateResponse> createNotificationTemplate(
            @Valid @RequestBody CreateNotificationTemplateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser
    ) throws Exception {
        CreateTemplateResponse response = execCommand(request, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新通知範本
     * <p>
     * 業務場景：系統管理員更新現有範本的內容
     * </p>
     *
     * @param id          範本 ID
     * @param request     更新請求
     * @param currentUser 當前使用者
     * @return 更新結果
     * @throws Exception 業務邏輯例外
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "更新通知範本",
            description = "更新現有通知範本的內容、渠道或狀態",
            operationId = "updateNotificationTemplate"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
            @ApiResponse(responseCode = "404", description = "範本不存在")
    })
    public ResponseEntity<CreateTemplateResponse> updateNotificationTemplate(
            @PathVariable String id,
            @Valid @RequestBody UpdateNotificationTemplateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser
    ) throws Exception {
        CreateTemplateResponse response = execCommand(request, currentUser, id);
        return ResponseEntity.ok(response);
    }

    /**
     * 刪除通知範本
     * <p>
     * 業務場景：刪除不再使用的通知範本
     * 注意：已被使用的範本不可刪除，建議停用
     * </p>
     *
     * @param id          範本 ID
     * @param currentUser 當前使用者
     * @return 刪除結果
     * @throws Exception 業務邏輯例外
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "刪除通知範本",
            description = "刪除不再使用的通知範本（軟刪除）。注意：已被使用的範本無法刪除",
            operationId = "deleteNotificationTemplate"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "刪除成功"),
            @ApiResponse(responseCode = "400", description = "範本已被使用，無法刪除"),
            @ApiResponse(responseCode = "404", description = "範本不存在")
    })
    public ResponseEntity<Void> deleteNotificationTemplate(
            @PathVariable String id,
            @Parameter(hidden = true) @AuthenticationPrincipal JWTModel currentUser
    ) throws Exception {
        execCommand(null, currentUser, id);
        return ResponseEntity.ok().build();
    }
}
