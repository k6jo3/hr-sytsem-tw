package com.company.hrms.organization.domain.repository;

import com.company.hrms.organization.domain.model.entity.WorkExperience;
import com.company.hrms.organization.domain.model.valueobject.ExperienceId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 工作經歷 Repository 介面
 */
public interface IWorkExperienceRepository {

    /**
     * 依 ID 查詢
     * @param id 經歷 ID
     * @return 工作經歷
     */
    Optional<WorkExperience> findById(ExperienceId id);

    /**
     * 依 ID 查詢
     * @param id 經歷 ID
     * @return 工作經歷
     */
    Optional<WorkExperience> findById(UUID id);

    /**
     * 依員工 ID 查詢工作經歷
     * @param employeeId 員工 ID
     * @return 工作經歷列表
     */
    List<WorkExperience> findByEmployeeId(UUID employeeId);

    /**
     * 儲存工作經歷
     * @param experience 工作經歷
     */
    void save(WorkExperience experience);

    /**
     * 刪除工作經歷
     * @param id 經歷 ID
     */
    void deleteById(UUID id);

    /**
     * 刪除員工的所有工作經歷
     * @param employeeId 員工 ID
     */
    void deleteByEmployeeId(UUID employeeId);
}
