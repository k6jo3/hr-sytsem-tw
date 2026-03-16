package com.company.hrms.insurance.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBatchBaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.repository.IInsuranceLevelRepository;
import com.company.hrms.insurance.infrastructure.entity.InsuranceLevelEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 投保薪資分級Repository實作
 */
@Repository
public class InsuranceLevelRepository extends CommandBatchBaseRepository<InsuranceLevelEntity, UUID>
        implements IInsuranceLevelRepository {

    public InsuranceLevelRepository(JPAQueryFactory factory) {
        super(factory, InsuranceLevelEntity.class);
    }

    @Override
    public Optional<InsuranceLevel> findById(LevelId id) {
        return super.findById(UUID.fromString(id.getValue()))
                .map(this::toDomain);
    }

    @Override
    public List<InsuranceLevel> findByType(InsuranceType type) {
        QueryGroup query = QueryGroup.and()
                .eq("insuranceType", type)
                .eq("isActive", true);
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<InsuranceLevel> findByTypeAndActiveOn(InsuranceType type, LocalDate date) {
        QueryGroup query = QueryGroup.and()
                .eq("insuranceType", type)
                .eq("isActive", true)
                .lte("effectiveDate", date);

        return super.findAll(query).stream()
                .filter(e -> e.getEndDate() == null || !date.isAfter(e.getEndDate()))
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<InsuranceLevel> findByTypeAndLevelNumber(InsuranceType type, int levelNumber) {
        QueryGroup query = QueryGroup.and()
                .eq("insuranceType", type)
                .eq("levelNumber", levelNumber)
                .eq("isActive", true);
        return super.findOne(query).map(this::toDomain);
    }

    @Override
    public List<InsuranceLevel> findAllActive(LocalDate date) {
        QueryGroup query = QueryGroup.and()
                .eq("isActive", true)
                .lte("effectiveDate", date);

        return super.findAll(query).stream()
                .filter(e -> e.getEndDate() == null || !date.isAfter(e.getEndDate()))
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<InsuranceLevel> findByTypeAndEndDateIsNull(InsuranceType type) {
        QueryGroup query = QueryGroup.and()
                .eq("insuranceType", type)
                .eq("isActive", true)
                .isNull("endDate");
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public InsuranceLevel save(InsuranceLevel level) {
        InsuranceLevelEntity entity = toEntity(level);
        super.save(entity);
        return level;
    }

    @Override
    public InsuranceLevel update(InsuranceLevel level) {
        InsuranceLevelEntity entity = toEntity(level);
        super.update(entity);
        return level;
    }

    @Override
    public void saveBatch(List<InsuranceLevel> levels) {
        List<InsuranceLevelEntity> entities = levels.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        super.saveAll(entities);
    }

    // ==================== Domain -> Entity 轉換 ====================

    private InsuranceLevelEntity toEntity(InsuranceLevel level) {
        return InsuranceLevelEntity.builder()
                .levelId(UUID.fromString(level.getId().getValue()))
                .insuranceType(level.getInsuranceType())
                .levelNumber(level.getLevelNumber())
                .monthlySalary(level.getMonthlySalary())
                .laborEmployeeRate(level.getLaborEmployeeRate())
                .laborEmployerRate(level.getLaborEmployerRate())
                .healthEmployeeRate(level.getHealthEmployeeRate())
                .healthEmployerRate(level.getHealthEmployerRate())
                .pensionEmployerRate(level.getPensionEmployerRate())
                .effectiveDate(level.getEffectiveDate())
                .endDate(level.getEndDate())
                .isActive(level.isActive())
                .build();
    }

    // ==================== Entity -> Domain 轉換 ====================

    private InsuranceLevel toDomain(InsuranceLevelEntity entity) {
        InsuranceLevel level = new InsuranceLevel(
                new LevelId(entity.getLevelId().toString()),
                entity.getInsuranceType(),
                entity.getLevelNumber(),
                entity.getMonthlySalary(),
                entity.getEffectiveDate());

        if (entity.getLaborEmployeeRate() != null && entity.getLaborEmployerRate() != null) {
            level.setLaborRates(entity.getLaborEmployeeRate(), entity.getLaborEmployerRate());
        }
        if (entity.getHealthEmployeeRate() != null && entity.getHealthEmployerRate() != null) {
            level.setHealthRates(entity.getHealthEmployeeRate(), entity.getHealthEmployerRate());
        }
        if (entity.getPensionEmployerRate() != null) {
            level.setPensionEmployerRate(entity.getPensionEmployerRate());
        }
        if (entity.getEndDate() != null) {
            level.setEndDate(entity.getEndDate());
        }

        return level;
    }
}
