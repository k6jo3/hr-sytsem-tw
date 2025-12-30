package com.company.hrms.attendance.domain.model.aggregate;

import java.time.LocalTime;

import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftType;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

@Getter
public class Shift extends AggregateRoot<ShiftId> {

    private String name;
    private ShiftType type;
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
    private int lateToleranceMinutes;
    private int earlyLeaveToleranceMinutes;

    public Shift(ShiftId id, String name, ShiftType type,
            LocalTime workStartTime, LocalTime workEndTime) {
        super(id);
        this.name = name;
        this.type = type;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.lateToleranceMinutes = 0;
        this.earlyLeaveToleranceMinutes = 0;
        validate();
    }

    public void setBreakTime(LocalTime start, LocalTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("Break start time cannot be after break end time");
        }
        this.breakStartTime = start;
        this.breakEndTime = end;
    }

    public void setTolerances(int lateToleranceMinutes, int earlyLeaveToleranceMinutes) {
        if (lateToleranceMinutes < 0 || earlyLeaveToleranceMinutes < 0) {
            throw new IllegalArgumentException("Tolerance minutes cannot be negative");
        }
        this.lateToleranceMinutes = lateToleranceMinutes;
        this.earlyLeaveToleranceMinutes = earlyLeaveToleranceMinutes;
    }

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Shift name cannot be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Shift type cannot be null");
        }
        if (workStartTime == null || workEndTime == null) {
            throw new IllegalArgumentException("Work start/end time cannot be null");
        }
        // Simple check, overnight shifts might make start > end, allowing for now if
        // logic handles it
        // But usually standard shift start < end. For robustness assuming same day for
        // now or logic handles cross-day.
    }
}
