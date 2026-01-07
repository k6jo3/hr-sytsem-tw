package com.company.hrms.recruitment.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.company.hrms.recruitment.domain.model.valueobject.OfferStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Offer Entity
 */
@Entity
@Table(name = "offers")
public class OfferEntity {

    @Id
    @Column(name = "offer_id")
    private UUID offerId;

    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    @Column(name = "candidate_name", length = 100)
    private String candidateName;

    @Column(name = "offered_position", nullable = false, length = 100)
    private String offeredPosition;

    @Column(name = "offered_salary", nullable = false)
    private BigDecimal offeredSalary;

    @Column(name = "offered_start_date")
    private LocalDate offeredStartDate;

    @Column(name = "offer_date", nullable = false)
    private LocalDate offerDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OfferStatus status;

    @Column(name = "response_date")
    private LocalDate responseDate;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // === Getters and Setters ===

    public UUID getOfferId() {
        return offerId;
    }

    public void setOfferId(UUID offerId) {
        this.offerId = offerId;
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getOfferedPosition() {
        return offeredPosition;
    }

    public void setOfferedPosition(String offeredPosition) {
        this.offeredPosition = offeredPosition;
    }

    public BigDecimal getOfferedSalary() {
        return offeredSalary;
    }

    public void setOfferedSalary(BigDecimal offeredSalary) {
        this.offeredSalary = offeredSalary;
    }

    public LocalDate getOfferedStartDate() {
        return offeredStartDate;
    }

    public void setOfferedStartDate(LocalDate offeredStartDate) {
        this.offeredStartDate = offeredStartDate;
    }

    public LocalDate getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(LocalDate offerDate) {
        this.offerDate = offerDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public LocalDate getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDate responseDate) {
        this.responseDate = responseDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
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
