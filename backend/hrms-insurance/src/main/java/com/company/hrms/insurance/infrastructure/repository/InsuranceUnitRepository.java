package com.company.hrms.insurance.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceUnit;
import com.company.hrms.insurance.domain.model.valueobject.UnitId;
import com.company.hrms.insurance.domain.repository.IInsuranceUnitRepository;
import com.company.hrms.insurance.infrastructure.entity.InsuranceUnitEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 投保單位Repository實作
 */
@Repository
public class InsuranceUnitRepository
        extends BaseRepository<InsuranceUnitEntity, UUID>
        implements IInsuranceUnitRepository {

    public InsuranceUnitRepository(JPAQueryFactory factory) {
        super(factory, InsuranceUnitEntity.class);
    }

    @Override
    public InsuranceUnit save(InsuranceUnit unit) {
        InsuranceUnitEntity entity = toEntity(unit);
        super.save(entity);
        return unit;
    }

    @Override
    public Optional<InsuranceUnit> findById(UnitId id) {
        return super.findById(UUID.fromString(id.getValue()))
                .map(this::toDomain);
    }

    @Override
    public List<InsuranceUnit> findActiveByOrganizationId(String organizationId) {
        QueryGroup query = QueryGroup.and()
                .eq("organizationId", organizationId)
                .eq("isActive", true);
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<InsuranceUnit> findByUnitCode(String unitCode) {
        QueryGroup query = QueryGroup.and().eq("unitCode", unitCode);
        return super.findOne(query).map(this::toDomain);
    }

    // ==================== Entity <-> Domain 轉換 ====================

    private InsuranceUnitEntity toEntity(InsuranceUnit domain) {
        return InsuranceUnitEntity.builder()
                .unitId(UUID.fromString(domain.getId().getValue()))
                .organizationId(domain.getOrganizationId())
                .unitCode(domain.getUnitCode())
                .unitName(domain.getUnitName())
                .laborInsuranceNumber(domain.getLaborInsuranceNumber())
                .healthInsuranceNumber(domain.getHealthInsuranceNumber())
                .pensionNumber(domain.getPensionNumber())
                .isActive(domain.isActive())
                .build();
    }

    private InsuranceUnit toDomain(InsuranceUnitEntity entity) {
        InsuranceUnit unit = new InsuranceUnit(
                new UnitId(entity.getUnitId().toString()),
                entity.getOrganizationId(),
                entity.getUnitCode(),
                entity.getUnitName());

        unit.setLaborInsuranceNumber(entity.getLaborInsuranceNumber());
        unit.setHealthInsuranceNumber(entity.getHealthInsuranceNumber());
        unit.setPensionNumber(entity.getPensionNumber());

        if (!entity.getIsActive()) {
            unit.deactivate();
        }

        return unit;
    }
}
