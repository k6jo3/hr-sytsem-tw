package com.company.hrms.organization.domain.model.valueobject;

/**
 * 學歷學位列舉
 */
public enum Degree {
    /**
     * 高中
     */
    HIGH_SCHOOL("高中", 1),

    /**
     * 專科
     */
    ASSOCIATE("專科", 2),

    /**
     * 學士
     */
    BACHELOR("學士", 3),

    /**
     * 碩士
     */
    MASTER("碩士", 4),

    /**
     * 博士
     */
    DOCTORATE("博士", 5);

    private final String displayName;
    private final int level;

    Degree(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    /**
     * 比較學歷高低
     * @param other 另一個學歷
     * @return 是否高於另一個學歷
     */
    public boolean isHigherThan(Degree other) {
        return this.level > other.level;
    }
}
