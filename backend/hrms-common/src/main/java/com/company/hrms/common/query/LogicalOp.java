package com.company.hrms.common.query;

/**
 * 邏輯運算子枚舉
 * 用於組合多個查詢條件
 */
public enum LogicalOp {
    /** 且 - 所有條件都必須符合 */
    AND,
    /** 或 - 任一條件符合即可 */
    OR
}
