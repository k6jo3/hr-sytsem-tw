package com.company.hrms.attendance.domain.model.valueobject;

/**
 * 補卡類型
 */
public enum CorrectionType {
    FORGET_CHECK_IN, // 忘記上班打卡
    FORGET_CHECK_OUT, // 忘記下班打卡
    DEVICE_FAILURE, // 設備故障
    OUT_FOR_BUSINESS, // 公出
    OTHER // 其他
}
