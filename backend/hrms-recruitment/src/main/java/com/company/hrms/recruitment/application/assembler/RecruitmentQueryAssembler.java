package com.company.hrms.recruitment.application.assembler;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 招聘查詢組裝器
 * 
 * 將 Request DTO 轉換為 QueryGroup，供 Repository 使用。
 */
import org.springframework.stereotype.Component;

import com.company.hrms.common.query.LogicalOp;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryCondition;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateStatus;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewStatus;
import com.company.hrms.recruitment.domain.model.valueobject.JobStatus;
import com.company.hrms.recruitment.domain.model.valueobject.OfferStatus;

/**
 * 招聘查詢組裝器
 * 
 * 將 Request DTO 轉換為 QueryGroup，供 Repository 使用。
 */
@Component
public class RecruitmentQueryAssembler {

    /**
     * 組裝職缺查詢條件
     */
    public QueryGroup toJobOpeningQuery(
            JobStatus status,
            String departmentId,
            String keyword) {

        JobOpeningCondition condition = new JobOpeningCondition();
        condition.setStatus(status);
        condition.setDepartmentId(departmentId);
        condition.setIsDeleted(0);

        // 使用 fromCondition 解析 @QueryCondition.EQ / @LIKE 等標準注解
        QueryGroup queryGroup = QueryBuilder.fromCondition(condition);

        if (keyword != null && !keyword.isBlank()) {
            JobOpeningKeywordCondition keywordCondition = new JobOpeningKeywordCondition();
            keywordCondition.setTitle(keyword);
            keywordCondition.setRequirements(keyword);

            // 以 fromCondition 取得條件後，轉成 OR 子群組
            QueryGroup kwGroup = QueryBuilder.fromCondition(keywordCondition);
            kwGroup.setJunction(LogicalOp.OR);
            queryGroup.addSubGroup(kwGroup);
        }

        return queryGroup;
    }

    @lombok.Data
    public static class JobOpeningCondition {
        @QueryCondition.EQ
        private JobStatus status;

        @QueryCondition.EQ("department_id")
        private String departmentId;

        @QueryCondition.EQ("is_deleted")
        private Integer isDeleted;
    }

    @lombok.Data
    public static class JobOpeningKeywordCondition {
        @QueryCondition.LIKE("title")
        private String title;

        @QueryCondition.LIKE("requirements")
        private String requirements;
    }

    /**
     * 組裝應徵者查詢條件
     */
    public QueryGroup toCandidateQuery(
            String openingId,
            CandidateStatus status,
            String keyword) {

        CandidateCondition condition = new CandidateCondition();
        if (openingId != null && !openingId.isBlank()) {
            condition.setOpeningId(UUID.fromString(openingId));
        }
        condition.setStatus(status);

        // 使用 fromCondition 解析 @QueryCondition.EQ / @LIKE 等標準注解
        QueryGroup queryGroup = QueryBuilder.fromCondition(condition);

        if (keyword != null && !keyword.isBlank()) {
            CandidateKeywordCondition keywordCondition = new CandidateKeywordCondition();
            keywordCondition.setFullName(keyword);
            keywordCondition.setEmail(keyword);

            // 以 fromCondition 取得條件後，轉成 OR 子群組
            QueryGroup kwGroup = QueryBuilder.fromCondition(keywordCondition);
            kwGroup.setJunction(LogicalOp.OR);
            queryGroup.addSubGroup(kwGroup);
        }

        return queryGroup;
    }

    @lombok.Data
    public static class CandidateCondition {
        @QueryCondition.EQ("openingId")
        private UUID openingId;

        @QueryCondition.EQ
        private CandidateStatus status;
    }

    @lombok.Data
    public static class CandidateKeywordCondition {
        @QueryCondition.LIKE("fullName")
        private String fullName;

        @QueryCondition.LIKE("email")
        private String email;
    }

    /**
     * 組裝面試查詢條件
     */
    public QueryGroup toInterviewQuery(
            String candidateId,
            String interviewStatus,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        InterviewCondition condition = new InterviewCondition();
        if (candidateId != null && !candidateId.isBlank()) {
            condition.setCandidateId(UUID.fromString(candidateId));
        }
        if (interviewStatus != null && !interviewStatus.isBlank()) {
            condition.setStatus(InterviewStatus.valueOf(interviewStatus));
        }
        condition.setStartDate(startDate);
        condition.setEndDate(endDate);

        // 使用 fromCondition 解析 @QueryCondition.EQ / @GTE / @LTE 等標準注解
        return QueryBuilder.fromCondition(condition);
    }

    @lombok.Data
    public static class InterviewCondition {
        @QueryCondition.EQ("candidateId")
        private UUID candidateId;

        @QueryCondition.EQ("status")
        private InterviewStatus status;

        @QueryCondition.GTE("interviewDate")
        private LocalDateTime startDate;

        @QueryCondition.LTE("interviewDate")
        private LocalDateTime endDate;
    }

    /**
     * 組裝 Offer 查詢條件
     */
    public QueryGroup toOfferQuery(
            String candidateId,
            String offerStatus) {

        OfferCondition condition = new OfferCondition();
        if (candidateId != null && !candidateId.isBlank()) {
            condition.setCandidateId(UUID.fromString(candidateId));
        }
        if (offerStatus != null && !offerStatus.isBlank()) {
            condition.setStatus(OfferStatus.valueOf(offerStatus));
        }

        // 使用 fromCondition 解析 @QueryCondition.EQ 等標準注解
        return QueryBuilder.fromCondition(condition);
    }

    @lombok.Data
    public static class OfferCondition {
        @QueryCondition.EQ("candidateId")
        private UUID candidateId;

        @QueryCondition.EQ("status")
        private OfferStatus status;
    }
}
