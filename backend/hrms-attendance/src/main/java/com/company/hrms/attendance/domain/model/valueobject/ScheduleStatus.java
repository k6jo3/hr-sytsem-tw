package com.company.hrms.attendance.domain.model.valueobject;

/**
 * 排班狀態
 */
public enum ScheduleStatus {
    DRAFT,      // 草稿（排班中）
    PUBLISHED,  // 已發佈
    LOCKED      // 已鎖定（結算後不可修改）
}
