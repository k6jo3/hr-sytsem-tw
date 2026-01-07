package com.company.hrms.recruitment.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.company.hrms.recruitment.domain.model.valueobject.CandidateStatus;
import com.company.hrms.recruitment.domain.model.valueobject.RecruitmentSource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 應徵者 Entity
 */
@Entity
@Table(name = "candidates")
public class CandidateEntity {

    @Id
    @Column(name = "candidate_id")
    private UUID candidateId;

    @Column(name = "opening_id", nullable = false)
    private UUID openingId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false, length = 200)
    private String email;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "resume_url", length = 500)
    private String resumeUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 30)
    private RecruitmentSource source;

    @Column(name = "referrer_id")
    private UUID referrerId;

    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CandidateStatus status;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Column(name = "expected_salary")
    private BigDecimal expectedSalary;

    @Column(name = "available_date")
    private LocalDate availableDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // === Getters and Setters ===

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
    }

    public UUID getOpeningId() {
        return openingId;
    }

    public void setOpeningId(UUID openingId) {
        this.openingId = openingId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public RecruitmentSource getSource() {
        return source;
    }

    public void setSource(RecruitmentSource source) {
        this.source = source;
    }

    public UUID getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(UUID referrerId) {
        this.referrerId = referrerId;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public BigDecimal getExpectedSalary() {
        return expectedSalary;
    }

    public void setExpectedSalary(BigDecimal expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public LocalDate getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDate availableDate) {
        this.availableDate = availableDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
