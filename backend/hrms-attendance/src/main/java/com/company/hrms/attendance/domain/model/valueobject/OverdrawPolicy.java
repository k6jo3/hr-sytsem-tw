package com.company.hrms.attendance.domain.model.valueobject;

/**
 * 超額請假政策
 *
 * <p>當員工特休餘額不足時的處理方式
 */
public enum OverdrawPolicy {

    /**
     * 拒絕申請（預設）
     */
    DENY,

    /**
     * 自動轉為事假
     * 超出部分以事假計，可能影響薪資
     */
    CONVERT_TO_PERSONAL,

    /**
     * 預支下年度額度
     * 從下一年度特休額度中預借，離職時需結算
     */
    ADVANCE
}
