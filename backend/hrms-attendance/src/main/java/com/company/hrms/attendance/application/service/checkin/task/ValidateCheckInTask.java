package com.company.hrms.attendance.application.service.checkin.task;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.checkin.context.AttendanceContext;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.ResourceAlreadyExistsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateCheckInTask implements PipelineTask<AttendanceContext> {

    private final IAttendanceRecordRepository attendanceRecordRepository;

    @Override
    public void execute(AttendanceContext context) throws Exception {
        String employeeId = context.getCheckInRequest().getEmployeeId();
        if (employeeId == null || employeeId.isBlank()) {
            throw new com.company.hrms.common.exception.ValidationException("EMPLOYEE_ID_REQUIRED", "員工 ID 為必填");
        }
        LocalDate today = LocalDate.now();

        log.debug("驗證打卡: employeeId={}, date={}", employeeId, today);

        // Check if already checked in today
        var existingRecords = attendanceRecordRepository.findByEmployeeIdAndDate(employeeId, today);
        if (!existingRecords.isEmpty() && existingRecords.get(0).getCheckInTime() != null) {
            throw new ResourceAlreadyExistsException("ATTENDANCE_RECORD_EXISTS", "今日已完成上班打卡");
        }

        log.info("打卡驗證通過: employeeId={}", employeeId);
    }

    @Override
    public String getName() {
        return "驗證打卡";
    }

    @Override
    public boolean shouldExecute(AttendanceContext context) {
        return context.getCheckInRequest() != null;
    }
}
