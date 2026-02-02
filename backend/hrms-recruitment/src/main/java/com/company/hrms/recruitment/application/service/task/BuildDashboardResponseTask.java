package com.company.hrms.recruitment.application.service.task;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.dto.report.DashboardResponse;
import com.company.hrms.recruitment.application.service.context.DashboardContext;

/**
 * 建構儀表板回應
 */
@Component
public class BuildDashboardResponseTask implements PipelineTask<DashboardContext> {

    @Override
    public void execute(DashboardContext context) throws Exception {

        long offers = context.getOffersCount();
        long hired = context.getHiredCount();

        BigDecimal acceptanceRate = BigDecimal.ZERO;
        if (offers > 0) {
            acceptanceRate = BigDecimal.valueOf(hired)
                    .divide(BigDecimal.valueOf(offers), 2, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        DashboardResponse response = DashboardResponse.builder()
                .period(DashboardResponse.Period.builder()
                        .from(context.getDateFrom())
                        .to(context.getDateTo())
                        .build())
                .kpis(DashboardResponse.KPIs.builder()
                        .openJobsCount((int) context.getOpenJobsCount())
                        .totalApplications((int) context.getTotalApplicationsCount())
                        .interviewsScheduled((int) context.getInterviewsCount())
                        .offersExtended((int) offers)
                        .hiredCount((int) hired)
                        .avgTimeToHire(0) // Logic pending
                        .offerAcceptanceRate(acceptanceRate)
                        .build())
                .sourceAnalytics(List.of())
                .conversionFunnel(DashboardResponse.ConversionFunnel.builder()
                        .applied((int) context.getTotalApplicationsCount())
                        .screened(0)
                        .interviewed((int) context.getInterviewsCount())
                        .offered((int) offers)
                        .hired((int) hired)
                        .rates(DashboardResponse.ConversionRates.builder().build())
                        .build())
                .openingsByDepartment(List.of())
                .monthlyTrend(List.of())
                .build();

        context.setResponse(response);
    }

    @Override
    public String getName() {
        return "建構儀表板回應";
    }
}
