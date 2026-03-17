package com.company.hrms.organization.domain.model.valueobject;

/**
 * 離職類型列舉
 * 依據勞基法區分不同離職態樣，影響預告期、資遣費等計算
 */
public enum TerminationType {

    /**
     * 自願離職 - 勞基法第 15 條
     */
    VOLUNTARY_RESIGNATION("自願離職"),

    /**
     * 資遣 - 勞基法第 11 條
     */
    LAYOFF("資遣"),

    /**
     * 懲戒解雇 - 勞基法第 12 條
     */
    DISMISSAL("懲戒解雇"),

    /**
     * 合意終止
     */
    MUTUAL_AGREEMENT("合意終止"),

    /**
     * 定期契約到期
     */
    CONTRACT_EXPIRY("定期契約到期"),

    /**
     * 退休 - 勞基法第 53、54 條
     */
    RETIREMENT("退休");

    private final String displayName;

    TerminationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 是否為非自願離職（資遣、懲戒解雇）
     * 非自願離職通常需要資遣費
     *
     * @return 是否為非自願離職
     */
    public boolean isInvoluntary() {
        return this == LAYOFF || this == DISMISSAL;
    }

    /**
     * 是否需要預告期
     * 依勞基法，資遣（第 11 條）與自願離職（第 15 條）需預告期
     * 懲戒解雇（第 12 條）不需預告期
     *
     * @return 是否需要預告期
     */
    public boolean requiresNoticePeriod() {
        return this == VOLUNTARY_RESIGNATION || this == LAYOFF || this == RETIREMENT;
    }

    /**
     * 是否需要資遣費
     * 資遣（第 11 條）及部分合意終止需要資遣費
     * 懲戒解雇（第 12 條）、自願離職（第 15 條）不需要
     *
     * @return 是否需要資遣費
     */
    public boolean requiresSeverancePay() {
        return this == LAYOFF;
    }
}
