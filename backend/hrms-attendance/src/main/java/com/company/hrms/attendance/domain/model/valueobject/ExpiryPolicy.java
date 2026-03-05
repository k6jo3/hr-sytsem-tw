package com.company.hrms.attendance.domain.model.valueobject;

/**
 * 未休假處理政策
 *
 * <p>年度結算時，未使用特休的處理方式
 */
public enum ExpiryPolicy {

    /**
     * 結轉至下年度
     * 未休天數可帶入下一年度使用
     */
    CARRYOVER,

    /**
     * 折算未休假工資
     * 依勞基法規定折算為工資補償
     */
    PAY_COMPENSATION,

    /**
     * 過期作廢
     * 未使用天數直接歸零（需符合法令限制）
     */
    FORFEIT
}
