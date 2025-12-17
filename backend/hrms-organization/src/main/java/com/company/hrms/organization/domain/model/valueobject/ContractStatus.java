package com.company.hrms.organization.domain.model.valueobject;

/**
 * 合約狀態列舉
 */
public enum ContractStatus {
    /**
     * 生效中
     */
    ACTIVE("生效中"),

    /**
     * 已到期
     */
    EXPIRED("已到期"),

    /**
     * 已終止
     */
    TERMINATED("已終止");

    private final String displayName;

    ContractStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 是否為生效中
     * @return 是否生效
     */
    public boolean isActive() {
        return this == ACTIVE;
    }
}
