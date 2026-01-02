package com.company.hrms.project.domain.model.valueobject;

public enum ProjectStatus {
    PLANNING("規劃中"),
    IN_PROGRESS("進行中"),
    ON_HOLD("暫停"),
    COMPLETED("已結案"),
    CANCELLED("已取消");

    private final String displayName;

    ProjectStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
