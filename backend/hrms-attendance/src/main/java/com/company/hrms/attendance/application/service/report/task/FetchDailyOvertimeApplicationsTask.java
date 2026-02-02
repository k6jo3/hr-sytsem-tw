package com.company.hrms.attendance.application.service.report.task;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.report.context.DailyReportContext;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;

/**
 * 獲取當日加班申請 Task
 */
@Component
@RequiredArgsConstructor
public class FetchDailyOvertimeApplicationsTask implements PipelineTask<DailyReportContext> {

    private final IOvertimeApplicationRepository overtimeRepository;

    @Override
    public void execute(DailyReportContext context) throws Exception {
        LocalDate date = context.getRequest().getDate();

        // 查詢該日期的加班
        QueryGroup query = QueryBuilder.where()
                .and("date", Operator.EQ, date)
                .and("status", Operator.EQ, "APPROVED") // 只統計已核准的
                .build();

        List<OvertimeApplication> overtimes = overtimeRepository.findByQuery(query);
        context.setOvertimeApplications(overtimes);

        // 以員工 ID 分組
        context.setEmployeeOvertimesMap(overtimes.stream()
                .collect(Collectors.groupingBy(OvertimeApplication::getEmployeeId)));
    }
}
