package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.organization.domain.model.entity.Education;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.IEducationRepository;
import com.company.hrms.organization.infrastructure.dao.EducationDAO;
import com.company.hrms.organization.infrastructure.po.EducationPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 學歷倉儲實作
 */
@Repository
@RequiredArgsConstructor
public class EducationRepositoryImpl implements IEducationRepository {

    private final EducationDAO educationDAO;

    @Override
    public Optional<Education> findById(EducationId id) {
        return educationDAO.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<Education> findByEmployeeId(EmployeeId employeeId) {
        return educationDAO.findByEmployeeId(employeeId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Education education) {
        EducationPO po = toPO(education);
        if (educationDAO.existsById(education.getId().getValue())) {
            po.setUpdatedAt(LocalDateTime.now());
            educationDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            educationDAO.insert(po);
        }
    }

    @Override
    public void delete(EducationId id) {
        educationDAO.deleteById(id.getValue());
    }

    @Override
    public void deleteByEmployeeId(EmployeeId employeeId) {
        educationDAO.deleteByEmployeeId(employeeId.getValue());
    }

    @Override
    public boolean existsById(EducationId id) {
        return educationDAO.existsById(id.getValue());
    }

    private Education toDomain(EducationPO po) {
        return Education.reconstitute(
                new EducationId(po.getId()),
                new EmployeeId(po.getEmployeeId()),
                po.getSchoolName(),
                Degree.valueOf(po.getDegree()),
                po.getMajor(),
                po.getStartDate(),
                po.getEndDate(),
                po.getIsGraduated()
        );
    }

    private EducationPO toPO(Education education) {
        EducationPO po = new EducationPO();
        po.setId(education.getId().getValue());
        po.setEmployeeId(education.getEmployeeId().getValue());
        po.setSchoolName(education.getSchoolName());
        po.setDegree(education.getDegree().name());
        po.setMajor(education.getMajor());
        po.setStartDate(education.getStartDate());
        po.setEndDate(education.getEndDate());
        po.setIsGraduated(education.isGraduated());
        return po;
    }
}
