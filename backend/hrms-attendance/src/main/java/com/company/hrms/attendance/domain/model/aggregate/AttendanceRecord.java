package com.company.hrms.attendance.domain.model.aggregate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.company.hrms.attendance.domain.model.valueobject.AnomalyType;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

@Getter
public class AttendanceRecord extends AggregateRoot<RecordId> {

    private String employeeId;
    private LocalDate date;
    private String shiftId; // 新增班別 ID
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private boolean isLate;
    private int lateMinutes;
    private boolean isEarlyLeave;
    private int earlyLeaveMinutes;

    private AnomalyType anomalyType;
    private boolean isCorrected;

    public AttendanceRecord(RecordId id, String employeeId, LocalDate date) {
        super(id);
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("EmployeeID cannot be null or blank");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        this.employeeId = employeeId;
        this.date = date;
        this.anomalyType = AnomalyType.NORMAL; // Default
        this.isLate = false;
        this.isEarlyLeave = false;
        this.isCorrected = false;
    }

    public void checkIn(LocalDateTime time, Shift shift) {
        if (this.checkInTime != null) {
            throw new IllegalStateException("Already checked in");
        }
        this.checkInTime = time;
        this.shiftId = shift.getId().getValue(); // 綁定班別

        // Calculate Late
        LocalDateTime expectedStart = LocalDateTime.of(date, shift.getWorkStartTime());
        int tolerance = shift.getLateToleranceMinutes();

        if (time.isAfter(expectedStart.plusMinutes(tolerance))) {
            this.isLate = true;
            this.lateMinutes = (int) Duration.between(expectedStart, time).toMinutes();
            this.anomalyType = AnomalyType.LATE;
        }
    }

    public void checkOut(LocalDateTime time, Shift shift) {
        if (this.checkInTime == null) {
            throw new IllegalStateException("Must check in first");
        }
        this.checkOutTime = time;

        // Calculate Early Leave
        LocalDateTime expectedEnd = LocalDateTime.of(date, shift.getWorkEndTime());
        int tolerance = shift.getEarlyLeaveToleranceMinutes();

        if (time.isBefore(expectedEnd.minusMinutes(tolerance))) {
            this.isEarlyLeave = true;
            this.earlyLeaveMinutes = (int) Duration.between(time, expectedEnd).toMinutes();

            if (this.anomalyType == AnomalyType.NORMAL) {
                this.anomalyType = AnomalyType.EARLY_LEAVE;
            }
        }
    }

    public void correctRecord(LocalDateTime checkIn, LocalDateTime checkOut, Shift shift) {
        this.checkInTime = checkIn;
        this.checkOutTime = checkOut;
        this.shiftId = shift.getId().getValue();
        this.isCorrected = true;
        this.anomalyType = AnomalyType.NORMAL; // Reset anomaly on correction
        this.isLate = false;
        this.isEarlyLeave = false;
        this.lateMinutes = 0;
        this.earlyLeaveMinutes = 0;
    }

    private AttendanceRecord(RecordId id, String employeeId, LocalDate date, String shiftId,
            LocalDateTime checkInTime, LocalDateTime checkOutTime,
            boolean isLate, int lateMinutes, boolean isEarlyLeave, int earlyLeaveMinutes,
            AnomalyType anomalyType, boolean isCorrected) {
        super(id);
        this.employeeId = employeeId;
        this.date = date;
        this.shiftId = shiftId;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.isLate = isLate;
        this.lateMinutes = lateMinutes;
        this.isEarlyLeave = isEarlyLeave;
        this.earlyLeaveMinutes = earlyLeaveMinutes;
        this.anomalyType = anomalyType;
        this.isCorrected = isCorrected;
    }

    /**
     * 建立缺勤記錄（排程自動判定用）
     */
    public static AttendanceRecord createAbsentRecord(RecordId id, String employeeId, LocalDate date) {
        AttendanceRecord record = new AttendanceRecord(id, employeeId, date);
        record.anomalyType = AnomalyType.ABSENT;
        return record;
    }

    public static AttendanceRecord reconstitute(RecordId id, String employeeId, LocalDate date, String shiftId,
            LocalDateTime checkInTime, LocalDateTime checkOutTime,
            boolean isLate, int lateMinutes, boolean isEarlyLeave, int earlyLeaveMinutes,
            AnomalyType anomalyType, boolean isCorrected) {
        return new AttendanceRecord(id, employeeId, date, shiftId, checkInTime, checkOutTime,
                isLate, lateMinutes, isEarlyLeave, earlyLeaveMinutes, anomalyType, isCorrected);
    }
}
