package com.company.hrms.insurance.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.insurance.domain.model.aggregate.GroupInsurancePlan;
import com.company.hrms.insurance.domain.model.entity.PlanTier;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.repository.IGroupInsurancePlanRepository;
import com.company.hrms.insurance.infrastructure.entity.GroupInsurancePlanEntity;
import com.company.hrms.insurance.infrastructure.entity.PlanTierEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * 團體保險方案 Repository 實作
 * 使用 EntityManager 直接操作，避免與 BaseRepository 的 findById 回傳型別衝突
 */
@Repository
public class GroupInsurancePlanRepositoryImpl implements IGroupInsurancePlanRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(GroupInsurancePlan plan) {
        GroupInsurancePlanEntity entity = toEntity(plan);
        entityManager.merge(entity);

        // 儲存所有 Tier（先刪除再重建，確保資料一致）
        entityManager.createQuery("DELETE FROM PlanTierEntity t WHERE t.planId = :planId")
                .setParameter("planId", plan.getPlanId())
                .executeUpdate();
        entityManager.flush();
        entityManager.clear(); // 清除 persistence context，避免刪除後的 stale 參照影響重新插入

        for (PlanTier tier : plan.getTiers()) {
            PlanTierEntity tierEntity = toTierEntity(tier, plan.getPlanId());
            entityManager.persist(tierEntity);
        }
    }

    @Override
    public Optional<GroupInsurancePlan> findById(String planId) {
        GroupInsurancePlanEntity entity = entityManager.find(GroupInsurancePlanEntity.class, planId);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<GroupInsurancePlan> findByPlanCode(String planCode) {
        List<GroupInsurancePlanEntity> results = entityManager
                .createQuery("SELECT p FROM GroupInsurancePlanEntity p WHERE p.planCode = :planCode",
                        GroupInsurancePlanEntity.class)
                .setParameter("planCode", planCode)
                .getResultList();
        return results.stream().findFirst().map(this::toDomain);
    }

    @Override
    public List<GroupInsurancePlan> findByOrganizationId(String organizationId) {
        return entityManager
                .createQuery("SELECT p FROM GroupInsurancePlanEntity p WHERE p.organizationId = :orgId",
                        GroupInsurancePlanEntity.class)
                .setParameter("orgId", organizationId)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupInsurancePlan> findActiveByOrganizationIdAndType(
            String organizationId, InsuranceType insuranceType) {
        return entityManager
                .createQuery(
                        "SELECT p FROM GroupInsurancePlanEntity p WHERE p.organizationId = :orgId "
                                + "AND p.insuranceType = :type AND p.active = true",
                        GroupInsurancePlanEntity.class)
                .setParameter("orgId", organizationId)
                .setParameter("type", insuranceType.name())
                .getResultList()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ==================== Entity <-> Domain 轉換 ====================

    private GroupInsurancePlanEntity toEntity(GroupInsurancePlan domain) {
        return GroupInsurancePlanEntity.builder()
                .planId(domain.getPlanId())
                .organizationId(domain.getOrganizationId())
                .planName(domain.getPlanName())
                .planCode(domain.getPlanCode())
                .insuranceType(domain.getInsuranceType().name())
                .insurerName(domain.getInsurerName())
                .policyNumber(domain.getPolicyNumber())
                .contractStartDate(domain.getContractStartDate())
                .contractEndDate(domain.getContractEndDate())
                .active(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private GroupInsurancePlan toDomain(GroupInsurancePlanEntity entity) {
        // 查詢關聯的 Tier
        List<PlanTierEntity> tierEntities = entityManager
                .createQuery("SELECT t FROM PlanTierEntity t WHERE t.planId = :planId", PlanTierEntity.class)
                .setParameter("planId", entity.getPlanId())
                .getResultList();

        List<PlanTier> tiers = tierEntities.stream()
                .map(this::toTierDomain)
                .collect(Collectors.toList());

        return GroupInsurancePlan.builder()
                .planId(entity.getPlanId())
                .organizationId(entity.getOrganizationId())
                .planName(entity.getPlanName())
                .planCode(entity.getPlanCode())
                .insuranceType(InsuranceType.valueOf(entity.getInsuranceType()))
                .insurerName(entity.getInsurerName())
                .policyNumber(entity.getPolicyNumber())
                .contractStartDate(entity.getContractStartDate())
                .contractEndDate(entity.getContractEndDate())
                .active(entity.getActive() != null ? entity.getActive() : true)
                .tiers(new ArrayList<>(tiers))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private PlanTierEntity toTierEntity(PlanTier tier, String planId) {
        return PlanTierEntity.builder()
                .tierId(tier.getTierId())
                .planId(planId)
                .jobGrade(tier.getJobGrade())
                .coverageAmount(tier.getCoverageAmount())
                .monthlyPremium(tier.getMonthlyPremium())
                .employerShareRate(tier.getEmployerShareRate())
                .build();
    }

    private PlanTier toTierDomain(PlanTierEntity entity) {
        return PlanTier.builder()
                .tierId(entity.getTierId())
                .jobGrade(entity.getJobGrade())
                .coverageAmount(entity.getCoverageAmount())
                .monthlyPremium(entity.getMonthlyPremium())
                .employerShareRate(entity.getEmployerShareRate())
                .build();
    }
}
