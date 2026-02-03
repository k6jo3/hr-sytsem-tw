package com.company.hrms.recruitment.application.service.task;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryCondition.BETWEEN;
import com.company.hrms.common.query.QueryCondition.EQ;
import com.company.hrms.recruitment.application.service.context.DashboardContext;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 統計各種應徵狀態數量
 */
@Component
@RequiredArgsConstructor
public class CountCandidateStatsTask implements PipelineTask<DashboardContext> {

    private final ICandidateRepository candidateRepository;

    @Override
    public void execute(DashboardContext context) throws Exception {
        LocalDate from = context.getDateFrom();
        LocalDate to = context.getDateTo();
        String deptId = context.getRequest().getDepartmentId();

        // 1. Total Applications
        context.setTotalApplicationsCount(countByStatus(null, from, to, deptId));

        // 2. Interviews
        context.setInterviewsCount(countByStatus("INTERVIEWING", from, to, deptId));

        // 3. Offers
        context.setOffersCount(countByStatus("OFFERED", from, to, deptId));

        // 4. Hired
        context.setHiredCount(countByStatus("HIRED", from, to, deptId));
    }

    private long countByStatus(String status, LocalDate from, LocalDate to, String deptId) {
        // 使用宣告式設計建構查詢條件
        CandidateStatCondition cond = CandidateStatCondition.builder()
                .status(status)
                .dateRange(Arrays.asList(from, to))
                .departmentId(deptId != null && !deptId.isBlank() ? UUID.fromString(deptId) : null)
                .build();

        return candidateRepository.count(QueryBuilder.fromCondition(cond));
    }

    @Override
    public String getName() {
        return "統計應徵狀態";
    }

    @Data
    @Builder
    public static class CandidateStatCondition {
        @EQ
        private String status;

        @BETWEEN("applicationDate")
        private java.util.List<LocalDate> dateRange;

        @EQ("opening.departmentId")
        private java.util.UUID departmentId;
    }
}
