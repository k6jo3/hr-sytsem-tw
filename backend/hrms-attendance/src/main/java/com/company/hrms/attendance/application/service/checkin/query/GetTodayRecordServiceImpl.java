package com.company.hrms.attendance.application.service.checkin.query;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.company.hrms.attendance.api.controller.attendance.HR03CheckInQryController.GetTodayRecordRequest;
import com.company.hrms.attendance.api.response.checkin.AttendanceRecordListResponse;
import com.company.hrms.attendance.api.response.checkin.TodayRecordResponse;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.service.QueryApiService;

import lombok.RequiredArgsConstructor;

/**
 * 取得今日打卡資訊服務實作
 */
@Service("getTodayRecordServiceImpl")
@RequiredArgsConstructor
public class GetTodayRecordServiceImpl implements QueryApiService<GetTodayRecordRequest, TodayRecordResponse> {

        private final IAttendanceRecordRepository attendanceRecordRepository;
        private final IShiftRepository shiftRepository;

        @Override
        public TodayRecordResponse getResponse(GetTodayRecordRequest request, JWTModel currentUser, String... args)
                        throws Exception {
                String employeeId = currentUser.getEmployeeId();
                LocalDate today = LocalDate.now();

                // 查詢今日記錄
                var query = QueryBuilder.where()
                                .and("employeeId", Operator.EQ, employeeId)
                                .and("recordDate", Operator.EQ, today)
                                .build();

                List<AttendanceRecord> records = attendanceRecordRepository.findByQuery(query);

                if (records.isEmpty()) {
                        return TodayRecordResponse.builder()
                                        .records(Collections.emptyList())
                                        .hasCheckedIn(false)
                                        .hasCheckedOut(false)
                                        .totalWorkHours(0.0)
                                        .build();
                }

                AttendanceRecord record = records.get(0);
                String shiftName = "預設班別";
                if (record.getShiftId() != null) {
                        shiftName = shiftRepository
                                        .findById(new ShiftId(record.getShiftId()))
                                        .map(Shift::getName)
                                        .orElse("未知班別");
                }

                AttendanceRecordListResponse item = AttendanceRecordListResponse.builder()
                                .recordId(record.getId().getValue())
                                .employeeId(record.getEmployeeId())
                                .attendanceDate(record.getDate())
                                .checkInTime(record.getCheckInTime())
                                .checkOutTime(record.getCheckOutTime())
                                .status(record.getAnomalyType().name())
                                .lateMinutes(record.getLateMinutes())
                                .earlyLeaveMinutes(record.getEarlyLeaveMinutes())
                                .shiftName(shiftName)
                                .build();

                Double totalHours = 0.0;
                if (record.getCheckInTime() != null && record.getCheckOutTime() != null) {
                        Duration duration = Duration.between(record.getCheckInTime(), record.getCheckOutTime());
                        totalHours = duration.toMinutes() / 60.0;
                        // 簡單處理，保留一位小數
                        totalHours = Math.round(totalHours * 10.0) / 10.0;
                }

                return TodayRecordResponse.builder()
                                .records(List.of(item))
                                .hasCheckedIn(record.getCheckInTime() != null)
                                .hasCheckedOut(record.getCheckOutTime() != null)
                                .totalWorkHours(totalHours)
                                .shiftName(item.getShiftName())
                                .build();
        }
}
