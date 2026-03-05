package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.ShiftSchedule;
import com.company.hrms.attendance.domain.model.valueobject.ScheduleId;
import com.company.hrms.attendance.domain.model.valueobject.ScheduleStatus;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.repository.IShiftScheduleRepository;
import com.company.hrms.attendance.infrastructure.po.ShiftSchedulePO;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 排班表 Repository 實作
 */
@Repository
public class ShiftScheduleRepositoryImpl extends BaseRepository<ShiftSchedulePO, String>
        implements IShiftScheduleRepository {

    public ShiftScheduleRepositoryImpl(JPAQueryFactory queryFactory) {
        super(queryFactory, ShiftSchedulePO.class);
    }

    @Override
    public Optional<ShiftSchedule> findById(ScheduleId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<ShiftSchedule> findByQuery(QueryGroup query) {
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftSchedule> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate) {
        QueryGroup query = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .and("scheduleDate", Operator.GTE, startDate.toString())
                .and("scheduleDate", Operator.LTE, endDate.toString())
                .and("isDeleted", Operator.EQ, 0)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<ShiftSchedule> findByDate(LocalDate date) {
        QueryGroup query = QueryBuilder.where()
                .and("scheduleDate", Operator.EQ, date.toString())
                .and("isDeleted", Operator.EQ, 0)
                .build();
        return findByQuery(query);
    }

    @Override
    public void save(ShiftSchedule schedule) {
        ShiftSchedulePO po = toPO(schedule);
        Optional<ShiftSchedulePO> existing = super.findById(po.getId());

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
    public void delete(ScheduleId id) {
        Optional<ShiftSchedulePO> poOpt = super.findById(id.getValue());
        poOpt.ifPresent(po -> {
            po.setIsDeleted(1);
            po.setUpdatedAt(LocalDateTime.now());
            super.update(po);
        });
    }

    private ShiftSchedule toDomain(ShiftSchedulePO po) {
        return ShiftSchedule.reconstitute(
                new ScheduleId(po.getId()),
                po.getEmployeeId(),
                new ShiftId(po.getShiftId()),
                po.getScheduleDate(),
                ScheduleStatus.valueOf(po.getStatus()),
                po.getRotationPatternId(),
                po.getNote(),
                po.getIsDeleted() != null && po.getIsDeleted() == 1);
    }

    private ShiftSchedulePO toPO(ShiftSchedule schedule) {
        return ShiftSchedulePO.builder()
                .id(schedule.getId().getValue())
                .employeeId(schedule.getEmployeeId())
                .shiftId(schedule.getShiftId().getValue())
                .scheduleDate(schedule.getScheduleDate())
                .status(schedule.getStatus().name())
                .rotationPatternId(schedule.getRotationPatternId())
                .note(schedule.getNote())
                .isDeleted(schedule.isDeleted() ? 1 : 0)
                .build();
    }
}
