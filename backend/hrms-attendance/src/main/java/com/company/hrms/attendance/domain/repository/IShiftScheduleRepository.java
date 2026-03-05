package com.company.hrms.attendance.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.ShiftSchedule;
import com.company.hrms.attendance.domain.model.valueobject.ScheduleId;
import com.company.hrms.common.query.QueryGroup;

/**
 * 排班表 Repository
 */
public interface IShiftScheduleRepository {

    void save(ShiftSchedule schedule);

    Optional<ShiftSchedule> findById(ScheduleId id);

    List<ShiftSchedule> findByQuery(QueryGroup query);

    /**
     * 查詢員工在指定日期範圍的排班
     */
    List<ShiftSchedule> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * 查詢某日期的所有排班
     */
    List<ShiftSchedule> findByDate(LocalDate date);

    void delete(ScheduleId id);
}
