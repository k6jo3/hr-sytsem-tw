package com.company.hrms.attendance.domain.model.aggregate;

import java.time.LocalDate;

import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeType;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

@Getter
public class OvertimeApplication extends AggregateRoot<OvertimeId> {

    private String employeeId;
    private LocalDate date;
    private Double hours;
    private OvertimeType overtimeType;
    private String reason;
    private ApplicationStatus status;
    private String rejectionReason;

    public OvertimeApplication(OvertimeId id, String employeeId, LocalDate date,
            Double hours, OvertimeType overtimeType, String reason) {
        super(id);
        if (hours == null || hours <= 0) {
            throw new IllegalArgumentException("Hours must be greater than 0");
        }

        this.employeeId = employeeId;
        this.date = date;
        this.hours = hours;
        this.overtimeType = overtimeType;
        this.reason = reason;
        this.status = ApplicationStatus.PENDING;
    }

    public void approve() {
        if (this.status != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be approved");
        }
        this.status = ApplicationStatus.APPROVED;
    }

    public void reject(String reason) {
        if (this.status != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be rejected");
        }
        this.status = ApplicationStatus.REJECTED;
        this.rejectionReason = reason;
    }

    private OvertimeApplication(OvertimeId id, String employeeId, LocalDate date,
            Double hours, OvertimeType overtimeType, String reason,
            ApplicationStatus status, String rejectionReason) {
        super(id);
        this.employeeId = employeeId;
        this.date = date;
        this.hours = hours;
        this.overtimeType = overtimeType;
        this.reason = reason;
        this.status = status;
        this.rejectionReason = rejectionReason;
    }

    public static OvertimeApplication reconstitute(OvertimeId id, String employeeId, LocalDate date,
            Double hours, OvertimeType overtimeType, String reason,
            ApplicationStatus status, String rejectionReason) {
        return new OvertimeApplication(id, employeeId, date, hours, overtimeType, reason, status, rejectionReason);
    }
}
