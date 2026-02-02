package com.company.hrms.attendance.application.service.report.task;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.report.context.MonthlyReportContext;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;

/**
 * 獲取月度出勤記錄 Task
 */
@Component
@RequiredArgsConstructor
public class FetchMonthlyAttendanceRecordsTask implements PipelineTask<MonthlyReportContext> {

    private final IAttendanceRecordRepository recordRepository;

    @Override
    public void execute(MonthlyReportContext context) throws Exception {
        int year = context.getRequest().getYear();
        int month = context.getRequest().getMonth();

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth());

        // 查詢該月份的所有出勤記錄
        QueryGroup query = QueryBuilder.where()
                .and("date", Operator.GTE, firstDay)
                .and("date", Operator.LTE, lastDay)
                .build();

        List<AttendanceRecord> records = recordRepository.findByQuery(query);
        context.setAttendanceRecords(records);

        // 以員工 ID 分組
        context.setEmployeeRecordsMap(records.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getEmployeeId)));
    }
}
