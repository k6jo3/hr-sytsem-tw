package com.company.hrms.attendance.application.service.report.task;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.report.context.DailyReportContext;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;

/**
 * 獲取當日請假申請 Task
 */
@Component
@RequiredArgsConstructor
public class FetchDailyLeaveApplicationsTask implements PipelineTask<DailyReportContext> {

    private final ILeaveApplicationRepository leaveRepository;

    @Override
    public void execute(DailyReportContext context) throws Exception {
        LocalDate date = context.getRequest().getDate();

        // 查詢該日期範圍內的請假 (start_date <= date AND end_date >= date)
        QueryGroup query = QueryBuilder.where()
                .and("startDate", Operator.LTE, date)
                .and("endDate", Operator.GTE, date)
                .and("status", Operator.EQ, "APPROVED") // 只統計已核准的
                .build();

        List<LeaveApplication> leaves = leaveRepository.findByQuery(query);
        context.setLeaveApplications(leaves);

        // 以員工 ID 分組
        context.setEmployeeLeavesMap(leaves.stream()
                .collect(Collectors.groupingBy(LeaveApplication::getEmployeeId)));
    }
}
