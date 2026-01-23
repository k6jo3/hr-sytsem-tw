package com.company.hrms.notification.api.request.template;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 建立通知範本請求
 *
 * @author Claude
 * @since 2025-01-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "建立通知範本請求")
public class CreateNotificationTemplateRequest {

    /**
     * 範本代碼（唯一）
     */
    @NotBlank(message = "範本代碼不可為空")
    @Pattern(regexp = "^[A-Z_]+$", message = "範本代碼必須為英文大寫字母與底線組合")
    @Size(max = 50, message = "範本代碼長度不可超過50字元")
    @Schema(description = "範本代碼（唯一，英文大寫_分隔）", example = "LEAVE_APPROVED")
    private String templateCode;

    /**
     * 範本名稱
     */
    @NotBlank(message = "範本名稱不可為空")
    @Size(min = 1, max = 100, message = "範本名稱長度必須在1-100字元之間")
    @Schema(description = "範本名稱", example = "請假申請核准通知")
    private String templateName;

    /**
     * 範本說明
     */
    @Size(max = 500, message = "範本說明長度不可超過500字元")
    @Schema(description = "範本說明", example = "員工請假申請核准後的通知範本")
    private String description;

    /**
     * Email 主旨範本
     */
    @Size(max = 200, message = "主旨長度不可超過200字元")
    @Schema(description = "Email 主旨範本", example = "【請假核准】您的請假申請已核准")
    private String subject;

    /**
     * 通知內容範本
     */
    @NotBlank(message = "範本內容不可為空")
    @Size(min = 1, max = 5000, message = "範本內容長度必須在1-5000字元之間")
    @Schema(description = "通知內容範本（支援變數 {{variableName}}）",
            example = "親愛的 {{employeeName}}：\n\n您的{{leaveType}}申請已核准。")
    private String body;

    /**
     * 通知類型
     */
    @NotBlank(message = "通知類型不可為空")
    @Schema(description = "通知類型", example = "APPROVAL_RESULT")
    private String notificationType;

    /**
     * 預設優先級
     */
    @Schema(description = "預設優先級", example = "NORMAL", defaultValue = "NORMAL")
    private String defaultPriority;

    /**
     * 預設發送渠道
     */
    @Schema(description = "預設發送渠道", example = "[\"IN_APP\", \"EMAIL\"]")
    private List<String> defaultChannels;

    /**
     * 範本變數定義
     * <p>
     * Key: 變數名稱
     * Value: 變數說明
     * </p>
     */
    @Schema(description = "範本變數定義（key=變數名, value=說明）",
            example = "{\"employeeName\": \"員工姓名\", \"leaveType\": \"請假類型\"}")
    private Map<String, String> variables;
}
