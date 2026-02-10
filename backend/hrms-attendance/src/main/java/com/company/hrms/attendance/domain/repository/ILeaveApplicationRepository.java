package com.company.hrms.attendance.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.common.query.QueryGroup;

public interface ILeaveApplicationRepository {
    void save(LeaveApplication application);

    Optional<LeaveApplication> findById(ApplicationId id);

    List<LeaveApplication> findByEmployeeId(String employeeId);

    List<LeaveApplication> findByStatus(ApplicationStatus status);

    List<LeaveApplication> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate);

    List<LeaveApplication> findByDateRange(LocalDate startDate, LocalDate endDate);

    List<LeaveApplication> findByQuery(QueryGroup query);

    Page<LeaveApplication> searchPage(QueryGroup query, Pageable pageable);

    void delete(ApplicationId id);
}
