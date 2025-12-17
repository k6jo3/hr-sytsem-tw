package com.company.hrms.organization.infrastructure.dao;

import com.company.hrms.organization.infrastructure.mapper.WorkExperienceMapper;
import com.company.hrms.organization.infrastructure.po.WorkExperiencePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 工作經歷 DAO
 */
@Repository
@RequiredArgsConstructor
public class WorkExperienceDAO {

    private final WorkExperienceMapper workExperienceMapper;

    public Optional<WorkExperiencePO> findById(String id) {
        return Optional.ofNullable(workExperienceMapper.selectById(id));
    }

    public List<WorkExperiencePO> findByEmployeeId(String employeeId) {
        return workExperienceMapper.selectByEmployeeId(employeeId);
    }

    public void insert(WorkExperiencePO experience) {
        workExperienceMapper.insert(experience);
    }

    public void update(WorkExperiencePO experience) {
        workExperienceMapper.update(experience);
    }

    public void deleteById(String id) {
        workExperienceMapper.deleteById(id);
    }

    public void deleteByEmployeeId(String employeeId) {
        workExperienceMapper.deleteByEmployeeId(employeeId);
    }

    public boolean existsById(String id) {
        return workExperienceMapper.existsById(id);
    }
}
