package com.company.hrms.attendance.domain.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.company.hrms.attendance.domain.model.aggregate.RotationPattern;
import com.company.hrms.attendance.domain.model.aggregate.RotationPattern.RotationDay;
import com.company.hrms.attendance.domain.model.aggregate.ShiftSchedule;
import com.company.hrms.attendance.domain.model.valueobject.ScheduleId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;

/**
 * 排班 Domain Service
 * 負責根據輪班模式自動產生排班表
 */
public class ShiftSchedulingDomainService {

    /**
     * 根據輪班模式，為指定員工產生日期範圍的排班
     *
     * @param employeeId    員工 ID
     * @param pattern       輪班模式
     * @param startDate     排班起始日
     * @param endDate       排班結束日
     * @param rotationStart 此員工輪班循環的起算日（決定從循環第幾天開始）
     * @return 產生的排班列表（不含休息日）
     */
    public List<ShiftSchedule> generateSchedules(String employeeId, RotationPattern pattern,
            LocalDate startDate, LocalDate endDate, LocalDate rotationStart) {

        if (pattern.getRotationDays().isEmpty()) {
            throw new IllegalStateException("輪班模式尚未設定天序");
        }

        List<ShiftSchedule> schedules = new ArrayList<>();
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        for (int i = 0; i < totalDays; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            int dayOffset = (int) ChronoUnit.DAYS.between(rotationStart, currentDate);
            // 處理 rotationStart 在 startDate 之後的情形
            dayOffset = ((dayOffset % pattern.getCycleDays()) + pattern.getCycleDays()) % pattern.getCycleDays();

            RotationDay rotationDay = pattern.getDayForIndex(dayOffset);

            if (!rotationDay.isRestDay()) {
                ShiftSchedule schedule = new ShiftSchedule(
                        ScheduleId.generate(),
                        employeeId,
                        rotationDay.getShiftId(),
                        currentDate);
                schedule.setRotationPatternId(pattern.getId().getValue());
                schedules.add(schedule);
            }
        }

        return schedules;
    }

    /**
     * 執行換班：交換兩筆排班的班別
     *
     * @param scheduleA 申請人的排班
     * @param scheduleB 對方的排班
     */
    public void executeSwap(ShiftSchedule scheduleA, ShiftSchedule scheduleB) {
        ShiftId shiftA = scheduleA.getShiftId();
        ShiftId shiftB = scheduleB.getShiftId();

        scheduleA.changeShift(shiftB);
        scheduleB.changeShift(shiftA);
    }
}
