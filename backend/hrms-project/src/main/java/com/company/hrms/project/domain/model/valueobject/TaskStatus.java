package com.company.hrms.project.domain.model.valueobject;

public enum TaskStatus {
    NOT_STARTED("未開始"),
    IN_PROGRESS("進行中"),
    COMPLETED("已完成"),
    BLOCKED("已擱置");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
