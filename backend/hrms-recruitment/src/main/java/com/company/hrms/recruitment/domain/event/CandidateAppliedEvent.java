package com.company.hrms.recruitment.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;

/**
 * 應徵者投遞事件
 */
public class CandidateAppliedEvent extends DomainEvent {

    private static final long serialVersionUID = 1L;

    private final CandidateId candidateId;
    private final String fullName;
    private final String email;
    private final OpeningId openingId;
    private final String jobTitle;
    private final String source;

    private CandidateAppliedEvent(
            CandidateId candidateId,
            String fullName,
            String email,
            OpeningId openingId,
            String jobTitle,
            String source) {
        super();
        this.candidateId = candidateId;
        this.fullName = fullName;
        this.email = email;
        this.openingId = openingId;
        this.jobTitle = jobTitle;
        this.source = source;
    }

    public static CandidateAppliedEvent create(
            CandidateId candidateId,
            String fullName,
            String email,
            OpeningId openingId,
            String source) {
        return new CandidateAppliedEvent(
                candidateId, fullName, email, openingId, null, source);
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

    public OpeningId getOpeningId() {
        return openingId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getSource() {
        return source;
    }
}
