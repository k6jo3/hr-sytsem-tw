package com.company.hrms.reporting.domain.model.dashboard;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

/**
 * Dashboard 聚合根
 * 
 * <p>
 * 儀表板是報表服務的核心聚合，負責管理 Widget 配置與布局
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Getter
public class Dashboard extends AggregateRoot<DashboardId> {

    private String dashboardName;
    private String description;
    private UUID ownerId;
    private String tenantId;
    private boolean isPublic;
    private boolean isDefault;
    private List<DashboardWidget> widgets;

    /**
     * 建構子（供 JPA 使用）
     */
    protected Dashboard() {
        super(null);
    }

    /**
     * 建構子
     */
    private Dashboard(DashboardId id) {
        super(id);
        this.widgets = new ArrayList<>();
    }

    /**
     * 建立新儀表板
     */
    public static Dashboard create(
            String dashboardName,
            String description,
            UUID ownerId,
            String tenantId,
            boolean isPublic) {

        Dashboard dashboard = new Dashboard(DashboardId.generate());
        dashboard.dashboardName = dashboardName;
        dashboard.description = description;
        dashboard.ownerId = ownerId;
        dashboard.tenantId = tenantId;
        dashboard.isPublic = isPublic;
        dashboard.isDefault = false;

        dashboard.validateDashboardName();

        return dashboard;
    }

    /**
     * 新增 Widget
     */
    public void addWidget(DashboardWidget widget) {
        validateWidgetPosition(widget);
        this.widgets.add(widget);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 移除 Widget
     */
    public void removeWidget(String widgetId) {
        this.widgets.removeIf(w -> w.getWidgetId().equals(widgetId));
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新 Widget 配置
     */
    public void updateWidgets(List<DashboardWidget> newWidgets) {
        // 先清空舊的 Widget 列表
        this.widgets.clear();

        // 驗證所有新 Widget 位置（此時列表已清空，不會與舊 Widget 衝突）
        for (DashboardWidget widget : newWidgets) {
            validateWidgetPosition(widget);
            this.widgets.add(widget);
        }

        touch();
    }

    /**
     * 設定為預設儀表板
     */
    public void setAsDefault() {
        this.isDefault = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 驗證儀表板名稱
     */
    private void validateDashboardName() {
        if (dashboardName == null || dashboardName.trim().isEmpty()) {
            throw new IllegalArgumentException("儀表板名稱不可為空");
        }

        if (dashboardName.length() > 100) {
            throw new IllegalArgumentException("儀表板名稱不可超過 100 字元");
        }
    }

    /**
     * 驗證 Widget 位置不重疊
     */
    private void validateWidgetPosition(DashboardWidget newWidget) {
        for (DashboardWidget existing : widgets) {
            if (existing.getWidgetId().equals(newWidget.getWidgetId())) {
                continue; // 跳過自己
            }

            if (isPositionOverlap(existing.getPosition(), newWidget.getPosition())) {
                throw new IllegalArgumentException(
                        String.format("Widget 位置重疊: %s 與 %s",
                                existing.getWidgetId(), newWidget.getWidgetId()));
            }
        }
    }

    /**
     * 檢查兩個位置是否重疊
     */
    private boolean isPositionOverlap(WidgetPosition pos1, WidgetPosition pos2) {
        // 檢查 X 軸是否重疊
        boolean xOverlap = pos1.getX() < pos2.getX() + pos2.getW() &&
                pos1.getX() + pos1.getW() > pos2.getX();

        // 檢查 Y 軸是否重疊
        boolean yOverlap = pos1.getY() < pos2.getY() + pos2.getH() &&
                pos1.getY() + pos1.getH() > pos2.getY();

        return xOverlap && yOverlap;
    }
}
