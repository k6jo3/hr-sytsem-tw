package com.company.hrms.attendance.application.service.checkout;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.attendance.CheckOutRequest;
import com.company.hrms.attendance.api.response.attendance.CheckOutResponse;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 下班打卡服務實作
 */
@Service("checkOutServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CheckOutServiceImpl implements CommandApiService<CheckOutRequest, CheckOutResponse> {

    private final IAttendanceRecordRepository attendanceRecordRepository;
    private final IShiftRepository shiftRepository;

    @Override
    public CheckOutResponse execCommand(CheckOutRequest request, JWTModel currentUser, String... args)
            throws Exception {
        String employeeId = request.getEmployeeId();
        LocalDateTime checkOutTime = request.getCheckOutTime() != null
                ? request.getCheckOutTime()
                : LocalDateTime.now();
        LocalDate today = checkOutTime.toLocalDate();

        log.info("下班打卡流程開始: employeeId={}", employeeId);

        // Find today's record
        var records = attendanceRecordRepository.findByEmployeeIdAndDate(employeeId, today);
        if (records.isEmpty()) {
            throw new IllegalStateException("今日尚未上班打卡");
        }

        AttendanceRecord record = records.get(0);
        if (record.getCheckOutTime() != null) {
            throw new IllegalStateException("今日已完成下班打卡");
        }

        // Get shift
        Shift shift = shiftRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到班別設定"));

        // Perform check-out
        record.checkOut(checkOutTime, shift);
        attendanceRecordRepository.save(record);

        // Calculate working hours
        double workingHours = Duration.between(record.getCheckInTime(), checkOutTime).toMinutes() / 60.0;

        log.info("下班打卡流程完成: recordId={}", record.getId().getValue());

        return CheckOutResponse.success(
                record.getId().getValue(),
                record.getCheckInTime(),
                checkOutTime,
                workingHours,
                record.isEarlyLeave(),
                record.getEarlyLeaveMinutes());
    }
}
