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

    // TODO: 注入相關 Repository

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

        // TODO: 實際從 Repository 查詢資料，以下為模擬資料
        return buildMockDashboard(dateFrom, dateTo);
    }

    /**
     * 建立模擬儀表板資料（待實際實作時替換為真實查詢）
     */
    private DashboardResponse buildMockDashboard(LocalDate dateFrom, LocalDate dateTo) {
        return DashboardResponse.builder()
                .period(DashboardResponse.Period.builder()
                        .from(dateFrom)
                        .to(dateTo)
                        .build())
                .kpis(DashboardResponse.KPIs.builder()
                        .openJobsCount(12)
                        .totalApplications(85)
                        .interviewsScheduled(23)
                        .offersExtended(8)
                        .hiredCount(5)
                        .avgTimeToHire(28)
                        .offerAcceptanceRate(BigDecimal.valueOf(62.5))
                        .build())
                .sourceAnalytics(List.of(
                        DashboardResponse.SourceAnalytics.builder()
                                .source("JOB_BANK")
                                .sourceLabel("人力銀行")
                                .count(38)
                                .percentage(BigDecimal.valueOf(44.7))
                                .hiredCount(2)
                                .conversionRate(BigDecimal.valueOf(5.3))
                                .build(),
                        DashboardResponse.SourceAnalytics.builder()
                                .source("REFERRAL")
                                .sourceLabel("員工推薦")
                                .count(21)
                                .percentage(BigDecimal.valueOf(24.7))
                                .hiredCount(2)
                                .conversionRate(BigDecimal.valueOf(9.5))
                                .build(),
                        DashboardResponse.SourceAnalytics.builder()
                                .source("WEBSITE")
                                .sourceLabel("公司官網")
                                .count(17)
                                .percentage(BigDecimal.valueOf(20.0))
                                .hiredCount(1)
                                .conversionRate(BigDecimal.valueOf(5.9))
                                .build(),
                        DashboardResponse.SourceAnalytics.builder()
                                .source("LINKEDIN")
                                .sourceLabel("LinkedIn")
                                .count(9)
                                .percentage(BigDecimal.valueOf(10.6))
                                .hiredCount(0)
                                .conversionRate(BigDecimal.ZERO)
                                .build()))
                .conversionFunnel(DashboardResponse.ConversionFunnel.builder()
                        .applied(85)
                        .screened(45)
                        .interviewed(23)
                        .offered(8)
                        .hired(5)
                        .rates(DashboardResponse.ConversionRates.builder()
                                .screeningRate(BigDecimal.valueOf(52.9))
                                .interviewRate(BigDecimal.valueOf(51.1))
                                .offerRate(BigDecimal.valueOf(34.8))
                                .acceptRate(BigDecimal.valueOf(62.5))
                                .build())
                        .build())
                .openingsByDepartment(List.of(
                        DashboardResponse.DepartmentStats.builder()
                                .departmentId("dept-001")
                                .departmentName("研發部")
                                .openJobs(5)
                                .candidates(35)
                                .hired(2)
                                .build(),
                        DashboardResponse.DepartmentStats.builder()
                                .departmentId("dept-002")
                                .departmentName("業務部")
                                .openJobs(3)
                                .candidates(25)
                                .hired(1)
                                .build()))
                .monthlyTrend(List.of(
                        DashboardResponse.MonthlyTrend.builder()
                                .month("2025-10")
                                .applications(65)
                                .hired(3)
                                .build(),
                        DashboardResponse.MonthlyTrend.builder()
                                .month("2025-11")
                                .applications(72)
                                .hired(4)
                                .build(),
                        DashboardResponse.MonthlyTrend.builder()
                                .month("2025-12")
                                .applications(85)
                                .hired(5)
                                .build()))
                .build();
    }
}
