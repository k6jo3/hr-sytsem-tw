package com.company.hrms.recruitment.domain.model.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.recruitment.domain.event.InterviewScheduledEvent;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewId;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewStatus;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewType;
import com.company.hrms.recruitment.domain.model.valueobject.OverallRating;

/**
 * 面試聚合根
 * 
 * 管理面試排程、評估和結果。
 */
public class Interview extends AggregateRoot<InterviewId> {

    /**
     * 應徵者 ID
     */
    private CandidateId candidateId;

    /**
     * 應徵者姓名（快照）
     */
    private String candidateName;

    /**
     * 面試輪次
     */
    private int interviewRound;

    /**
     * 面試類型
     */
    private InterviewType interviewType;

    /**
     * 面試日期時間
     */
    private LocalDateTime interviewDate;

    /**
     * 面試地點
     */
    private String location;

    /**
     * 面試官 ID 列表
     */
    private List<UUID> interviewerIds;

    /**
     * 面試狀態
     */
    private InterviewStatus status;

    /**
     * 面試評估列表
     */
    private List<InterviewEvaluation> evaluations;

    /**
     * 面試評估內部類
     */
    public static class InterviewEvaluation {
        private final UUID interviewerId;
        private final Integer technicalScore;
        private final Integer communicationScore;
        private final Integer cultureFitScore;
        private final OverallRating overallRating;
        private final String comments;
        private final String strengths;
        private final String concerns;
        private final LocalDateTime evaluatedAt;

        public InterviewEvaluation(
                UUID interviewerId,
                Integer technicalScore,
                Integer communicationScore,
                Integer cultureFitScore,
                OverallRating overallRating,
                String comments,
                String strengths,
                String concerns) {
            this.interviewerId = interviewerId;
            this.technicalScore = technicalScore;
            this.communicationScore = communicationScore;
            this.cultureFitScore = cultureFitScore;
            this.overallRating = overallRating;
            this.comments = comments;
            this.strengths = strengths;
            this.concerns = concerns;
            this.evaluatedAt = LocalDateTime.now();
        }

        // Getters
        public UUID getInterviewerId() {
            return interviewerId;
        }

        public Integer getTechnicalScore() {
            return technicalScore;
        }

        public Integer getCommunicationScore() {
            return communicationScore;
        }

        public Integer getCultureFitScore() {
            return cultureFitScore;
        }

        public OverallRating getOverallRating() {
            return overallRating;
        }

        public String getComments() {
            return comments;
        }

        public String getStrengths() {
            return strengths;
        }

        public String getConcerns() {
            return concerns;
        }

        public LocalDateTime getEvaluatedAt() {
            return evaluatedAt;
        }
    }

    /**
     * Domain 建構子
     */
    private Interview(InterviewId id) {
        super(id);
    }

    /**
     * 安排面試
     */
    public static Interview schedule(
            CandidateId candidateId,
            String candidateName,
            int interviewRound,
            InterviewType interviewType,
            LocalDateTime interviewDate,
            String location,
            List<UUID> interviewerIds) {

        validateCandidateId(candidateId);
        validateInterviewRound(interviewRound);
        validateInterviewType(interviewType);
        validateInterviewDate(interviewDate);
        validateInterviewers(interviewerIds);

        InterviewId interviewId = InterviewId.create();
        Interview interview = new Interview(interviewId);
        interview.candidateId = candidateId;
        interview.candidateName = candidateName;
        interview.interviewRound = interviewRound;
        interview.interviewType = interviewType;
        interview.interviewDate = interviewDate;
        interview.location = location;
        interview.interviewerIds = new ArrayList<>(interviewerIds);
        interview.status = InterviewStatus.SCHEDULED;
        interview.evaluations = new ArrayList<>();

        // 發布面試安排事件
        interview.registerEvent(InterviewScheduledEvent.create(
                interviewId,
                candidateId,
                candidateName,
                interviewRound,
                interviewType,
                interviewDate,
                location));

        return interview;
    }

    /**
     * 重建面試（從資料庫載入）
     */
    public static Interview reconstitute(
            InterviewId interviewId,
            CandidateId candidateId,
            String candidateName,
            int interviewRound,
            InterviewType interviewType,
            LocalDateTime interviewDate,
            String location,
            List<UUID> interviewerIds,
            InterviewStatus status,
            List<InterviewEvaluation> evaluations,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        Interview interview = new Interview(interviewId);
        interview.candidateId = candidateId;
        interview.candidateName = candidateName;
        interview.interviewRound = interviewRound;
        interview.interviewType = interviewType;
        interview.interviewDate = interviewDate;
        interview.location = location;
        interview.interviewerIds = interviewerIds != null ? new ArrayList<>(interviewerIds) : new ArrayList<>();
        interview.status = status;
        interview.evaluations = evaluations != null ? new ArrayList<>(evaluations) : new ArrayList<>();
        interview.createdAt = createdAt;
        interview.updatedAt = updatedAt;

        return interview;
    }

