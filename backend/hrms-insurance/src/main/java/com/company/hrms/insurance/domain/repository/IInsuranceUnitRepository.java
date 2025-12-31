package com.company.hrms.insurance.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.insurance.domain.model.aggregate.InsuranceUnit;
import com.company.hrms.insurance.domain.model.valueobject.UnitId;

/**
 * 投保單位Repository介面
 */
public interface IInsuranceUnitRepository {

    /**
     * 儲存投保單位
     */
    InsuranceUnit save(InsuranceUnit unit);

    /**
     * 根據ID查詢
     */
    Optional<InsuranceUnit> findById(UnitId id);

    /**
     * 根據組織ID查詢有效的投保單位
     */
    List<InsuranceUnit> findActiveByOrganizationId(String organizationId);

    /**
     * 根據單位代號查詢
     */
    Optional<InsuranceUnit> findByUnitCode(String unitCode);
}
