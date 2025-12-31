package com.company.hrms.insurance.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.insurance.domain.model.aggregate.SupplementaryPremium;
import com.company.hrms.insurance.domain.model.valueobject.PremiumId;
import com.company.hrms.insurance.domain.repository.ISupplementaryPremiumRepository;
import com.company.hrms.insurance.infrastructure.entity.SupplementaryPremiumEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 補充保費Repository實作
 */
@Repository
public class SupplementaryPremiumRepository
        extends BaseRepository<SupplementaryPremiumEntity, UUID>
        implements ISupplementaryPremiumRepository {

    public SupplementaryPremiumRepository(JPAQueryFactory factory) {
        super(factory, SupplementaryPremiumEntity.class);
    }

    @Override
    public SupplementaryPremium save(SupplementaryPremium premium) {
        SupplementaryPremiumEntity entity = toEntity(premium);
        super.save(entity);
        return premium;
    }

    @Override
    public Optional<SupplementaryPremium> findById(PremiumId id) {
        // SupplementaryPremium 不提供 ID 反查，暫時返回 empty
        // 若需要實作，需在 Domain 保留 ID 資訊
        return Optional.empty();
    }

    @Override
    public List<SupplementaryPremium> findByEmployeeId(String employeeId) {
        QueryGroup query = QueryGroup.and().eq("employeeId", employeeId);
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplementaryPremium> findByEmployeeIdAndYear(String employeeId, int year) {
        QueryGroup query = QueryGroup.and()
                .eq("employeeId", employeeId)
                .eq("year", year);
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ==================== Entity <-> Domain 轉換 ====================

    private SupplementaryPremiumEntity toEntity(SupplementaryPremium domain) {
        return SupplementaryPremiumEntity.builder()
                .premiumId(UUID.fromString(domain.getId().getValue()))
                .employeeId(domain.getEmployeeId())
                .incomeType(domain.getIncomeType())
                .incomeDate(domain.getIncomeDate())
                .incomeAmount(domain.getIncomeAmount())
                .insuredSalary(domain.getInsuredSalary())
                .threshold(domain.getThreshold())
                .premiumBase(domain.getPremiumBase())
                .premiumAmount(domain.getPremiumAmount())
                .year(domain.getYear())
                .month(domain.getMonth())
                .build();
    }

    private SupplementaryPremium toDomain(SupplementaryPremiumEntity entity) {
        // 使用 factory method 重新計算 (確保一致性)
        return SupplementaryPremium.calculate(
                entity.getEmployeeId(),
                entity.getIncomeType(),
                entity.getIncomeDate(),
                entity.getIncomeAmount(),
                entity.getInsuredSalary());
    }
}
