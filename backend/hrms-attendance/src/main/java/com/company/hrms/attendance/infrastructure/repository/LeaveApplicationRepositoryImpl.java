package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.LeavePeriodType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.attendance.infrastructure.dao.LeaveApplicationDAO;
import com.company.hrms.attendance.infrastructure.po.LeaveApplicationPO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LeaveApplicationRepositoryImpl implements ILeaveApplicationRepository {

    private final LeaveApplicationDAO leaveApplicationDAO;

    @Override
    public Optional<LeaveApplication> findById(ApplicationId id) {
        return leaveApplicationDAO.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<LeaveApplication> findByEmployeeId(String employeeId) {
        return leaveApplicationDAO.findByEmployeeId(employeeId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveApplication> findByStatus(ApplicationStatus status) {
        return leaveApplicationDAO.findByStatus(status.name()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveApplication> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate,
            LocalDate endDate) {
        return leaveApplicationDAO.findByEmployeeIdAndDateRange(employeeId, startDate, endDate).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(LeaveApplication application) {
        LeaveApplicationPO po = toPO(application);
        if (leaveApplicationDAO.findById(po.getId()).isPresent()) {
            po.setUpdatedAt(LocalDateTime.now());
            leaveApplicationDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            leaveApplicationDAO.insert(po);
        }
    }

    @Override
    public void delete(ApplicationId id) {
        leaveApplicationDAO.deleteById(id.getValue());
    }

    private LeaveApplication toDomain(LeaveApplicationPO po) {
        return LeaveApplication.reconstitute(
                new ApplicationId(po.getId()),
                po.getEmployeeId(),
                new LeaveTypeId(po.getLeaveTypeId()),
                po.getStartDate(),
                po.getEndDate(),
                po.getStartPeriod() != null ? LeavePeriodType.valueOf(po.getStartPeriod()) : null,
                po.getEndPeriod() != null ? LeavePeriodType.valueOf(po.getEndPeriod()) : null,
                po.getReason(),
                po.getProofAttachmentUrl(),
                ApplicationStatus.valueOf(po.getStatus()),
                po.getRejectionReason());
    }

    private LeaveApplicationPO toPO(LeaveApplication application) {
        LeaveApplicationPO po = new LeaveApplicationPO();
        po.setId(application.getId().getValue());
        po.setEmployeeId(application.getEmployeeId());
        po.setLeaveTypeId(application.getLeaveTypeId().getValue());
        po.setStartDate(application.getStartDate());
        po.setEndDate(application.getEndDate());
        po.setStatus(application.getStatus().name());
        po.setReason(application.getReason());
        po.setStartPeriod(application.getStartPeriod() != null ? application.getStartPeriod().name() : null);
        po.setEndPeriod(application.getEndPeriod() != null ? application.getEndPeriod().name() : null);
        po.setProofAttachmentUrl(application.getProofAttachmentUrl());
        po.setRejectionReason(application.getRejectionReason());
        return po;
    }
}
