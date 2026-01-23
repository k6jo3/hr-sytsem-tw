package com.company.hrms.notification.api.response.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 範本列表回應
 *
 * @author Claude
 * @since 2025-01-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "範本列表回應")
public class TemplateListResponse {

    /**
     * 範本項目列表
     */
    @Schema(description = "範本項目列表")
    private List<TemplateItem> items;

    /**
     * 分頁資訊
     */
    @Schema(description = "分頁資訊")
    private PaginationInfo pagination;

    /**
     * 範本項目
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "範本項目")
    public static class TemplateItem {

        /**
         * 範本 ID
         */
        @Schema(description = "範本 ID", example = "tpl-001")
        private String templateId;

        /**
         * 範本代碼
         */
        @Schema(description = "範本代碼", example = "LEAVE_APPROVED")
        private String templateCode;

        /**
         * 範本名稱
         */
        @Schema(description = "範本名稱", example = "請假申請核准通知")
        private String templateName;

        /**
         * Email 主旨
         */
        @Schema(description = "Email 主旨")
        private String subject;

        /**
         * 通知類型
         */
        @Schema(description = "通知類型")
        private String notificationType;

        /**
         * 預設發送渠道
         */
        @Schema(description = "預設發送渠道")
        private List<String> defaultChannels;

        /**
         * 是否啟用
         */
        @Schema(description = "是否啟用")
        private Boolean isActive;

        /**
         * 變數數量
         */
        @Schema(description = "變數數量", example = "6")
        private Integer variableCount;

        /**
         * 建立時間
         */
        @Schema(description = "建立時間")
        private LocalDateTime createdAt;

        /**
         * 更新時間
         */
        @Schema(description = "更新時間")
        private LocalDateTime updatedAt;
    }

    /**
     * 分頁資訊
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分頁資訊")
    public static class PaginationInfo {

        /**
         * 當前頁碼
         */
        @Schema(description = "當前頁碼", example = "1")
        private Integer currentPage;

        /**
         * 每頁筆數
         */
        @Schema(description = "每頁筆數", example = "20")
        private Integer pageSize;

        /**
         * 總筆數
         */
        @Schema(description = "總筆數", example = "15")
        private Long totalItems;

        /**
         * 總頁數
         */
        @Schema(description = "總頁數", example = "1")
        private Integer totalPages;
    }
}
