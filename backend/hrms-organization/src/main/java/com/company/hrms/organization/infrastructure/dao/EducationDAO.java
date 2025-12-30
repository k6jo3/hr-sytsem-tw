package com.company.hrms.organization.infrastructure.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.organization.infrastructure.mapper.EducationMapper;
import com.company.hrms.organization.infrastructure.po.EducationPO;

import lombok.RequiredArgsConstructor;

/**
 * 學歷 DAO
 */
@Repository
@RequiredArgsConstructor
public class EducationDAO {

    private final EducationMapper educationMapper;

    public Optional<EducationPO> findById(String id) {
        return Optional.ofNullable(educationMapper.selectById(id));
    }

    public List<EducationPO> findByEmployeeId(String employeeId) {
        return educationMapper.selectByEmployeeId(employeeId);
    }

    public List<EducationPO> findByDegree(String degree) {
        return educationMapper.selectByDegree(degree);
    }

    public void insert(EducationPO education) {
        educationMapper.insert(education);
    }

    public void update(EducationPO education) {
        educationMapper.update(education);
    }

    public void deleteById(String id) {
        educationMapper.deleteById(id);
    }

    public void deleteByEmployeeId(String employeeId) {
        educationMapper.deleteByEmployeeId(employeeId);
    }

    public boolean existsById(String id) {
        return educationMapper.existsById(id);
    }

    public Optional<EducationPO> findHighestByEmployeeId(String employeeId) {
        return Optional.ofNullable(educationMapper.selectHighestByEmployeeId(employeeId));
    }
}
