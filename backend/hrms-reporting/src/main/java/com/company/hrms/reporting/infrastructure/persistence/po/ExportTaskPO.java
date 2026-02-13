package com.company.hrms.reporting.infrastructure.persistence.po;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 報表匯出任務持久化物件
 */
@Data
@Entity
@Table(name = "report_exports")
public class ExportTaskPO {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "report_type", nullable = false)
    private String reportType;

    @Column(name = "format", nullable = false)
    private String format;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "requester_id", nullable = false, columnDefinition = "UUID")
    private UUID requesterId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "filters_json", columnDefinition = "JSONB")
    private String filtersJson;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
