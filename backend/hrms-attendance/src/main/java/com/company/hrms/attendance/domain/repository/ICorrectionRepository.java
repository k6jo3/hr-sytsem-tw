package com.company.hrms.attendance.domain.repository;

import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.CorrectionApplication;

/**
 * 補卡申請 Repository 介面
 */
public interface ICorrectionRepository {
    void save(CorrectionApplication application);

    Optional<CorrectionApplication> findById(String id);
}
