package com.company.hrms.reporting.infrastructure.persistence.po;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Dashboard 持久化物件
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Entity
@Table(name = "rpt_dashboard")
public class DashboardPO {

    @Id
    @Column(name = "dashboard_id", columnDefinition = "UUID")
    private UUID dashboardId;

    @Column(name = "dashboard_name", nullable = false, length = 100)
    private String dashboardName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "owner_id", nullable = false, columnDefinition = "UUID")
    private UUID ownerId;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @Column(name = "widgets_config", columnDefinition = "JSONB")
    private String widgetsConfig; // JSON 格式儲存 Widget 配置

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", columnDefinition = "UUID")
    private UUID createdBy;

    @Column(name = "updated_by", columnDefinition = "UUID")
    private UUID updatedBy;
}
