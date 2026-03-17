package com.company.hrms.attendance.application.service.checkin.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.application.service.checkin.context.AttendanceContext;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateCheckInRecordTask implements PipelineTask<AttendanceContext> {

    private final IShiftRepository shiftRepository;

    @Override
    public void execute(AttendanceContext context) throws Exception {
        var request = context.getCheckInRequest();
        String employeeId = request.getEmployeeId();
        LocalDateTime checkInTime = request.getCheckInTime() != null
                ? request.getCheckInTime()
                : LocalDateTime.now();
        LocalDate today = checkInTime.toLocalDate();

        log.debug("建立打卡記錄: employeeId={}, time={}", employeeId, checkInTime);

        // Get default shift
        Shift shift = shiftRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new DomainException("SHIFT_NOT_FOUND", "找不到班別設定，請先建立班別資料"));

        // Create AttendanceRecord
        AttendanceRecord record = new AttendanceRecord(
                new RecordId(java.util.UUID.randomUUID().toString()),
                employeeId,
                today);

        // Perform check-in
        record.checkIn(checkInTime, shift);

        context.setRecord(record);
        context.setShift(shift);

        log.info("打卡記錄建立成功: recordId={}, isLate={}",
                record.getId().getValue(), record.isLate());
    }

    @Override
    public String getName() {
        return "建立打卡記錄";
    }

    @Override
    public boolean shouldExecute(AttendanceContext context) {
        return context.getCheckInRequest() != null;
    }
}
