package com.company.hrms.attendance.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.common.query.QueryGroup;

public interface IAttendanceRecordRepository {
    void save(AttendanceRecord record);

    Optional<AttendanceRecord> findById(RecordId id);

    List<AttendanceRecord> findByEmployeeIdAndDate(String employeeId, LocalDate date);

    List<AttendanceRecord> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate);

    List<AttendanceRecord> findByQuery(QueryGroup query);

    Page<AttendanceRecord> findPageByQuery(QueryGroup query, Pageable pageable);

    void delete(RecordId id);

    /**
     * 查詢指定日期已有打卡記錄的員工 ID 清單
     */
    List<String> findEmployeeIdsWithRecordOnDate(LocalDate date);
}
