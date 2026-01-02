package com.company.hrms.timesheet.domain.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;

public interface ITimesheetRepository {
    Timesheet save(Timesheet timesheet);

    Optional<Timesheet> findById(TimesheetId id);

    Page<Timesheet> findAll(QueryGroup query, Pageable pageable);

    /**
     * 查詢員工指定週的工時表
     */
    Optional<Timesheet> findByEmployeeAndWeek(UUID employeeId, LocalDate weekStartDate);

    /**
     * 查詢員工指定日期的工時表 (用於跨週/日報檢查)
     * 實際上 Timesheet 是週報，所以是查詢包含該日期的 Timesheet
     */
    Optional<Timesheet> findByEmployeeAndDate(UUID employeeId, LocalDate date);

    /**
     * 查詢待審核工時表
     */
    Page<Timesheet> findPendingApprovals(UUID approverId, Pageable pageable);
}
