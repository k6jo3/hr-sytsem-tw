package com.company.hrms.payroll.domain.model.valueobject;

/**
 * 薪資制度列舉
 * 定義員工的薪資計算方式
 */
public enum PayrollSystem {

    /**
     * 時薪制
     * 以工時計算薪資
     */
    HOURLY,

    /**
     * 日薪制
     * 以出勤天數計算薪資
     */
    DAILY,

    /**
     * 月薪制
     * 以固定月薪計算薪資
     */
    MONTHLY
}
