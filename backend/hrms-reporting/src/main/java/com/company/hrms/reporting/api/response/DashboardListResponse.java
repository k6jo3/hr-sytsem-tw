package com.company.hrms.reporting.api.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 儀表板列表回應
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "儀表板列表回應")
public class DashboardListResponse {

    @Schema(description = "儀表板列表")
    private List<DashboardSummary> content;

    @Schema(description = "總筆數")
    private Long totalElements;

    @Schema(description = "總頁數")
    private Integer totalPages;

    @Schema(description = "當前頁碼")
    private Integer currentPage;

    /**
     * 儀表板摘要
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "儀表板摘要")
    public static class DashboardSummary {

        @Schema(description = "儀表板 ID")
        private UUID dashboardId;

        @Schema(description = "儀表板名稱")
        private String dashboardName;

        @Schema(description = "描述")
        private String description;

        @Schema(description = "是否公開")
        private Boolean isPublic;

        @Schema(description = "是否為預設")
        private Boolean isDefault;

        @Schema(description = "Widget 數量")
        private Integer widgetCount;

        @Schema(description = "建立時間")
        private LocalDateTime createdAt;

        @Schema(description = "更新時間")
        private LocalDateTime updatedAt;
    }
}
