package com.company.hrms.attendance.domain.model.aggregate;

import java.time.LocalDate;
import java.time.LocalTime;

import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionId;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionType;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

/**
 * 補卡申請聚合根
 */
@Getter
public class CorrectionApplication extends AggregateRoot<CorrectionId> {

    private String employeeId;
    private String attendanceRecordId;
    private LocalDate correctionDate;
    private CorrectionType correctionType;
    private LocalTime correctedCheckInTime;
    private LocalTime correctedCheckOutTime;
    private String reason;
    private ApplicationStatus status;
    private String rejectionReason;

    public CorrectionApplication(CorrectionId id, String employeeId, String attendanceRecordId,
            LocalDate correctionDate, CorrectionType correctionType,
            LocalTime correctedCheckInTime, LocalTime correctedCheckOutTime,
            String reason) {
        super(id);
        validateInput(employeeId, correctionDate, correctionType, reason);

        this.employeeId = employeeId;
        this.attendanceRecordId = attendanceRecordId;
        this.correctionDate = correctionDate;
        this.correctionType = correctionType;
        this.correctedCheckInTime = correctedCheckInTime;
        this.correctedCheckOutTime = correctedCheckOutTime;
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

    private void validateInput(String employeeId, LocalDate correctionDate,
            CorrectionType correctionType, String reason) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("Employee ID cannot be null or blank");
        }
        if (correctionDate == null) {
            throw new IllegalArgumentException("Correction date cannot be null");
        }
        if (correctionType == null) {
            throw new IllegalArgumentException("Correction type cannot be null");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason cannot be null or blank");
        }
    }

    // 用於從持久層重建聚合
    private CorrectionApplication(CorrectionId id, String employeeId, String attendanceRecordId,
            LocalDate correctionDate, CorrectionType correctionType,
            LocalTime correctedCheckInTime, LocalTime correctedCheckOutTime,
            String reason, ApplicationStatus status, String rejectionReason) {
        super(id);
        this.employeeId = employeeId;
        this.attendanceRecordId = attendanceRecordId;
        this.correctionDate = correctionDate;
        this.correctionType = correctionType;
        this.correctedCheckInTime = correctedCheckInTime;
        this.correctedCheckOutTime = correctedCheckOutTime;
        this.reason = reason;
        this.status = status;
        this.rejectionReason = rejectionReason;
    }

    public static CorrectionApplication reconstitute(CorrectionId id, String employeeId, String attendanceRecordId,
            LocalDate correctionDate, CorrectionType correctionType,
            LocalTime correctedCheckInTime, LocalTime correctedCheckOutTime,
            String reason, ApplicationStatus status, String rejectionReason) {
        return new CorrectionApplication(id, employeeId, attendanceRecordId, correctionDate,
                correctionType, correctedCheckInTime, correctedCheckOutTime, reason, status, rejectionReason);
    }
}
