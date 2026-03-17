package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 員工離職事件 (系統關鍵事件)
 * 觸發時機: 員工離職
 * 訂閱服務: IAM, Attendance, Insurance, Payroll, Project
 */
@Getter
public class EmployeeTerminatedEvent extends DomainEvent {

    private final UUID employeeId;
    private final String employeeNumber;
    private final String fullName;
    private final String companyEmail;
    private final UUID organizationId;
    private final UUID departmentId;
    private final LocalDate terminationDate;
    private final String terminationReason;

    /**
     * 離職類型（VOLUNTARY_RESIGNATION, LAYOFF, DISMISSAL, MUTUAL_AGREEMENT, CONTRACT_EXPIRY, RETIREMENT）
     */
    private final String terminationType;

    /**
     * 到職日期（下游服務用於計算年資與資遣費）
     */
    private final LocalDate hireDate;

    /**
     * 預告期天數（依勞基法第 16 條計算）
     */
    private final int noticePeriodDays;

    public EmployeeTerminatedEvent(UUID employeeId, String employeeNumber, String fullName,
                                    String companyEmail, UUID organizationId, UUID departmentId,
                                    LocalDate terminationDate, String terminationReason,
                                    String terminationType, LocalDate hireDate, int noticePeriodDays) {
        super();
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.companyEmail = companyEmail;
        this.organizationId = organizationId;
        this.departmentId = departmentId;
        this.terminationDate = terminationDate;
        this.terminationReason = terminationReason;
        this.terminationType = terminationType;
        this.hireDate = hireDate;
        this.noticePeriodDays = noticePeriodDays;
    }
}
