package com.company.hrms.attendance.application.service.report;

import org.springframework.stereotype.Service;

import com.company.hrms.attendance.api.request.report.GetDailyReportRequest;
import com.company.hrms.attendance.api.response.report.DailyReportResponse;
import com.company.hrms.attendance.application.service.report.context.DailyReportContext;
import com.company.hrms.attendance.application.service.report.task.AggregateDailyReportTask;
import com.company.hrms.attendance.application.service.report.task.FetchAllShiftsTask;
import com.company.hrms.attendance.application.service.report.task.FetchDailyAttendanceRecordsTask;
import com.company.hrms.attendance.application.service.report.task.FetchDailyLeaveApplicationsTask;
import com.company.hrms.attendance.application.service.report.task.FetchDailyOvertimeApplicationsTask;
import com.company.hrms.attendance.application.service.report.task.FetchDailyReportEmployeesTask;
import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢考勤日報表服務實作
 */
@Slf4j
@Service("getDailyReportServiceImpl")
@RequiredArgsConstructor
public class GetDailyReportServiceImpl extends AbstractQueryService<GetDailyReportRequest, DailyReportResponse> {

    // Tasks
    private final FetchDailyReportEmployeesTask fetchEmployeesTask;
    private final FetchDailyAttendanceRecordsTask fetchRecordsTask;
    private final FetchDailyLeaveApplicationsTask fetchLeavesTask;
    private final FetchDailyOvertimeApplicationsTask fetchOvertimesTask;
    private final FetchAllShiftsTask fetchShiftsTask;
    private final AggregateDailyReportTask aggregateTask;

    @Override
    protected QueryGroup buildQuery(GetDailyReportRequest request, JWTModel currentUser) {
        // 日報表邏輯較複雜，主要靠 Pipeline 處理，QueryGroup 僅作基本的 Log 紀錄
        return null;
    }

    @Override
    protected DailyReportResponse executeQuery(QueryGroup query, GetDailyReportRequest request, JWTModel currentUser,
            String... args) throws Exception {
        log.info("執行日報表 Pipeline: organizationId={}, date={}", request.getOrganizationId(), request.getDate());

        DailyReportContext context = new DailyReportContext(request, currentUser);

        BusinessPipeline.start(context)
                .next(fetchEmployeesTask)
                .next(fetchShiftsTask)
                .next(fetchRecordsTask)
                .next(fetchLeavesTask)
                .next(fetchOvertimesTask)
                .next(aggregateTask)
                .execute();

        return context.getResponse();
    }
}
