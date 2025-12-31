package com.company.hrms.common.query;

/**
 * 聚合函數枚舉
 * 定義所有支援的聚合運算類型
 */
public enum AggregateFunction {

    /** 計數 */
    COUNT,

    /** 計數 (去重) */
    COUNT_DISTINCT,

    /** 總和 */
    SUM,

    /** 平均值 */
    AVG,

    /** 最大值 */
    MAX,

    /** 最小值 */
    MIN
}
