package com.company.hrms.organization.domain.repository;

import com.company.hrms.organization.domain.model.entity.Education;
import com.company.hrms.organization.domain.model.valueobject.EducationId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 學歷 Repository 介面
 */
public interface IEducationRepository {

    /**
     * 依 ID 查詢
     * @param id 學歷 ID
     * @return 學歷
     */
    Optional<Education> findById(EducationId id);

    /**
     * 依 ID 查詢
     * @param id 學歷 ID
     * @return 學歷
     */
    Optional<Education> findById(UUID id);

    /**
     * 依員工 ID 查詢學歷
     * @param employeeId 員工 ID
     * @return 學歷列表
     */
    List<Education> findByEmployeeId(UUID employeeId);

    /**
     * 查詢員工的最高學歷
     * @param employeeId 員工 ID
     * @return 最高學歷
     */
    Optional<Education> findHighestByEmployeeId(UUID employeeId);

    /**
     * 儲存學歷
     * @param education 學歷
     */
    void save(Education education);

    /**
     * 刪除學歷
     * @param id 學歷 ID
     */
    void deleteById(UUID id);

    /**
     * 刪除員工的所有學歷
     * @param employeeId 員工 ID
     */
    void deleteByEmployeeId(UUID employeeId);
}
