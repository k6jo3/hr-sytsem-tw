package com.company.hrms.attendance.application.service.checkin.query;

import org.springframework.stereotype.Service;

import com.company.hrms.attendance.api.controller.attendance.HR03CheckInQryController.GetAttendanceRecordRequest;
import com.company.hrms.attendance.api.response.checkin.AttendanceRecordDetailResponse;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;

import lombok.RequiredArgsConstructor;

/**
 * 查詢單筆出勤記錄服務
 */
@Service("getAttendanceRecordServiceImpl")
@RequiredArgsConstructor
public class GetAttendanceRecordServiceImpl
        implements QueryApiService<GetAttendanceRecordRequest, AttendanceRecordDetailResponse> {

    private final IAttendanceRecordRepository attendanceRecordRepository;

    @Override
    public AttendanceRecordDetailResponse getResponse(GetAttendanceRecordRequest request, JWTModel currentUser,
            String... args) throws Exception {
        if (args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Record ID is required");
        }

        String recordId = args[0];

        var record = attendanceRecordRepository.findById(new RecordId(recordId))
                .orElseThrow(() -> new EntityNotFoundException("ATTENDANCE_RECORD", recordId));

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
                // TODO: 這裡應該要有關聯員工詳情與班別資訊的邏輯
                .employeeName("Test Employee")
                .employeeNumber("EMP001")
                .shiftName("標準班別")
                .isCorrected(record.isCorrected())
                .build();
    }
}
