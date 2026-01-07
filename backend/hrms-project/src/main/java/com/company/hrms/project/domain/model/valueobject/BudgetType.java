package com.company.hrms.project.domain.model.valueobject;

public enum BudgetType {
    FIXED_PRICE("固定價格"),
    TIME_AND_MATERIAL("實報實銷");

    private final String displayName;

    BudgetType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
