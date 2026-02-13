package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeType;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;
import com.company.hrms.attendance.infrastructure.po.OvertimeApplicationPO;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class OvertimeApplicationRepositoryImpl extends BaseRepository<OvertimeApplicationPO, String>
        implements IOvertimeApplicationRepository {

    public OvertimeApplicationRepositoryImpl(JPAQueryFactory factory) {
        super(factory, OvertimeApplicationPO.class);
    }

    @Override
    public Optional<OvertimeApplication> findById(OvertimeId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<OvertimeApplication> findByEmployeeId(String employeeId) {
        QueryGroup query = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<OvertimeApplication> findByEmployeeIdAndMonth(String employeeId, int year, int month) {
        return super.findAll(QueryBuilder.where().and("employeeId", Operator.EQ, employeeId).build()).stream()
                .filter(po -> po.getOvertimeDate().getYear() == year && po.getOvertimeDate().getMonthValue() == month)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<OvertimeApplication> findByQuery(QueryGroup query) {
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(OvertimeApplication application) {
        OvertimeApplicationPO po = toPO(application);
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
    public void delete(OvertimeId id) {
        super.deleteById(id.getValue());
    }

    private OvertimeApplication toDomain(OvertimeApplicationPO po) {
        return OvertimeApplication.reconstitute(
                new OvertimeId(po.getId()),
                po.getEmployeeId(),
                po.getOvertimeDate(),
                po.getHours(),
                OvertimeType.valueOf(po.getOvertimeType()),
                po.getReason(),
                ApplicationStatus.valueOf(po.getStatus()),
                po.getRejectionReason());
    }

    private OvertimeApplicationPO toPO(OvertimeApplication application) {
        return OvertimeApplicationPO.builder()
                .id(application.getId().getValue())
                .employeeId(application.getEmployeeId())
                .overtimeDate(application.getOvertimeDate())
                .hours(application.getHours())
                .status(application.getStatus().name())
                .reason(application.getReason())
                .overtimeType(application.getOvertimeType().name())
                .rejectionReason(application.getRejectionReason())
                .isDeleted(0)
                .build();
    }
}
