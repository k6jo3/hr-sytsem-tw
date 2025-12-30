package com.company.hrms.payroll.domain.model.valueobject;

/**
 * 領薪週期列舉
 * 定義薪資發放的頻率
 */
public enum PayrollCycle {

    /**
     * 日薪
     * 每日發放薪資
     */
    DAILY,

    /**
     * 週薪
     * 每週發放薪資
     */
    WEEKLY,

    /**
     * 半月薪
     * 每半月發放薪資 (每月 1 日與 16 日)
     */
    BI_WEEKLY,

    /**
     * 月薪
     * 每月發放薪資
     */
    MONTHLY
}
