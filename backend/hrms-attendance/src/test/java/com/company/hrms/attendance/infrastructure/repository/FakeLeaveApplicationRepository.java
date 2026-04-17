package com.company.hrms.attendance.infrastructure.repository;

import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ILeaveApplicationRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 */
public class FakeLeaveApplicationRepository
        extends InMemoryBaseRepository<LeaveApplication, ApplicationId>
        implements ILeaveApplicationRepository {

    public FakeLeaveApplicationRepository() {
        super(LeaveApplication.class, LeaveApplication::getId);
    }

    @Override
    public boolean existsByLeaveTypeIdAndStatusIn(String leaveTypeId, List<ApplicationStatus> statuses) {
        return findAll().stream()
                .filter(a -> leaveTypeId.equals(a.getLeaveTypeId().getValue()))
                .anyMatch(a -> statuses.contains(a.getStatus()));
    }

    @Override
    public Optional<LeaveApplication> findById(ApplicationId id) {
        return super.findById(id);
    }

    @Override
    public List<LeaveApplication> findByEmployeeId(String employeeId) {
        return findByField("employeeId", employeeId);
    }

    @Override
    public List<LeaveApplication> findByStatus(ApplicationStatus status) {
        return findByField("status", status);
    }

    @Override
    public List<LeaveApplication> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate) {
        return findAll().stream()
                .filter(a -> employeeId.equals(a.getEmployeeId()))
                .filter(a -> !a.getStartDate().isAfter(endDate) && !a.getEndDate().isBefore(startDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveApplication> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return findAll().stream()
                .filter(a -> !a.getStartDate().isAfter(endDate) && !a.getEndDate().isBefore(startDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveApplication> findByQuery(QueryGroup query) {
        return findAll(query);
    }

    @Override
    public Page<LeaveApplication> searchPage(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }

    @Override
    public void delete(ApplicationId id) {
        super.deleteById(id);
    }

    @Override
    public List<String> findEmployeeIdsWithApprovedLeaveOnDate(LocalDate date) {
        return findAll().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.APPROVED)
                .filter(a -> !a.getStartDate().isAfter(date) && !a.getEndDate().isBefore(date))
                .map(LeaveApplication::getEmployeeId)
                .distinct()
                .collect(Collectors.toList());
    }
@Override    public void save(LeaveApplication leaveApplication) {        doSave(leaveApplication);    }
}
