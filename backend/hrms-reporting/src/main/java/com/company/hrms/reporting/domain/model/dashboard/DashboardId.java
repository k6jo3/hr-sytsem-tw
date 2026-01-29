package com.company.hrms.reporting.domain.model.dashboard;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

/**
 * Dashboard ID 值物件
 * 
 * @author SA Team
 * @since 2026-01-29
 */
public class DashboardId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    protected DashboardId(UUID value) {
        super(value);
    }

    public static DashboardId of(UUID value) {
        return new DashboardId(value);
    }

    public static DashboardId of(String value) {
        return new DashboardId(UUID.fromString(value));
    }

    public static DashboardId generate() {
        return new DashboardId(UUID.randomUUID());
    }
}
