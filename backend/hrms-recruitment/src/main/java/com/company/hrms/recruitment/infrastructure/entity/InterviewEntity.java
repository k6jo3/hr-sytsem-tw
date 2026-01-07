package com.company.hrms.recruitment.infrastructure.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.company.hrms.recruitment.domain.model.valueobject.InterviewStatus;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 面試 Entity
 */
@Entity
@Table(name = "interviews")
public class InterviewEntity {

    @Id
    @Column(name = "interview_id")
    private UUID interviewId;

    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    @Column(name = "candidate_name", length = 100)
    private String candidateName;

    @Column(name = "interview_round", nullable = false)
    private int interviewRound;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_type", nullable = false, length = 20)
    private InterviewType interviewType;

    @Column(name = "interview_date", nullable = false)
    private LocalDateTime interviewDate;

    @Column(name = "location", length = 200)
    private String location;

    // 面試官 ID 列表，以 JSON 字串儲存
    @Column(name = "interviewer_ids", columnDefinition = "TEXT")
    private String interviewerIdsJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InterviewStatus status;

    // 評估資料，以 JSON 字串儲存
    @Column(name = "evaluations", columnDefinition = "TEXT")
    private String evaluationsJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // === Getters and Setters ===

    public UUID getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(UUID interviewId) {
        this.interviewId = interviewId;
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

    public int getInterviewRound() {
        return interviewRound;
    }

    public void setInterviewRound(int interviewRound) {
        this.interviewRound = interviewRound;
    }

    public InterviewType getInterviewType() {
        return interviewType;
    }

    public void setInterviewType(InterviewType interviewType) {
        this.interviewType = interviewType;
    }

    public LocalDateTime getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(LocalDateTime interviewDate) {
        this.interviewDate = interviewDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInterviewerIdsJson() {
        return interviewerIdsJson;
    }

    public void setInterviewerIdsJson(String interviewerIdsJson) {
        this.interviewerIdsJson = interviewerIdsJson;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }

    public String getEvaluationsJson() {
        return evaluationsJson;
    }

    public void setEvaluationsJson(String evaluationsJson) {
        this.evaluationsJson = evaluationsJson;
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
