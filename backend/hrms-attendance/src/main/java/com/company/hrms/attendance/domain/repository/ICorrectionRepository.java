package com.company.hrms.attendance.domain.repository;

import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.CorrectionApplication;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionId;

/**
 * 補卡申請 Repository 介面
 */
public interface ICorrectionRepository {
    void save(CorrectionApplication application);

    Optional<CorrectionApplication> findById(CorrectionId id);

    java.util.List<com.company.hrms.attendance.domain.model.aggregate.CorrectionApplication> findByQuery(
            com.company.hrms.common.query.QueryGroup query);
}
