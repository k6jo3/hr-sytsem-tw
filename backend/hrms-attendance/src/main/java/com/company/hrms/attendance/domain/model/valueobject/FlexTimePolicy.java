package com.company.hrms.attendance.domain.model.valueobject;

import java.time.LocalTime;

import lombok.Getter;

/**
 * 彈性工時政策
 *
 * <p>定義彈性上下班時間帶與核心工作時段：
 * <ul>
 *   <li>flexStartRange：彈性上班範圍（如 08:00-10:00）</li>
 *   <li>flexEndRange：彈性下班範圍（如 17:00-19:00）</li>
 *   <li>coreStartTime/coreEndTime：核心時段（如 10:00-17:00，此期間必須在崗）</li>
 *   <li>requiredHoursPerDay：每日應達工時（小時）</li>
 * </ul>
 */
@Getter
public class FlexTimePolicy {

    /** 彈性上班最早時間 */
    private final LocalTime flexStartEarliest;

    /** 彈性上班最晚時間 */
    private final LocalTime flexStartLatest;

    /** 彈性下班最早時間 */
    private final LocalTime flexEndEarliest;

    /** 彈性下班最晚時間 */
    private final LocalTime flexEndLatest;

    /** 核心時段開始 */
    private final LocalTime coreStartTime;

    /** 核心時段結束 */
    private final LocalTime coreEndTime;

    /** 每日應達工時（小時） */
    private final double requiredHoursPerDay;

    public FlexTimePolicy(
            LocalTime flexStartEarliest, LocalTime flexStartLatest,
            LocalTime flexEndEarliest, LocalTime flexEndLatest,
            LocalTime coreStartTime, LocalTime coreEndTime,
            double requiredHoursPerDay) {

        if (flexStartEarliest.isAfter(flexStartLatest)) {
            throw new IllegalArgumentException("彈性上班最早時間不可晚於最晚時間");
        }
        if (coreStartTime.isAfter(coreEndTime)) {
            throw new IllegalArgumentException("核心時段開始不可晚於結束");
        }
        if (requiredHoursPerDay <= 0 || requiredHoursPerDay > 24) {
            throw new IllegalArgumentException("每日應達工時必須介於 0~24 小時");
        }

        this.flexStartEarliest = flexStartEarliest;
        this.flexStartLatest = flexStartLatest;
        this.flexEndEarliest = flexEndEarliest;
        this.flexEndLatest = flexEndLatest;
        this.coreStartTime = coreStartTime;
        this.coreEndTime = coreEndTime;
        this.requiredHoursPerDay = requiredHoursPerDay;
    }

    /**
     * 判斷打卡時間是否在彈性上班範圍內
     */
    public boolean isWithinFlexStart(LocalTime checkInTime) {
        return !checkInTime.isBefore(flexStartEarliest) && !checkInTime.isAfter(flexStartLatest);
    }

    /**
     * 判斷是否遲到（超過彈性上班最晚時間）
     */
    public boolean isLateForFlexShift(LocalTime checkInTime) {
        return checkInTime.isAfter(flexStartLatest);
    }

    /**
     * 判斷是否缺席核心時段
     */
    public boolean missedCoreTime(LocalTime checkInTime, LocalTime checkOutTime) {
        return checkInTime.isAfter(coreStartTime) || checkOutTime.isBefore(coreEndTime);
    }
}
