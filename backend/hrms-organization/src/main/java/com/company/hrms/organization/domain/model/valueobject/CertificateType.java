package com.company.hrms.organization.domain.model.valueobject;

/**
 * 證明文件類型列舉
 */
public enum CertificateType {
    /**
     * 在職證明
     */
    EMPLOYMENT_CERTIFICATE("在職證明"),

    /**
     * 薪資證明
     */
    SALARY_CERTIFICATE("薪資證明"),

    /**
     * 扣繳憑單
     */
    TAX_WITHHOLDING("扣繳憑單");

    private final String displayName;

    CertificateType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
