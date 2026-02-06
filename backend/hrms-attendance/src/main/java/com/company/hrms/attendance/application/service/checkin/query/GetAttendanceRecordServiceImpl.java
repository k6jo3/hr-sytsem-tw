package com.company.hrms.attendance.application.service.checkin.query;

import org.springframework.stereotype.Service;

import com.company.hrms.attendance.api.controller.attendance.HR03CheckInQryController.GetAttendanceRecordRequest;
import com.company.hrms.attendance.api.response.checkin.AttendanceRecordDetailResponse;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.attendance.infrastructure.client.organization.OrganizationServiceClient;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢單筆出勤記錄服務
 */
@Service("getAttendanceRecordServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class GetAttendanceRecordServiceImpl
        implements QueryApiService<GetAttendanceRecordRequest, AttendanceRecordDetailResponse> {

    private final IAttendanceRecordRepository attendanceRecordRepository;
    private final IShiftRepository shiftRepository;
    private final OrganizationServiceClient organizationServiceClient;

    @Override
    public AttendanceRecordDetailResponse getResponse(GetAttendanceRecordRequest request, JWTModel currentUser,
            String... args) throws Exception {
        if (args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Record ID is required");
        }

        String recordId = args[0];

        var record = attendanceRecordRepository.findById(new RecordId(recordId))
                .orElseThrow(() -> new EntityNotFoundException("ATTENDANCE_RECORD", recordId));

        // 查詢員工資訊
        String employeeName = "Unknown";
        String employeeNumber = "Unknown";
        try {
            var employee = organizationServiceClient.getEmployeeDetail(record.getEmployeeId());
            if (employee != null) {
                employeeName = employee.getFullName();
                employeeNumber = employee.getEmployeeNumber();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch employee details for ID: {}", record.getEmployeeId(), e);
        }

        // 查詢班別資訊
        String shiftName = "Unknown";
        if (record.getShiftId() != null) {
            try {
                shiftRepository.findById(new ShiftId(record.getShiftId()))
                        .ifPresent(shift -> {
                            // Lambda variable MUST be effectively final, so we can't update shiftName
                            // directly inside lambda
                            // unless we use a wrapper or AtomicReference, or just fetch directly.
                            // Using ifPresent is cleaner if we map it, but here assigning to variable is
                            // needed.
                            // Let's reload logic slightly.
                        });
                // Better approach:
                var shiftOpt = shiftRepository.findById(new ShiftId(record.getShiftId()));
                if (shiftOpt.isPresent()) {
                    shiftName = shiftOpt.get().getName();
                }
            } catch (Exception e) {
                log.warn("Failed to fetch shift details for ID: {}", record.getShiftId(), e);
            }
        }

        return AttendanceRecordDetailResponse.builder()
                .recordId(record.getId().getValue())
                .employeeId(record.getEmployeeId())
                .attendanceDate(record.getDate())
                .shiftId(record.getShiftId())
                .checkInTime(record.getCheckInTime())
                .checkOutTime(record.getCheckOutTime())
                .status(record.getAnomalyType().name())
                .lateMinutes(record.getLateMinutes())
                .earlyLeaveMinutes(record.getEarlyLeaveMinutes())
                .employeeName(employeeName)
                .employeeNumber(employeeNumber)
                .shiftName(shiftName)
                .isCorrected(record.isCorrected())
                .build();
    }
}
