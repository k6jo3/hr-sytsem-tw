package com.company.hrms.attendance.domain.model.aggregate;

import java.time.LocalTime;

import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftType;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

/**
 * 班別 (Shift) 聚合根
 */
@Getter
public class Shift extends AggregateRoot<ShiftId> {

    private String organizationId;
    private String code; // 新增班別編碼
    private String name;
    private ShiftType type;
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
    private int lateToleranceMinutes;
    private int earlyLeaveToleranceMinutes;
    private boolean isActive;
    private boolean isDeleted;

    public Shift(ShiftId id, String organizationId, String code, String name, ShiftType type,
            LocalTime workStartTime, LocalTime workEndTime) {
        super(id);
        this.organizationId = organizationId;
        this.code = code;
        this.name = name;
        this.type = type;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.lateToleranceMinutes = 0;
        this.earlyLeaveToleranceMinutes = 0;
        this.isActive = true;
        this.isDeleted = false;
        validate();
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
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
        if (organizationId == null || organizationId.isBlank()) {
            throw new IllegalArgumentException("Organization ID cannot be empty");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Shift code cannot be empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Shift name cannot be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Shift type cannot be null");
        }
        if (workStartTime == null || workEndTime == null) {
            throw new IllegalArgumentException("Work start/end time cannot be null");
        }
    }

    private Shift(ShiftId id, String organizationId, String code, String name, ShiftType type,
            LocalTime workStartTime, LocalTime workEndTime,
            LocalTime breakStartTime, LocalTime breakEndTime,
            int lateToleranceMinutes, int earlyLeaveToleranceMinutes,
            boolean isActive, boolean isDeleted) {
        super(id);
        this.organizationId = organizationId;
        this.code = code;
        this.name = name;
        this.type = type;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
        this.lateToleranceMinutes = lateToleranceMinutes;
        this.earlyLeaveToleranceMinutes = earlyLeaveToleranceMinutes;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
    }

    public static Shift reconstitute(ShiftId id, String organizationId, String code, String name, ShiftType type,
            LocalTime workStartTime, LocalTime workEndTime,
            LocalTime breakStartTime, LocalTime breakEndTime,
            int lateToleranceMinutes, int earlyLeaveToleranceMinutes,
            boolean isActive, boolean isDeleted) {
        return new Shift(id, organizationId, code, name, type, workStartTime, workEndTime,
                breakStartTime, breakEndTime, lateToleranceMinutes, earlyLeaveToleranceMinutes,
                isActive, isDeleted);
    }
}
