package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.RotationPattern;
import com.company.hrms.attendance.domain.model.aggregate.RotationPattern.RotationDay;
import com.company.hrms.attendance.domain.model.valueobject.RotationPatternId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.repository.IRotationPatternRepository;
import com.company.hrms.attendance.infrastructure.po.RotationDayPO;
import com.company.hrms.attendance.infrastructure.po.RotationPatternPO;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * 輪班模式 Repository 實作
 */
@Repository
public class RotationPatternRepositoryImpl extends BaseRepository<RotationPatternPO, String>
        implements IRotationPatternRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public RotationPatternRepositoryImpl(JPAQueryFactory queryFactory) {
        super(queryFactory, RotationPatternPO.class);
    }

    @Override
    public Optional<RotationPattern> findById(RotationPatternId id) {
        return super.findById(id.getValue()).map(po -> {
            List<RotationDayPO> dayPOs = findDaysByPatternId(id.getValue());
            return toDomain(po, dayPOs);
        });
    }

    @Override
    public List<RotationPattern> findAll() {
        QueryGroup query = QueryBuilder.where()
                .and("isDeleted", Operator.EQ, 0)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<RotationPattern> findByQuery(QueryGroup query) {
        return super.findAll(query).stream()
                .map(po -> {
                    List<RotationDayPO> dayPOs = findDaysByPatternId(po.getId());
                    return toDomain(po, dayPOs);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void save(RotationPattern pattern) {
        RotationPatternPO po = toPO(pattern);
        Optional<RotationPatternPO> existing = super.findById(po.getId());

        if (existing.isPresent()) {
            po.setCreatedAt(existing.get().getCreatedAt());
            po.setUpdatedAt(LocalDateTime.now());
            super.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            super.save(po);
        }

        // 儲存輪班天序：先刪除再新增
        deleteDaysByPatternId(pattern.getId().getValue());
        for (RotationDay day : pattern.getRotationDays()) {
            RotationDayPO dayPO = RotationDayPO.builder()
                    .id(UUID.randomUUID().toString())
                    .patternId(pattern.getId().getValue())
                    .dayOrder(day.getDayOrder())
                    .shiftId(day.getShiftId() != null ? day.getShiftId().getValue() : null)
                    .isRestDay(day.isRestDay())
                    .build();
            entityManager.merge(dayPO);
        }
    }

    @Override
    public void delete(RotationPatternId id) {
        Optional<RotationPatternPO> poOpt = super.findById(id.getValue());
        poOpt.ifPresent(po -> {
            po.setIsDeleted(1);
            po.setUpdatedAt(LocalDateTime.now());
            super.update(po);
        });
    }

    private List<RotationDayPO> findDaysByPatternId(String patternId) {
        try {
            return entityManager
                    .createQuery("SELECT d FROM RotationDayPO d WHERE d.patternId = :patternId ORDER BY d.dayOrder",
                            RotationDayPO.class)
                    .setParameter("patternId", patternId)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void deleteDaysByPatternId(String patternId) {
        try {
            entityManager
                    .createQuery("DELETE FROM RotationDayPO d WHERE d.patternId = :patternId")
                    .setParameter("patternId", patternId)
                    .executeUpdate();
        } catch (Exception e) {
            // 新建時無舊資料，忽略
        }
    }

    private RotationPattern toDomain(RotationPatternPO po, List<RotationDayPO> dayPOs) {
        List<RotationDay> days = dayPOs.stream()
                .map(dayPO -> {
                    if (Boolean.TRUE.equals(dayPO.getIsRestDay())) {
                        return RotationDay.restDay(dayPO.getDayOrder());
                    } else {
                        return RotationDay.workDay(dayPO.getDayOrder(),
                                new ShiftId(dayPO.getShiftId()));
                    }
                })
                .collect(Collectors.toList());

        return RotationPattern.reconstitute(
                new RotationPatternId(po.getId()),
                po.getOrganizationId(),
                po.getName(),
                po.getCode(),
                po.getCycleDays(),
                days,
                po.getIsActive() != null && po.getIsActive() == 1,
                po.getIsDeleted() != null && po.getIsDeleted() == 1);
    }

    private RotationPatternPO toPO(RotationPattern pattern) {
        return RotationPatternPO.builder()
                .id(pattern.getId().getValue())
                .organizationId(pattern.getOrganizationId())
                .name(pattern.getName())
                .code(pattern.getCode())
                .cycleDays(pattern.getCycleDays())
                .isActive(pattern.isActive() ? 1 : 0)
                .isDeleted(pattern.isDeleted() ? 1 : 0)
                .build();
    }
}
