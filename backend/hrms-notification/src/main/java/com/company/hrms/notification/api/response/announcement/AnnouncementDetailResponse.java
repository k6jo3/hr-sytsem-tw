package com.company.hrms.notification.api.response.announcement;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公告詳情回應
 *
 * @author Claude
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "公告詳情回應")
public class AnnouncementDetailResponse {

    /**
     * 公告 ID
     */
    @Schema(description = "公告 ID", example = "ann-001")
    private String announcementId;

    /**
     * 公告標題
     */
    @Schema(description = "公告標題", example = "2026年春節放假公告")
    private String title;

    /**
     * 公告內容
     */
    @Schema(description = "公告內容", example = "各位同仁：\n\n2026年春節假期為1/28（六）至2/5（日），共9天。")
    private String content;

    /**
     * 優先級
     */
    @Schema(description = "優先級", example = "HIGH")
    private String priority;

    /**
     * 公告狀態
     */
    @Schema(description = "公告狀態", example = "PUBLISHED")
    private String status;

    /**
     * 目標對象
     */
    @Schema(description = "目標對象")
    private TargetAudience targetAudience;

    /**
     * 附件
     */
    @Schema(description = "附件")
    private List<Attachment> attachments;

    /**
     * 發布時間
     */
    @Schema(description = "發布時間", example = "2025-12-30T09:00:00")
    private LocalDateTime publishedAt;

    /**
     * 過期時間
     */
    @Schema(description = "過期時間", example = "2026-02-05T23:59:59")
    private LocalDateTime expireAt;

    /**
     * 是否已讀
     */
    @Schema(description = "是否已讀", example = "false")
    private Boolean isRead;

    /**
     * 發布者資訊
     */
    @Schema(description = "發布者資訊")
    private PublishedBy publishedBy;

    /**
     * 建立時間
     */
    @Schema(description = "建立時間", example = "2025-12-29T10:00:00")
    private LocalDateTime createdAt;

    /**
     * 目標對象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "目標對象")
    public static class TargetAudience {

        /**
         * 類型：ALL / DEPARTMENT / ROLE
         */
        @Schema(description = "類型", example = "ALL")
        private String type;

        /**
         * 部門 ID 列表
         */
        @Schema(description = "部門 ID 列表")
        private List<String> departmentIds;

        /**
         * 角色 ID 列表
         */
        @Schema(description = "角色 ID 列表")
        private List<String> roleIds;
    }

    /**
     * 附件
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "附件")
    public static class Attachment {

        /**
         * 附件 ID
         */
        @Schema(description = "附件 ID", example = "att-001")
        private String attachmentId;

        /**
         * 檔案名稱
         */
        @Schema(description = "檔案名稱", example = "放假通知.pdf")
        private String fileName;

        /**
         * 檔案 URL
         */
        @Schema(description = "檔案 URL", example = "/api/v1/documents/att-001")
        private String fileUrl;
    }

    /**
     * 發布者資訊
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "發布者資訊")
    public static class PublishedBy {

        /**
         * 員工 ID
         */
        @Schema(description = "員工 ID", example = "hr-001")
        private String employeeId;

        /**
         * 全名
         */
        @Schema(description = "全名", example = "人事部")
        private String fullName;
    }
}
