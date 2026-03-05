package com.company.hrms.insurance.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.insurance.domain.model.aggregate.GroupInsurancePlan;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;

/**
 * 團體保險方案 Repository 介面
 */
public interface IGroupInsurancePlanRepository {

    void save(GroupInsurancePlan plan);

    Optional<GroupInsurancePlan> findById(String planId);

    Optional<GroupInsurancePlan> findByPlanCode(String planCode);

    List<GroupInsurancePlan> findByOrganizationId(String organizationId);

    List<GroupInsurancePlan> findActiveByOrganizationIdAndType(
            String organizationId, InsuranceType insuranceType);
}
