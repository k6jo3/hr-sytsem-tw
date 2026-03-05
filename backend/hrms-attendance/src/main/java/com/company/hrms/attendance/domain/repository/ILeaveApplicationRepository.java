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

    /**
     * 檢查指定假別是否有指定狀態的請假申請存在
     */
    boolean existsByLeaveTypeIdAndStatusIn(String leaveTypeId, List<ApplicationStatus> statuses);
    void save(LeaveApplication application);

    Optional<LeaveApplication> findById(ApplicationId id);

    List<LeaveApplication> findByEmployeeId(String employeeId);

    List<LeaveApplication> findByStatus(ApplicationStatus status);

    List<LeaveApplication> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate);

    List<LeaveApplication> findByDateRange(LocalDate startDate, LocalDate endDate);

    List<LeaveApplication> findByQuery(QueryGroup query);

    Page<LeaveApplication> searchPage(QueryGroup query, Pageable pageable);

    void delete(ApplicationId id);

    /**
     * 查詢指定日期已核准請假的員工 ID 清單
     */
    List<String> findEmployeeIdsWithApprovedLeaveOnDate(LocalDate date);
}
