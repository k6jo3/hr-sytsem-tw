package com.company.hrms.recruitment.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.dto.report.DashboardResponse;
import com.company.hrms.recruitment.application.dto.report.DashboardSearchDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得招募儀表板 Service
 */
@Slf4j
@Service("getDashboardServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetDashboardServiceImpl
                implements QueryApiService<DashboardSearchDto, DashboardResponse> {

        @Override
        public DashboardResponse getResponse(
                        DashboardSearchDto request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                // 處理預設日期範圍
                LocalDate dateFrom = request.getDateFrom();
                LocalDate dateTo = request.getDateTo();

                if (dateFrom == null) {
                        dateFrom = LocalDate.now().withDayOfMonth(1); // 預設當月第一天
                }
                if (dateTo == null) {
                        dateTo = LocalDate.now(); // 預設今天
                }

                log.info("取得招募儀表板: from={}, to={}", dateFrom, dateTo);

                long openJobs = jobOpeningRepository
                                .count(com.company.hrms.common.query.QueryBuilder.where().eq("status", "OPEN").build());
                long totalApplications = candidateRepository.count(com.company.hrms.common.query.QueryBuilder.where()
                                .between("applicationDate", dateFrom, dateTo).build());
                long interviews = candidateRepository.count(com.company.hrms.common.query.QueryBuilder.where()
                                .eq("status", "INTERVIEWING").between("applicationDate", dateFrom, dateTo).build());
                long offers = candidateRepository.count(com.company.hrms.common.query.QueryBuilder.where()
                                .eq("status", "OFFERED").between("applicationDate", dateFrom, dateTo).build());
                long hired = candidateRepository.count(com.company.hrms.common.query.QueryBuilder.where()
                                .eq("status", "HIRED").between("applicationDate", dateFrom, dateTo).build());

                // Note: 進階統計 (AvgTimeToHire, OfferAcceptanceRate) 需實作 Aggregation Query，目前暫回傳 0
                // 或僅算數值
                BigDecimal acceptanceRate = BigDecimal.ZERO;
                if (offers > 0) {
                        acceptanceRate = BigDecimal.valueOf(hired)
                                        .divide(BigDecimal.valueOf(offers), 2, java.math.RoundingMode.HALF_UP)
                                        .multiply(BigDecimal.valueOf(100));
                }

                return DashboardResponse.builder()
                                .period(DashboardResponse.Period.builder()
                                                .from(dateFrom)
                                                .to(dateTo)
                                                .build())
                                .kpis(DashboardResponse.KPIs.builder()
                                                .openJobsCount((int) openJobs)
                                                .totalApplications((int) totalApplications)
                                                .interviewsScheduled((int) interviews)
                                                .offersExtended((int) offers)
                                                .hiredCount((int) hired)
                                                .avgTimeToHire(0) // 需複雜查詢
                                                .offerAcceptanceRate(acceptanceRate)
                                                .build())
                                .sourceAnalytics(List.of()) // 需 GroupBy 查詢
                                .conversionFunnel(DashboardResponse.ConversionFunnel.builder()
                                                .applied((int) totalApplications)
                                                .screened(0) // New -> Screening
                                                .interviewed((int) interviews)
                                                .offered((int) offers)
                                                .hired((int) hired)
                                                .rates(DashboardResponse.ConversionRates.builder().build())
                                                .build())
                                .openingsByDepartment(List.of()) // 需 GroupBy
                                .monthlyTrend(List.of()) // 需 GroupBy
                                .build();
        }

        private final com.company.hrms.recruitment.domain.repository.IJobOpeningRepository jobOpeningRepository;
        private final com.company.hrms.recruitment.domain.repository.ICandidateRepository candidateRepository;
}
