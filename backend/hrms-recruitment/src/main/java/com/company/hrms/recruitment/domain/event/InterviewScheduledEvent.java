package com.company.hrms.recruitment.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewId;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewType;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;

/**
 * 面試安排事件
 */
public class InterviewScheduledEvent extends DomainEvent {

    private static final long serialVersionUID = 1L;

    private final InterviewId interviewId;
    private final CandidateId candidateId;
    private final String candidateName;
    private final String candidateEmail;
    private final OpeningId openingId;
    private final String jobTitle;
    private final int interviewRound;
    private final InterviewType interviewType;
    private final LocalDateTime interviewDate;
    private final String location;
    private final List<InterviewerInfo> interviewers;
    private final UUID scheduledBy;

    public static class InterviewerInfo {
        private final UUID employeeId;
        private final String employeeName;
        private final String email;

        public InterviewerInfo(UUID employeeId, String employeeName, String email) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.email = email;
        }

        public UUID getEmployeeId() {
            return employeeId;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public String getEmail() {
            return email;
        }
    }

    private InterviewScheduledEvent(
            InterviewId interviewId,
            CandidateId candidateId,
            String candidateName,
            String candidateEmail,
            OpeningId openingId,
            String jobTitle,
            int interviewRound,
            InterviewType interviewType,
            LocalDateTime interviewDate,
            String location,
            List<InterviewerInfo> interviewers,
            UUID scheduledBy) {
        super();
        this.interviewId = interviewId;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.openingId = openingId;
        this.jobTitle = jobTitle;
        this.interviewRound = interviewRound;
        this.interviewType = interviewType;
        this.interviewDate = interviewDate;
        this.location = location;
        this.interviewers = interviewers;
        this.scheduledBy = scheduledBy;
    }

    public static InterviewScheduledEvent create(
            InterviewId interviewId,
            CandidateId candidateId,
            String candidateName,
            int interviewRound,
            InterviewType interviewType,
            LocalDateTime interviewDate,
            String location) {
        return new InterviewScheduledEvent(
                interviewId, candidateId, candidateName, null,
                null, null, interviewRound, interviewType,
                interviewDate, location, null, null);
    }

    @Override
    public String getAggregateId() {
        return interviewId.getValue().toString();
    }

    @Override
    public String getAggregateType() {
        return "Interview";
    }

    // === Getters ===

    public InterviewId getInterviewId() {
        return interviewId;
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

    public String getJobTitle() {
        return jobTitle;
    }

    public int getInterviewRound() {
        return interviewRound;
    }

    public InterviewType getInterviewType() {
        return interviewType;
    }

    public LocalDateTime getInterviewDate() {
        return interviewDate;
    }

    public String getLocation() {
        return location;
    }

    public List<InterviewerInfo> getInterviewers() {
        return interviewers;
    }

    public UUID getScheduledBy() {
        return scheduledBy;
    }
}
