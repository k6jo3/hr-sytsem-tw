package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.organization.domain.model.entity.Education;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.IEducationRepository;
import com.company.hrms.organization.infrastructure.dao.EducationDAO;
import com.company.hrms.organization.infrastructure.po.EducationPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EducationRepositoryImpl implements IEducationRepository {

    private final EducationDAO educationDAO;

    @Override
    public void save(Education education) {
        EducationPO po = toPO(education);
        if (educationDAO.existsById(po.getId())) {
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
    public List<Education> findByEmployeeId(UUID employeeId) {
        return educationDAO.findByEmployeeId(employeeId.toString())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(EducationId id) {
        educationDAO.deleteById(id.getValue().toString());
    }

    @Override
    public void deleteByEmployeeId(UUID employeeId) {
        educationDAO.deleteByEmployeeId(employeeId.toString());
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

    private EducationPO toPO(Education entity) {
        EducationPO po = new EducationPO();
        po.setId(entity.getId().getValue().toString());
        po.setEmployeeId(entity.getEmployeeId().toString());
        po.setDegree(entity.getDegree().name());
        po.setSchoolName(entity.getSchoolName());
        po.setMajor(entity.getMajor());
        po.setStartDate(entity.getStartDate());
        po.setEndDate(entity.getEndDate());
        po.setIsGraduated(entity.isGraduated());
        return po;
    }
}
