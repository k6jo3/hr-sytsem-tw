package com.company.hrms.attendance.domain.model.aggregate;

import java.time.LocalDate;

import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.LeavePeriodType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

@Getter
public class LeaveApplication extends AggregateRoot<ApplicationId> {

    private String employeeId;
    private LeaveTypeId leaveTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeavePeriodType startPeriod;
    private LeavePeriodType endPeriod;
    private String reason;
    private String proofAttachmentUrl;
    private ApplicationStatus status;
    private String rejectionReason;

    public LeaveApplication(ApplicationId id, String employeeId, LeaveTypeId leaveTypeId,
            LocalDate startDate, LocalDate endDate,
            LeavePeriodType startPeriod, LeavePeriodType endPeriod,
            String reason) {
        super(id);
        validateDates(startDate, endDate);

        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
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

    public void cancel() {
        if (this.status == ApplicationStatus.REJECTED) {
            throw new IllegalStateException("Cannot cancel rejected applications");
        }
        this.status = ApplicationStatus.CANCELLED;
    }

    public void setProofAttachmentUrl(String url) {
        this.proofAttachmentUrl = url;
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    private LeaveApplication(ApplicationId id, String employeeId, LeaveTypeId leaveTypeId,
            LocalDate startDate, LocalDate endDate,
            LeavePeriodType startPeriod, LeavePeriodType endPeriod,
            String reason, String proofAttachmentUrl,
            ApplicationStatus status, String rejectionReason) {
        super(id);
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.reason = reason;
        this.proofAttachmentUrl = proofAttachmentUrl;
        this.status = status;
        this.rejectionReason = rejectionReason;
    }

    public static LeaveApplication reconstitute(ApplicationId id, String employeeId, LeaveTypeId leaveTypeId,
            LocalDate startDate, LocalDate endDate,
            LeavePeriodType startPeriod, LeavePeriodType endPeriod,
            String reason, String proofAttachmentUrl,
            ApplicationStatus status, String rejectionReason) {
        return new LeaveApplication(id, employeeId, leaveTypeId, startDate, endDate,
                startPeriod, endPeriod, reason, proofAttachmentUrl, status, rejectionReason);
    }
}
