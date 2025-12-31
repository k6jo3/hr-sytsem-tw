package com.company.hrms.insurance.domain.model.valueobject;

/**
 * 眷屬類型
 */
public enum DependentType {

    SPOUSE("配偶"),
    CHILD("子女"),
    PARENT("父母"),
    SIBLING("兄弟姐妹"),
    OTHER("其他");

    private final String displayName;

    DependentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
