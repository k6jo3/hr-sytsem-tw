package com.company.hrms.attendance.application.service.checkin.query;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.company.hrms.attendance.api.request.attendance.GetAttendanceListRequest;
import com.company.hrms.attendance.api.response.checkin.AttendanceRecordListResponse;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;

import lombok.RequiredArgsConstructor;

/**
 * 查詢出勤記錄列表服務
 */
@Service("getAttendanceRecordsServiceImpl")
@RequiredArgsConstructor
public class GetAttendanceRecordsServiceImpl
        implements QueryApiService<GetAttendanceListRequest, PageResponse<AttendanceRecordListResponse>> {

    private final IAttendanceRecordRepository attendanceRecordRepository;

    @Override
    public PageResponse<AttendanceRecordListResponse> getResponse(GetAttendanceListRequest request,
            JWTModel currentUser, String... args) throws Exception {
        QueryBuilder builder = QueryBuilder.where();

        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()) {
            builder.and("employeeId", Operator.EQ, request.getEmployeeId());
        }

        if (request.getStartDate() != null) {
            builder.and("recordDate", Operator.GTE, request.getStartDate());
        }

        if (request.getEndDate() != null) {
            builder.and("recordDate", Operator.LTE, request.getEndDate());
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            builder.and("status", Operator.EQ, request.getStatus());
        }

        QueryGroup query = builder.build();

        // 目前 Repository findByQuery 不支援分頁，先手動處理
        var allRecords = attendanceRecordRepository.findByQuery(query);
        long total = allRecords.size();

        int page = request.getPage() != null ? request.getPage() : 1;
        int size = request.getSize() != null ? request.getSize() : 20;

        List<AttendanceRecordListResponse> items = allRecords.stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .map(record -> AttendanceRecordListResponse.builder()
                        .recordId(record.getId().getValue())
                        .employeeId(record.getEmployeeId())
                        .attendanceDate(record.getDate())
                        .checkInTime(record.getCheckInTime())
                        .checkOutTime(record.getCheckOutTime())
                        .status(record.getAnomalyType().name())
                        .lateMinutes(record.getLateMinutes())
                        .earlyLeaveMinutes(record.getEarlyLeaveMinutes())
                        .employeeName("Test Employee")
                        .employeeNumber("EMP001")
                        .shiftName("標準班別")
                        .build())
                .collect(Collectors.toList());

        return PageResponse.of(items, page, size, total);
    }
}
