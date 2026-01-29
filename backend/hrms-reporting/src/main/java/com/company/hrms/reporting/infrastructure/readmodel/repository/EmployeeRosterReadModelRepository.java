package com.company.hrms.reporting.infrastructure.readmodel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;

/**
 * 員工花名冊讀模型 Repository
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Repository
public interface EmployeeRosterReadModelRepository
        extends JpaRepository<EmployeeRosterReadModel, String>,
        JpaSpecificationExecutor<EmployeeRosterReadModel> {

    // Spring Data JPA 會自動提供基本的 CRUD 操作與 Specification 查詢
}
