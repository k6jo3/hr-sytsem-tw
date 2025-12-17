package com.company.hrms.organization.domain.model.valueobject;

/**
 * 合約類型列舉
 */
public enum ContractType {
    /**
     * 不定期契約
     */
    INDEFINITE("不定期契約"),

    /**
     * 定期契約
     */
    FIXED_TERM("定期契約");

    private final String displayName;

    ContractType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 是否為不定期契約
     * @return 是否為不定期
     */
    public boolean isIndefinite() {
        return this == INDEFINITE;
    }
}
