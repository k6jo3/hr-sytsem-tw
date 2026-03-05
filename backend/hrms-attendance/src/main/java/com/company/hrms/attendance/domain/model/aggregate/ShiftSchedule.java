package com.company.hrms.attendance.domain.model.aggregate;

import java.time.LocalDate;

import com.company.hrms.attendance.domain.model.valueobject.ScheduleId;
import com.company.hrms.attendance.domain.model.valueobject.ScheduleStatus;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

/**
 * 排班表聚合根
 * 記錄單一員工在某日的班別指派
 */
@Getter
public class ShiftSchedule extends AggregateRoot<ScheduleId> {

    private String employeeId;
    private ShiftId shiftId;
    private LocalDate scheduleDate;
    private ScheduleStatus status;
    private String rotationPatternId; // 關聯的輪班模式 ID（nullable，手動排班時為 null）
    private String note;
    private boolean isDeleted;

    /**
     * 新建排班
     */
    public ShiftSchedule(ScheduleId id, String employeeId, ShiftId shiftId, LocalDate scheduleDate) {
        super(id);
        validate(employeeId, shiftId, scheduleDate);
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.scheduleDate = scheduleDate;
        this.status = ScheduleStatus.DRAFT;
        this.isDeleted = false;
    }

    /**
     * 變更班別
     */
    public void changeShift(ShiftId newShiftId) {
        if (this.status == ScheduleStatus.LOCKED) {
            throw new IllegalStateException("已鎖定的排班不可變更");
        }
        if (newShiftId == null) {
            throw new IllegalArgumentException("班別 ID 不可為空");
        }
        this.shiftId = newShiftId;
    }

    /**
     * 發佈排班
     */
    public void publish() {
        if (this.status != ScheduleStatus.DRAFT) {
            throw new IllegalStateException("僅草稿狀態可發佈");
        }
        this.status = ScheduleStatus.PUBLISHED;
    }

    /**
     * 鎖定排班（月結後）
     */
    public void lock() {
        if (this.status != ScheduleStatus.PUBLISHED) {
            throw new IllegalStateException("僅已發佈狀態可鎖定");
        }
        this.status = ScheduleStatus.LOCKED;
    }

    /**
     * 退回草稿（尚未鎖定時）
     */
    public void revertToDraft() {
        if (this.status == ScheduleStatus.LOCKED) {
            throw new IllegalStateException("已鎖定的排班不可退回");
        }
        this.status = ScheduleStatus.DRAFT;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setRotationPatternId(String rotationPatternId) {
        this.rotationPatternId = rotationPatternId;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    private void validate(String employeeId, ShiftId shiftId, LocalDate scheduleDate) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("員工 ID 不可為空");
        }
        if (shiftId == null) {
            throw new IllegalArgumentException("班別 ID 不可為空");
        }
        if (scheduleDate == null) {
            throw new IllegalArgumentException("排班日期不可為空");
        }
    }

    /**
     * 從持久層重建
     */
    private ShiftSchedule(ScheduleId id, String employeeId, ShiftId shiftId, LocalDate scheduleDate,
            ScheduleStatus status, String rotationPatternId, String note, boolean isDeleted) {
        super(id);
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.scheduleDate = scheduleDate;
        this.status = status;
        this.rotationPatternId = rotationPatternId;
        this.note = note;
        this.isDeleted = isDeleted;
    }

    public static ShiftSchedule reconstitute(ScheduleId id, String employeeId, ShiftId shiftId,
            LocalDate scheduleDate, ScheduleStatus status, String rotationPatternId,
            String note, boolean isDeleted) {
        return new ShiftSchedule(id, employeeId, shiftId, scheduleDate, status,
                rotationPatternId, note, isDeleted);
    }
}
