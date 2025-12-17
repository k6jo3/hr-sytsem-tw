package com.company.hrms.organization.domain.model.valueobject;

/**
 * 組織類型列舉
 */
public enum OrganizationType {
    /**
     * 母公司
     */
    PARENT("母公司"),

    /**
     * 子公司
     */
    SUBSIDIARY("子公司");

    private final String displayName;

    OrganizationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
