package com.company.hrms.payroll.domain.model.valueobject;

/**
 * 薪資調整類型
 */
public enum AdjustmentType {

    /**
     * 補發
     * 追溯補發薪資差額（如調薪追溯、漏發項目）
     */
    SUPPLEMENTARY,

    /**
     * 扣回
     * 追溯扣回多發金額（如溢發更正）
     */
    DEDUCTION,

    /**
     * 沖正
     * 原薪資單作廢，產生負數沖正記錄
     */
    REVERSAL
}