    // === 狀態轉換方法 ===

    /**
     * 提交面試評估
     */
    public void addEvaluation(
            UUID interviewerId,
            Integer technicalScore,
            Integer communicationScore,
            Integer cultureFitScore,
            OverallRating overallRating,
            String comments,
            String strengths,
            String concerns) {

        if (this.status != InterviewStatus.SCHEDULED) {
            throw new IllegalStateException("只有已排程狀態可以提交評估，當前狀態：" + this.status.getDisplayName());
        }

        // 檢查評估者是否為面試官
        if (!this.interviewerIds.contains(interviewerId)) {
            throw new IllegalArgumentException("評估者必須是該面試的面試官");
        }

        // 檢查是否已評估
        boolean alreadyEvaluated = this.evaluations.stream()
                .anyMatch(e -> e.getInterviewerId().equals(interviewerId));
        if (alreadyEvaluated) {
            throw new IllegalStateException("該面試官已提交評估");
        }

        // 驗證分數
        validateScore(technicalScore, "技術分數");
        validateScore(communicationScore, "溝通分數");
        validateScore(cultureFitScore, "文化契合分數");

        InterviewEvaluation evaluation = new InterviewEvaluation(
                interviewerId,
                technicalScore,
                communicationScore,
                cultureFitScore,
                overallRating,
                comments,
                strengths,
                concerns);

        this.evaluations.add(evaluation);
        touch();

        // 如果所有面試官都已評估，標記為完成
        if (this.evaluations.size() >= this.interviewerIds.size()) {
            this.status = InterviewStatus.COMPLETED;
        }
    }

    /**
     * 標記面試完成
     */
    public void complete() {
        if (this.status != InterviewStatus.SCHEDULED) {
            throw new IllegalStateException("只有已排程狀態可以標記完成，當前狀態：" + this.status.getDisplayName());
        }
        this.status = InterviewStatus.COMPLETED;
        touch();
    }

    /**
     * 取消面試
     */
    public void cancel() {
        if (this.status == InterviewStatus.COMPLETED) {
            throw new IllegalStateException("已完成的面試無法取消");
        }
        if (this.status == InterviewStatus.CANCELLED) {
            throw new IllegalStateException("面試已取消");
        }
        this.status = InterviewStatus.CANCELLED;
        touch();
    }

    /**
     * 重新排程面試
     */
    public void reschedule(LocalDateTime newDate, String newLocation) {
        if (this.status != InterviewStatus.SCHEDULED) {
            throw new IllegalStateException("只有已排程狀態可以重新排程");
        }
        validateInterviewDate(newDate);
        this.interviewDate = newDate;
        this.location = newLocation;
        touch();
    }

    // === 驗證方法 ===

    private static void validateCandidateId(CandidateId candidateId) {
        if (candidateId == null) {
            throw new IllegalArgumentException("應徵者 ID 不可為空");
        }
    }

    private static void validateInterviewRound(int round) {
        if (round < 1) {
            throw new IllegalArgumentException("面試輪次必須大於等於 1");
        }
    }

    private static void validateInterviewType(InterviewType type) {
        if (type == null) {
            throw new IllegalArgumentException("面試類型不可為空");
        }
    }

    private static void validateInterviewDate(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("面試日期不可為空");
        }
    }

    private static void validateInterviewers(List<UUID> interviewerIds) {
        if (interviewerIds == null || interviewerIds.isEmpty()) {
            throw new IllegalArgumentException("至少需要一位面試官");
        }
    }

    private static void validateScore(Integer score, String fieldName) {
        if (score != null && (score < 1 || score > 5)) {
            throw new IllegalArgumentException(fieldName + " 必須在 1-5 之間");
        }
    }

    // === Getters ===

    public CandidateId getCandidateId() {
        return candidateId;
    }

    public String getCandidateName() {
        return candidateName;
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

    public List<UUID> getInterviewerIds() {
        return Collections.unmodifiableList(interviewerIds);
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public List<InterviewEvaluation> getEvaluations() {
        return Collections.unmodifiableList(evaluations);
    }
}
