package com.company.hrms.reporting.infrastructure.persistence.mapper;

import com.company.hrms.reporting.domain.model.dashboard.Dashboard;
import com.company.hrms.reporting.domain.model.dashboard.DashboardId;
import com.company.hrms.reporting.infrastructure.persistence.po.DashboardPO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Dashboard 領域模型與持久化物件轉換器
 * 
 * @author SA Team
 * @since 2026-01-29
 */
public class DashboardMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * PO 轉 Domain
     */
    public static Dashboard toDomain(DashboardPO po) {
        if (po == null) {
            return null;
        }

        Dashboard dashboard = Dashboard.create(
                po.getDashboardName(),
                po.getDescription(),
                po.getOwnerId(),
                po.getTenantId(),
                po.getIsPublic());

        // 使用反射設定 ID（因為 create 方法會生成新 ID）
        try {
            var idField = Dashboard.class.getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(dashboard, DashboardId.of(po.getDashboardId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set dashboard ID", e);
        }

        if (Boolean.TRUE.equals(po.getIsDefault())) {
            dashboard.setAsDefault();
        }

        if (po.getWidgetsConfig() != null) {
            try {
                java.util.List<com.company.hrms.reporting.domain.model.dashboard.DashboardWidget> widgets = objectMapper
                        .readValue(po.getWidgetsConfig(), new com.fasterxml.jackson.core.type.TypeReference<>() {
                        });
                dashboard.updateWidgets(widgets);
            } catch (JsonProcessingException e) {
                // Log error or ignore if config is invalid
            }
        }

        return dashboard;
    }

    /**
     * Domain 轉 PO
     */
    public static DashboardPO toPO(Dashboard dashboard) {
        if (dashboard == null) {
            return null;
        }

        DashboardPO po = new DashboardPO();
        po.setDashboardId(dashboard.getId().getValue());
        po.setDashboardName(dashboard.getDashboardName());
        po.setDescription(dashboard.getDescription());
        po.setOwnerId(dashboard.getOwnerId());
        po.setTenantId(dashboard.getTenantId());
        po.setIsPublic(dashboard.isPublic());
        po.setIsDefault(dashboard.isDefault());
        po.setCreatedAt(dashboard.getCreatedAt());
        po.setUpdatedAt(dashboard.getUpdatedAt());

        // 將 widgets 序列化為 JSON
        try {
            po.setWidgetsConfig(objectMapper.writeValueAsString(dashboard.getWidgets()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize widgets", e);
        }

        return po;
    }
}
