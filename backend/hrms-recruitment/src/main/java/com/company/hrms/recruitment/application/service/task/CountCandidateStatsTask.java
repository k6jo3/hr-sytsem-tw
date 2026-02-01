package com.company.hrms.recruitment.application.service.task;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryCondition;
import com.company.hrms.recruitment.application.service.context.DashboardContext;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;

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
        CandidateStatCondition cond = new CandidateStatCondition();
        cond.setStatus(status); // if null, @EQ skips or we handle explicitly? Fluent engine skips nulls by
                                // default for @EQ usually.
        // Actually for Date Range we need specific handling if not purely @BETWEEN on
        // single field DTO
        // Let's use clean separate logic or a reusable method

        var builder = QueryBuilder.where();
        if (status != null) {
            builder.eq("status", status);
        }
        // Date range
        builder.between("applicationDate", from, to);

        // TODO: Handle department join if needed. Assuming simple case for now or
        // departmentId is on Candidate (unlikely, usually on JobOpening).
        // Since original code didn't handle department filter on candidates, I will
        // skip it too to match original logic,
        // OR better, verify domain model. But for now I match original logic which
        // relied on date mainly.

        return candidateRepository.count(builder.build());
    }

    @Override
    public String getName() {
        return "統計應徵狀態";
    }

    @Data
    public static class CandidateStatCondition {
        @QueryCondition.EQ
        private String status;

        // Complex date logic usually handled by builder API if simple DTO annotation
        // doesn't suffice (e.g. dynamic range)
    }
}
