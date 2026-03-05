package com.company.hrms.payroll.domain.model.valueobject;

/**
 * 薪資單狀態列舉
 * 追蹤個別薪資單的處理狀態
 */
public enum PayslipStatus {

    /**
     * 草稿
     * 薪資單已產生但尚未定案
     */
    DRAFT,

    /**
     * 已定案
     * 薪資單已確認，不可再修改
     */
    FINALIZED,

    /**
     * 已發送
     * 薪資單已發送給員工
     */
    SENT,

    /**
     * 已作廢
     * 薪資單已被作廢（產生沖正單）
     */
    VOIDED,

    /**
     * 已沖正
     * 作為沖正記錄（負數薪資單）
     */
    REVERSED
}
