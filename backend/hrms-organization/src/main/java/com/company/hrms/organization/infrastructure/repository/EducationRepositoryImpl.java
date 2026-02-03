package com.company.hrms.organization.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.organization.domain.model.entity.Education;
import com.company.hrms.organization.domain.model.valueobject.Degree;
import com.company.hrms.organization.domain.model.valueobject.EducationId;
import com.company.hrms.organization.domain.repository.IEducationRepository;
import com.company.hrms.organization.infrastructure.dao.EducationDAO;
import com.company.hrms.organization.infrastructure.po.EducationPO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EducationRepositoryImpl implements IEducationRepository {

    private final EducationDAO educationDAO;

    @Override
    public void save(Education education) {
        EducationPO po = toPO(education);
        if (educationDAO.existsById(education.getId().getValue().toString())) {
            educationDAO.update(po);
        } else {
            educationDAO.insert(po);
        }
    }

    @Override
    public Optional<Education> findById(EducationId id) {
        return educationDAO.findById(id.getValue().toString())
                .map(this::toDomain);
    }

    @Override
    public Optional<Education> findById(UUID id) {
        return educationDAO.findById(id.toString())
                .map(this::toDomain);
    }

    @Override
    public List<Education> findByEmployeeId(UUID employeeId) {
        return educationDAO.findByEmployeeId(employeeId.toString())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Education> findHighestByEmployeeId(UUID employeeId) {
        return educationDAO.findHighestByEmployeeId(employeeId.toString())
                .map(this::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        educationDAO.deleteById(id.toString());
    }

    @Override
    public void deleteByEmployeeId(UUID employeeId) {
        educationDAO.deleteByEmployeeId(employeeId.toString());
    }

    private Education toDomain(EducationPO po) {
        return Education.builder()
                .id(new EducationId(UUID.fromString(po.getId())))
                .employeeId(UUID.fromString(po.getEmployeeId()))
                .degree(Degree.valueOf(po.getDegree()))
                .school(po.getSchoolName())
                .major(po.getMajor())
                .startDate(po.getStartDate())
                .endDate(po.getEndDate())
                .isHighestDegree(po.getIsGraduated() != null && po.getIsGraduated())
                .build();
    }

    private EducationPO toPO(Education entity) {
        EducationPO po = new EducationPO();
        po.setId(entity.getId().getValue().toString());
        po.setEmployeeId(entity.getEmployeeId().toString());
        po.setDegree(entity.getDegree().name());
        po.setSchoolName(entity.getSchool());
        po.setMajor(entity.getMajor());
        po.setStartDate(entity.getStartDate());
        po.setEndDate(entity.getEndDate());
        po.setIsGraduated(entity.isHighestDegree());
        return po;
    }
}
