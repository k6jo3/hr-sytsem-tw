package com.company.hrms.attendance.domain.model.valueobject;

/**
 * 異常類型
 */
public enum AnomalyType {
    NORMAL, // 正常
    LATE, // 遲到
    EARLY_LEAVE, // 早退
    ABSENT, // 缺勤
    MISSING_CHECK_IN, // 缺上班卡
    MISSING_CHECK_OUT // 缺下班卡
}
