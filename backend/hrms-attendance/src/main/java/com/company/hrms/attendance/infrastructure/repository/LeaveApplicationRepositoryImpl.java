package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.LeavePeriodType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.attendance.infrastructure.po.LeaveApplicationPO;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class LeaveApplicationRepositoryImpl extends BaseRepository<LeaveApplicationPO, String>
        implements ILeaveApplicationRepository {

    public LeaveApplicationRepositoryImpl(JPAQueryFactory factory) {
        super(factory, LeaveApplicationPO.class);
    }

    @Override
    public Optional<LeaveApplication> findById(ApplicationId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<LeaveApplication> findByEmployeeId(String employeeId) {
        QueryGroup query = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<LeaveApplication> findByStatus(ApplicationStatus status) {
        QueryGroup query = QueryBuilder.where()
                .and("status", Operator.EQ, status.name())
                .build();
        return findByQuery(query);
    }

    @Override
    public List<LeaveApplication> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate,
            LocalDate endDate) {
        QueryGroup query = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .and("startDate", Operator.LTE, endDate)
                .and("endDate", Operator.GTE, startDate)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<LeaveApplication> findByDateRange(LocalDate startDate, LocalDate endDate) {
        QueryGroup query = QueryBuilder.where()
                .and("startDate", Operator.LTE, endDate)
                .and("endDate", Operator.GTE, startDate)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<LeaveApplication> findByQuery(QueryGroup query) {
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<LeaveApplication> searchPage(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    @Override
    public void save(LeaveApplication application) {
        LeaveApplicationPO po = toPO(application);
        if (super.findById(po.getId()).isPresent()) {
            po.setUpdatedAt(LocalDateTime.now());
            super.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            super.save(po);
        }
    }

    @Override
    public void delete(ApplicationId id) {
        super.deleteById(id.getValue());
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
        return LeaveApplicationPO.builder()
                .id(application.getId().getValue())
                .employeeId(application.getEmployeeId())
                .leaveTypeId(application.getLeaveTypeId().getValue())
                .startDate(application.getStartDate())
                .endDate(application.getEndDate())
                .startPeriod(application.getStartPeriod() != null ? application.getStartPeriod().name() : null)
                .endPeriod(application.getEndPeriod() != null ? application.getEndPeriod().name() : null)
                .status(application.getStatus().name())
                .reason(application.getReason())
                .proofAttachmentUrl(application.getProofAttachmentUrl())
                .rejectionReason(application.getRejectionReason())
                .build();
    }
}
