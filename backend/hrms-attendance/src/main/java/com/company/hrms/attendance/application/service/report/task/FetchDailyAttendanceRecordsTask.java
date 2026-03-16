package com.company.hrms.attendance.application.service.report.task;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.report.context.DailyReportContext;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;

/**
 * 獲取當日出勤記錄 Task
 */
@Component
@RequiredArgsConstructor
public class FetchDailyAttendanceRecordsTask implements PipelineTask<DailyReportContext> {

    private final IAttendanceRecordRepository recordRepository;

    @Override
    public void execute(DailyReportContext context) throws Exception {
        LocalDate date = context.getRequest().getDate();

        // 查詢該日期的所有出勤記錄
        QueryGroup query = QueryBuilder.where()
                .and("recordDate", Operator.EQ, date)
                .build();

        List<AttendanceRecord> records = recordRepository.findByQuery(query);
        context.setAttendanceRecords(records);

        // 以員工 ID 分組，方便後續查詢
        context.setEmployeeRecordsMap(records.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getEmployeeId)));
    }
}
