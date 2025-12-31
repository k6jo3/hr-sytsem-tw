package com.company.hrms.attendance.application.service.checkout.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.checkout.context.CheckOutContext;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 驗證下班打卡 Task
 * - 驗證今日是否已有上班打卡記錄
 * - 驗證今日是否已完成下班打卡
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateCheckOutTask implements PipelineTask<CheckOutContext> {

    private final IAttendanceRecordRepository attendanceRecordRepository;

    @Override
    public void execute(CheckOutContext context) throws Exception {
        var request = context.getCheckOutRequest();
        String employeeId = request.getEmployeeId();
        LocalDateTime checkOutTime = request.getCheckOutTime() != null
                ? request.getCheckOutTime()
                : LocalDateTime.now();
        LocalDate today = checkOutTime.toLocalDate();

        log.debug("驗證下班打卡: employeeId={}, date={}", employeeId, today);

        // Check if has check-in record today
        var records = attendanceRecordRepository.findByEmployeeIdAndDate(employeeId, today);
        if (records.isEmpty()) {
            throw new IllegalStateException("今日尚未上班打卡");
        }

        AttendanceRecord record = records.get(0);
        if (record.getCheckOutTime() != null) {
            throw new IllegalStateException("今日已完成下班打卡");
        }

        context.setRecord(record);
        log.info("下班打卡驗證通過: employeeId={}, recordId={}", employeeId, record.getId().getValue());
    }

    @Override
    public String getName() {
        return "驗證下班打卡";
    }

    @Override
    public boolean shouldExecute(CheckOutContext context) {
        return context.getCheckOutRequest() != null;
    }
}
