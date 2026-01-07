package com.company.hrms.recruitment.domain.event;

import java.time.LocalDate;
import java.util.UUID;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;

/**
 * 應徵者錄取事件
 * 
 * 當應徵者被錄取時發布此事件。
 * 訂閱服務：Organization Service（自動建立員工資料）、Notification Service（發送通知）
 */
public class CandidateHiredEvent extends DomainEvent {

    private static final long serialVersionUID = 1L;

    private final CandidateId candidateId;
    private final String fullName;
    private final String email;
    private final String phoneNumber;
    private final OpeningId openingId;
    private final String jobTitle;
    private final UUID departmentId;
    private final String departmentName;
    private final java.math.BigDecimal offeredSalary;
    private final LocalDate expectedStartDate;
    private final String resumeUrl;
    private final UUID referrerId;
    private final String referrerName;
    private final UUID hiredBy;
    private final String hiredByName;

    private CandidateHiredEvent(
            CandidateId candidateId,
            String fullName,
            String email,
            String phoneNumber,
            OpeningId openingId,
            String jobTitle,
            UUID departmentId,
            String departmentName,
            java.math.BigDecimal offeredSalary,
            LocalDate expectedStartDate,
            String resumeUrl,
            UUID referrerId,
            String referrerName,
            UUID hiredBy,
            String hiredByName) {
        super();
        this.candidateId = candidateId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.openingId = openingId;
        this.jobTitle = jobTitle;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.offeredSalary = offeredSalary;
        this.expectedStartDate = expectedStartDate;
        this.resumeUrl = resumeUrl;
        this.referrerId = referrerId;
        this.referrerName = referrerName;
        this.hiredBy = hiredBy;
        this.hiredByName = hiredByName;
    }

    /**
     * 建立簡化版事件（常用於單元測試或基本場景）
     */
    public static CandidateHiredEvent create(
            CandidateId candidateId,
            String fullName,
            String email,
            String phoneNumber,
            OpeningId openingId) {
        return new CandidateHiredEvent(
                candidateId, fullName, email, phoneNumber,
                openingId, null, null, null,
                null, null, null,
                null, null, null, null);
    }

    /**
     * 建立完整版事件
     */
    public static CandidateHiredEvent createFull(
            CandidateId candidateId,
            String fullName,
            String email,
            String phoneNumber,
            OpeningId openingId,
            String jobTitle,
            UUID departmentId,
            String departmentName,
            java.math.BigDecimal offeredSalary,
            LocalDate expectedStartDate,
            String resumeUrl,
            UUID referrerId,
            String referrerName,
            UUID hiredBy,
            String hiredByName) {
        return new CandidateHiredEvent(
                candidateId, fullName, email, phoneNumber,
                openingId, jobTitle, departmentId, departmentName,
                offeredSalary, expectedStartDate, resumeUrl,
                referrerId, referrerName, hiredBy, hiredByName);
    }

    @Override
    public String getAggregateId() {
        return candidateId.getValue().toString();
    }

    @Override
    public String getAggregateType() {
        return "Candidate";
    }

    // === Getters ===

    public CandidateId getCandidateId() {
        return candidateId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public OpeningId getOpeningId() {
        return openingId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public java.math.BigDecimal getOfferedSalary() {
        return offeredSalary;
    }

    public LocalDate getExpectedStartDate() {
        return expectedStartDate;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public UUID getReferrerId() {
        return referrerId;
    }

    public String getReferrerName() {
        return referrerName;
    }

    public UUID getHiredBy() {
        return hiredBy;
    }

    public String getHiredByName() {
        return hiredByName;
    }
}
