package com.company.hrms.timesheet.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;

public interface ITimesheetEntryRepository {
    Page<TimesheetEntry> findAll(QueryGroup query, Pageable pageable);

    List<TimesheetEntry> findByTimesheetId(UUID timesheetId);
}
