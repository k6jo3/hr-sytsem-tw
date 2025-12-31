package com.company.hrms.insurance.domain.model.valueobject;

/**
 * 收入類型枚舉 (二代健保補充保費適用)
 */
public enum IncomeType {
    /**
     * 獎金
     */
    BONUS("獎金"),

    /**
     * 兼職所得
     */
    PART_TIME_INCOME("兼職所得"),

    /**
     * 執行業務所得
     */
    PROFESSIONAL_FEE("執行業務所得");

    private final String displayName;

    IncomeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
