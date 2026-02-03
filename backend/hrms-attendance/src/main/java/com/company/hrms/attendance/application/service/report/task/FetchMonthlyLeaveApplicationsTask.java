package com.company.hrms.attendance.application.service.report.task;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.report.context.MonthlyReportContext;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;

/**
 * 獲取月度請假申請 Task
 */
@Component
@RequiredArgsConstructor
public class FetchMonthlyLeaveApplicationsTask implements PipelineTask<MonthlyReportContext> {

    private final ILeaveApplicationRepository leaveRepository;

    @Override
    public void execute(MonthlyReportContext context) throws Exception {
        int year = context.getRequest().getYear();
        int month = context.getRequest().getMonth();

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth());

        // 查詢與該月份重疊的請假 (startDate <= lastDay AND endDate >= firstDay)
        QueryGroup query = QueryBuilder.where()
                .and("startDate", Operator.LTE, lastDay)
                .and("endDate", Operator.GTE, firstDay)
                .and("status", Operator.EQ, "APPROVED")
                .build();

        List<LeaveApplication> leaves = leaveRepository.findByQuery(query);
        context.setLeaveApplications(leaves);

        // 以員工 ID 分組
        context.setEmployeeLeavesMap(leaves.stream()
                .collect(Collectors.groupingBy(LeaveApplication::getEmployeeId)));
    }
}
