package com.company.hrms.recruitment.domain.model.valueobject;

/**
 * 履歷來源
 */
public enum RecruitmentSource {
    /**
     * 人力銀行（104、1111等）
     */
    JOB_BANK("人力銀行"),

    /**
     * 員工推薦
     */
    REFERRAL("員工推薦"),

    /**
     * 公司官網
     */
    WEBSITE("公司官網"),

    /**
     * LinkedIn
     */
    LINKEDIN("LinkedIn"),

    /**
     * 獵頭
     */
    HEADHUNTER("獵頭"),

    /**
     * 其他
     */
    OTHER("其他");

    private final String displayName;

    RecruitmentSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
