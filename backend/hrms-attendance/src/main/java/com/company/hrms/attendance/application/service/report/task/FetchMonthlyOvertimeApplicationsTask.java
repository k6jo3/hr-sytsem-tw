package com.company.hrms.attendance.application.service.report.task;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.report.context.MonthlyReportContext;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;

/**
 * 獲取月度加班申請 Task
 */
@Component
@RequiredArgsConstructor
public class FetchMonthlyOvertimeApplicationsTask implements PipelineTask<MonthlyReportContext> {

    private final IOvertimeApplicationRepository overtimeRepository;

    @Override
    public void execute(MonthlyReportContext context) throws Exception {
        int year = context.getRequest().getYear();
        int month = context.getRequest().getMonth();

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth());

        // 查詢該月份的加班
        QueryGroup query = QueryBuilder.where()
                .and("overtimeDate", Operator.GTE, firstDay)
                .and("overtimeDate", Operator.LTE, lastDay)
                .and("status", Operator.EQ, "APPROVED")
                .build();

        List<OvertimeApplication> overtimes = overtimeRepository.findByQuery(query);
        context.setOvertimeApplications(overtimes);

        // 以員工 ID 分組
        context.setEmployeeOvertimesMap(overtimes.stream()
                .collect(Collectors.groupingBy(OvertimeApplication::getEmployeeId)));
    }
}
