package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.LeaveType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.model.valueobject.LeaveUnit;
import com.company.hrms.attendance.domain.model.valueobject.StatutoryLeaveType;
import com.company.hrms.attendance.domain.repository.ILeaveTypeRepository;
import com.company.hrms.attendance.infrastructure.po.LeaveTypePO;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 假別 Repository 實作
 * 繼承 BaseRepository 以獲得整合 Fluent-Query-Engine 的能力
 */
@Repository
public class LeaveTypeRepositoryImpl extends BaseRepository<LeaveTypePO, String> implements ILeaveTypeRepository {

    public LeaveTypeRepositoryImpl(JPAQueryFactory queryFactory) {
        super(queryFactory, LeaveTypePO.class);
    }

    @Override
    public Optional<LeaveType> findById(LeaveTypeId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Optional<LeaveType> findByCode(String code) {
        QueryGroup query = QueryBuilder.where()
                .and("code", Operator.EQ, code)
                .and("isDeleted", Operator.EQ, 0)
                .build();
        return super.findOne(query).map(this::toDomain);
    }

    @Override
    public List<LeaveType> findAll() {
        QueryGroup query = QueryBuilder.where()
                .and("isDeleted", Operator.EQ, 0)
                .build();
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveType> findByQuery(QueryGroup query) {
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(LeaveType leaveType) {
        LeaveTypePO po = toPO(leaveType);
        Optional<LeaveTypePO> existing = super.findById(po.getId());

        if (existing.isPresent()) {
            po.setCreatedAt(existing.get().getCreatedAt()); // 保留建立時間
            po.setUpdatedAt(LocalDateTime.now());
            super.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            super.save(po);
        }
    }

    @Override
    public void delete(LeaveTypeId id) {
        // 軟刪除邏輯
        Optional<LeaveTypePO> poOpt = super.findById(id.getValue());
        poOpt.ifPresent(po -> {
            po.setIsDeleted(1);
            po.setUpdatedAt(LocalDateTime.now());
            super.save(po);
        });
    }

    private LeaveType toDomain(LeaveTypePO po) {
        return LeaveType.reconstitute(
                new LeaveTypeId(po.getId()),
                po.getOrganizationId(),
                po.getName(),
                po.getCode(),
                LeaveUnit.valueOf(po.getUnit()),
                po.getIsPaid(),
                po.getPayRate(),
                po.getIsActive(),
                po.getIsStatutoryLeave(),
                po.getStatutoryType() != null ? StatutoryLeaveType.valueOf(po.getStatutoryType()) : null,
                po.getRequiresProof(),
                po.getProofDescription(),
                po.getMaxDaysPerYear(),
                po.getCanCarryover());
    }

    private LeaveTypePO toPO(LeaveType leaveType) {
        return LeaveTypePO.builder()
                .id(leaveType.getId().getValue())
                .organizationId(leaveType.getOrganizationId())
                .name(leaveType.getName())
                .code(leaveType.getCode())
                .unit(leaveType.getUnit().name())
                .isPaid(leaveType.isPaid())
                .payRate(leaveType.getPayRate())
                .isActive(leaveType.isActive())
                .isStatutoryLeave(leaveType.isStatutoryLeave())
                .statutoryType(leaveType.getStatutoryType() != null ? leaveType.getStatutoryType().name() : null)
                .requiresProof(leaveType.isRequiresProof())
                .proofDescription(leaveType.getProofDescription())
                .maxDaysPerYear(leaveType.getMaxDaysPerYear())
                .canCarryover(leaveType.isCanCarryover())
                .isDeleted(0)
                .build();
    }
}
