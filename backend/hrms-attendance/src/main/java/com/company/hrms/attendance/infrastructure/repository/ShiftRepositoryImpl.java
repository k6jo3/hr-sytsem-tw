package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftType;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.attendance.infrastructure.po.ShiftPO;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 班別 Repository 實作
 */
@Repository
public class ShiftRepositoryImpl extends BaseRepository<ShiftPO, String> implements IShiftRepository {

    public ShiftRepositoryImpl(JPAQueryFactory queryFactory) {
        super(queryFactory, ShiftPO.class);
    }

    @Override
    public Optional<Shift> findById(ShiftId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<Shift> findAll() {
        QueryGroup query = QueryBuilder.where()
                .and("isDeleted", Operator.EQ, 0)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<Shift> findByQuery(QueryGroup query) {
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Shift shift) {
        ShiftPO po = toPO(shift);
        Optional<ShiftPO> existing = super.findById(po.getId());

        if (existing.isPresent()) {
            po.setCreatedAt(existing.get().getCreatedAt());
            po.setUpdatedAt(LocalDateTime.now());
            super.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            super.save(po);
        }
    }

    @Override
    public void delete(ShiftId id) {
        Optional<ShiftPO> poOpt = super.findById(id.getValue());
        poOpt.ifPresent(po -> {
            po.setIsDeleted(1);
            po.setUpdatedAt(LocalDateTime.now());
            super.update(po);
        });
    }

    private Shift toDomain(ShiftPO po) {
        return Shift.reconstitute(
                new ShiftId(po.getId()),
                po.getOrganizationId(),
                po.getName(),
                ShiftType.valueOf(po.getType()),
                LocalTime.parse(po.getStartTime()),
                LocalTime.parse(po.getEndTime()),
                po.getBreakStartTime() != null ? LocalTime.parse(po.getBreakStartTime()) : null,
                po.getBreakEndTime() != null ? LocalTime.parse(po.getBreakEndTime()) : null,
                po.getLateToleranceMinutes() != null ? po.getLateToleranceMinutes() : 0,
                po.getEarlyLeaveToleranceMinutes() != null ? po.getEarlyLeaveToleranceMinutes() : 0,
                po.getIsActive() != null && po.getIsActive() == 1,
                po.getIsDeleted() != null && po.getIsDeleted() == 1);
    }

    private ShiftPO toPO(Shift shift) {
        return ShiftPO.builder()
                .id(shift.getId().getValue())
                .organizationId(shift.getOrganizationId())
                .name(shift.getName())
                .type(shift.getType().name())
                .startTime(shift.getWorkStartTime().toString())
                .endTime(shift.getWorkEndTime().toString())
                .breakStartTime(shift.getBreakStartTime() != null ? shift.getBreakStartTime().toString() : null)
                .breakEndTime(shift.getBreakEndTime() != null ? shift.getBreakEndTime().toString() : null)
                .lateToleranceMinutes(shift.getLateToleranceMinutes())
                .earlyLeaveToleranceMinutes(shift.getEarlyLeaveToleranceMinutes())
                .isActive(shift.isActive() ? 1 : 0)
                .isDeleted(shift.isDeleted() ? 1 : 0)
                .build();
    }
}
