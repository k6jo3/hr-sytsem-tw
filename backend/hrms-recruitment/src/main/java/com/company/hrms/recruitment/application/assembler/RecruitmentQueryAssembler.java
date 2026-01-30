package com.company.hrms.recruitment.application.assembler;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateStatus;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewStatus;
import com.company.hrms.recruitment.domain.model.valueobject.JobStatus;

/**
 * 招聘查詢組裝器
 * 
 * 將 Request DTO 轉換為 QueryGroup，供 Repository 使用。
 */
public class RecruitmentQueryAssembler {

    /**
     * 組裝職缺查詢條件
     */
    public QueryGroup toJobOpeningQuery(
            JobStatus status,
            String departmentId,
            String keyword) {
        // TODO: 未符合Fluent-Query-Engine設計
        QueryBuilder builder = QueryBuilder.where();

        if (status != null) {
            builder.eq("status", status);
        }

        if (departmentId != null && !departmentId.isBlank()) {
            builder.eq("department_id", java.util.UUID.fromString(departmentId));
        }

        if (keyword != null && !keyword.isBlank()) {
            builder.orGroup(group -> group
                    .like("title", keyword)
                    .like("requirements", "%" + keyword + "%"));
        }

        // 軟刪除過濾
        builder.eq("is_deleted", 0);

        return builder.build();
    }

    /**
     * 組裝應徵者查詢條件
     */
    public QueryGroup toCandidateQuery(
            String openingId,
            CandidateStatus status,
            String keyword) {

        QueryBuilder builder = QueryBuilder.where();

        if (openingId != null && !openingId.isBlank()) {
            builder.eq("openingId", java.util.UUID.fromString(openingId));
        }

        if (status != null) {
            builder.eq("status", status);
        }

        if (keyword != null && !keyword.isBlank()) {
            builder.orGroup(group -> group
                    .like("fullName", "%" + keyword + "%")
                    .like("email", "%" + keyword + "%"));
        }

        return builder.build();
    }

    /**
     * 組裝面試查詢條件
     */
    public QueryGroup toInterviewQuery(
            String candidateId,
            String interviewStatus,
            java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate) {

        QueryBuilder builder = QueryBuilder.where();

        if (candidateId != null && !candidateId.isBlank()) {
            builder.eq("candidateId", java.util.UUID.fromString(candidateId));
        }

        if (interviewStatus != null && !interviewStatus.isBlank()) {
            builder.eq("status",
                    InterviewStatus.valueOf(interviewStatus));
        }

        if (startDate != null) {
            builder.gte("interviewDate", startDate);
        }

        if (endDate != null) {
            builder.lte("interviewDate", endDate);
        }

        return builder.build();
    }

    /**
     * 組裝 Offer 查詢條件
     */
    public QueryGroup toOfferQuery(
            String candidateId,
            String offerStatus) {

        QueryBuilder builder = QueryBuilder.where();

        if (candidateId != null && !candidateId.isBlank()) {
            builder.eq("candidateId", java.util.UUID.fromString(candidateId));
        }

        if (offerStatus != null && !offerStatus.isBlank()) {
            builder.eq("status",
                    com.company.hrms.recruitment.domain.model.valueobject.OfferStatus.valueOf(offerStatus));
        }

        return builder.build();
    }
}
