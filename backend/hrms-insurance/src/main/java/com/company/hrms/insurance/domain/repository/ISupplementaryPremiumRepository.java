package com.company.hrms.insurance.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.insurance.domain.model.aggregate.SupplementaryPremium;
import com.company.hrms.insurance.domain.model.valueobject.PremiumId;

/**
 * 補充保費Repository介面
 */
public interface ISupplementaryPremiumRepository {

    /**
     * 儲存補充保費記錄
     */
    SupplementaryPremium save(SupplementaryPremium premium);

    /**
     * 根據ID查詢
     */
    Optional<SupplementaryPremium> findById(PremiumId id);

    /**
     * 根據員工ID查詢所有補充保費記錄
     */
    List<SupplementaryPremium> findByEmployeeId(String employeeId);

    /**
     * 根據員工ID和年度查詢補充保費記錄
     */
    List<SupplementaryPremium> findByEmployeeIdAndYear(String employeeId, int year);
}
