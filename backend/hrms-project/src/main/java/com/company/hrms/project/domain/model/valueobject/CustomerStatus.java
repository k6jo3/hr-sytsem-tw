package com.company.hrms.project.domain.model.valueobject;

public enum CustomerStatus {
    ACTIVE("啟用"),
    INACTIVE("停用");

    private final String displayName;

    CustomerStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
