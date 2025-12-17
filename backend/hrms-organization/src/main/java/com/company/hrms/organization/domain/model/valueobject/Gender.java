package com.company.hrms.organization.domain.model.valueobject;

/**
 * 性別列舉
 */
public enum Gender {
    /**
     * 男性
     */
    MALE("男"),

    /**
     * 女性
     */
    FEMALE("女"),

    /**
     * 其他
     */
    OTHER("其他");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
