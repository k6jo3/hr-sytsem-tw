package com.company.hrms.reporting.infrastructure.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 報表匯出記錄實體
 */
@Entity
@Table(name = "report_exports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportExportEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "report_type")
    private String reportType;

    @Column(name = "format")
    private String format;

    @Column(name = "status")
    private String status;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "requester_id")
    private String requesterId;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "filters_json")
    private String filtersJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "format_type")
    private String formatType; // 政府報表類型 (LABOR_INSURANCE, etc.)

    @Column(name = "period")
    private String period; // 期間 (YYYY-MM)

    @Column(name = "file_path")
    private String filePath; // 檔案儲存路徑
}
