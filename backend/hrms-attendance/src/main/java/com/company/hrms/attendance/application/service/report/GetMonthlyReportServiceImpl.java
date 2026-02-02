package com.company.hrms.attendance.application.service.report;

import org.springframework.stereotype.Service;

import com.company.hrms.attendance.api.request.report.GetMonthlyReportRequest;
import com.company.hrms.attendance.api.response.report.MonthlyReportResponse;
import com.company.hrms.attendance.application.service.report.context.MonthlyReportContext;
import com.company.hrms.attendance.application.service.report.task.AggregateMonthlyReportTask;
import com.company.hrms.attendance.application.service.report.task.FetchMonthlyAttendanceRecordsTask;
import com.company.hrms.attendance.application.service.report.task.FetchMonthlyLeaveApplicationsTask;
import com.company.hrms.attendance.application.service.report.task.FetchMonthlyOvertimeApplicationsTask;
import com.company.hrms.attendance.application.service.report.task.FetchMonthlyReportEmployeesTask;
import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢考勤月報表服務實作
 */
@Slf4j
@Service("getMonthlyReportServiceImpl")
@RequiredArgsConstructor
public class GetMonthlyReportServiceImpl extends AbstractQueryService<GetMonthlyReportRequest, MonthlyReportResponse> {

    // Tasks
    private final FetchMonthlyReportEmployeesTask fetchEmployeesTask;
    private final FetchMonthlyAttendanceRecordsTask fetchRecordsTask;
    private final FetchMonthlyLeaveApplicationsTask fetchLeavesTask;
    private final FetchMonthlyOvertimeApplicationsTask fetchOvertimesTask;
    private final AggregateMonthlyReportTask aggregateTask;

    @Override
    protected QueryGroup buildQuery(GetMonthlyReportRequest request, JWTModel currentUser) {
        return null;
    }

    @Override
    protected MonthlyReportResponse executeQuery(QueryGroup query, GetMonthlyReportRequest request,
            JWTModel currentUser, String... args) throws Exception {
        log.info("執行月報表 Pipeline: organizationId={}, year={}, month={}",
                request.getOrganizationId(), request.getYear(), request.getMonth());

        MonthlyReportContext context = new MonthlyReportContext(request, currentUser);

        BusinessPipeline.start(context)
                .next(fetchEmployeesTask)
                .next(fetchRecordsTask)
                .next(fetchLeavesTask)
                .next(fetchOvertimesTask)
                .next(aggregateTask)
                .execute();

        return context.getResponse();
    }
}
