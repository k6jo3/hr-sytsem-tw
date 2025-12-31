package com.company.hrms.insurance.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;

/**
 * 投保級距Repository介面
 */
public interface IInsuranceLevelRepository {

    /**
     * 根據ID查詢
     */
    Optional<InsuranceLevel> findById(LevelId id);

    /**
     * 根據保險類型查詢所有有效級距
     */
    List<InsuranceLevel> findByType(InsuranceType type);

    /**
     * 根據保險類型和指定日期查詢有效級距
     */
    List<InsuranceLevel> findByTypeAndActiveOn(InsuranceType type, LocalDate date);

    /**
     * 根據保險類型和級距號碼查詢
     */
    Optional<InsuranceLevel> findByTypeAndLevelNumber(InsuranceType type, int levelNumber);

    /**
     * 查詢指定日期有效的所有級距
     */
    List<InsuranceLevel> findAllActive(LocalDate date);
}
