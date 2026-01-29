package com.company.hrms.notification.api.response.announcement;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公告列表回應
 *
 * @author Claude
 * @since 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "公告列表回應")
public class AnnouncementListResponse {

    /**
     * 公告列表
     */
    @Schema(description = "公告列表")
    private List<AnnouncementItem> items;

    /**
     * 分頁資訊
     */
    @Schema(description = "分頁資訊")
    private Pagination pagination;

    /**
     * 公告項目
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "公告項目")
    public static class AnnouncementItem {

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
         * 公告摘要（內容截斷）
         */
        @Schema(description = "公告摘要", example = "各位同仁：2026年春節假期為...")
        private String summary;

        /**
         * 公告內容（完整內容）
         */
        @Schema(description = "公告內容", example = "各位同仁：\n\n2026年春節假期為...")
        private String content;

        /**
         * 優先級
         */
        @Schema(description = "優先級", example = "HIGH")
        private String priority;

        /**
         * 狀態
         */
        @Schema(description = "狀態", example = "PUBLISHED")
        private String status;

        /**
         * 是否置頂
         */
        @Schema(description = "是否置頂", example = "false")
        private Boolean isPinned;

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

    /**
     * 分頁資訊
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分頁資訊")
    public static class Pagination {

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
        @Schema(description = "總筆數", example = "5")
        private Long totalItems;

        /**
         * 總頁數
         */
        @Schema(description = "總頁數", example = "1")
        private Integer totalPages;
    }
}
