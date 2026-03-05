package com.company.hrms.attendance.domain.model.valueobject;

/**
 * 特休年度制度
 */
public enum AnnualLeaveSystem {

    /**
     * 歷年制（1/1～12/31）
     * 依曆年計算，入職首年按比例折算
     */
    CALENDAR_YEAR,

    /**
     * 週年制（到職日起算）
     * 依員工到職日起算週年
     */
    ANNIVERSARY
}
