package com.company.hrms.common.query;

/**
 * 查詢運算子枚舉
 * 定義所有支援的過濾條件運算類型
 */
public enum Operator {
    /** 等於 */
    EQ("="),
    /** 不等於 */
    NE("!="),
    /** 大於 */
    GT(">"),
    /** 小於 */
    LT("<"),
    /** 大於等於 */
    GTE(">="),
    /** 小於等於 */
    LTE("<="),
    /** 模糊查詢 */
    LIKE("LIKE"),
    /** 包含於 */
    IN("IN"),
    /** 為空 */
    IS_NULL("IS NULL"),
    /** 不為空 */
    IS_NOT_NULL("IS NOT NULL");

    private final String symbol;

    Operator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    /**
     * 從符號字串解析運算子
     */
    public static Operator fromSymbol(String symbol) {
        if (symbol == null) {
            return EQ;
        }
        String normalized = symbol.trim().toUpperCase();
        for (Operator op : values()) {
            if (op.symbol.equals(normalized) || op.name().equals(normalized)) {
                return op;
            }
        }
        // 處理常見變體
        switch (normalized) {
            case "==":
                return EQ;
            case "<>":
                return NE;
            case ">=":
                return GTE;
            case "<=":
                return LTE;
            default:
                return EQ;
        }
    }
}
