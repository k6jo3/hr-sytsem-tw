package com.company.hrms.recruitment.domain.event;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.OfferId;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;

/**
 * Offer 發送事件
 */
public class OfferSentEvent extends DomainEvent {

    private static final long serialVersionUID = 1L;

    private final OfferId offerId;
    private final CandidateId candidateId;
    private final String candidateName;
    private final String candidateEmail;
    private final OpeningId openingId;
    private final String offeredPosition;
    private final BigDecimal offeredSalary;
    private final LocalDate offeredStartDate;
    private final LocalDate offerDate;
    private final LocalDate expiryDate;
    private final String offerDocumentUrl;

    private OfferSentEvent(
            OfferId offerId,
            CandidateId candidateId,
            String candidateName,
            String candidateEmail,
            OpeningId openingId,
            String offeredPosition,
            BigDecimal offeredSalary,
            LocalDate offeredStartDate,
            LocalDate offerDate,
            LocalDate expiryDate,
            String offerDocumentUrl) {
        super();
        this.offerId = offerId;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.openingId = openingId;
        this.offeredPosition = offeredPosition;
        this.offeredSalary = offeredSalary;
        this.offeredStartDate = offeredStartDate;
        this.offerDate = offerDate;
        this.expiryDate = expiryDate;
        this.offerDocumentUrl = offerDocumentUrl;
    }

    public static OfferSentEvent create(
            OfferId offerId,
            CandidateId candidateId,
            String candidateName,
            String offeredPosition,
            BigDecimal offeredSalary,
            LocalDate expiryDate) {
        return new OfferSentEvent(
                offerId, candidateId, candidateName, null,
                null, offeredPosition, offeredSalary,
                null, LocalDate.now(), expiryDate, null);
    }

    @Override
    public String getAggregateId() {
        return offerId.getValue().toString();
    }

    @Override
    public String getAggregateType() {
        return "Offer";
    }

    // === Getters ===

    public OfferId getOfferId() {
        return offerId;
    }

    public CandidateId getCandidateId() {
        return candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public OpeningId getOpeningId() {
        return openingId;
    }

    public String getOfferedPosition() {
        return offeredPosition;
    }

    public BigDecimal getOfferedSalary() {
        return offeredSalary;
    }

    public LocalDate getOfferedStartDate() {
        return offeredStartDate;
    }

    public LocalDate getOfferDate() {
        return offerDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getOfferDocumentUrl() {
        return offerDocumentUrl;
    }
}
