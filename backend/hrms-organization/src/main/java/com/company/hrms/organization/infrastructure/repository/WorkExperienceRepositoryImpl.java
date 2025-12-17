package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.organization.domain.model.entity.WorkExperience;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.IWorkExperienceRepository;
import com.company.hrms.organization.infrastructure.dao.WorkExperienceDAO;
import com.company.hrms.organization.infrastructure.po.WorkExperiencePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 工作經歷倉儲實作
 */
@Repository
@RequiredArgsConstructor
public class WorkExperienceRepositoryImpl implements IWorkExperienceRepository {

    private final WorkExperienceDAO workExperienceDAO;

    @Override
    public Optional<WorkExperience> findById(ExperienceId id) {
        return workExperienceDAO.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<WorkExperience> findByEmployeeId(EmployeeId employeeId) {
        return workExperienceDAO.findByEmployeeId(employeeId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(WorkExperience experience) {
        WorkExperiencePO po = toPO(experience);
        if (workExperienceDAO.existsById(experience.getId().getValue())) {
            po.setUpdatedAt(LocalDateTime.now());
            workExperienceDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            workExperienceDAO.insert(po);
        }
    }

    @Override
    public void delete(ExperienceId id) {
        workExperienceDAO.deleteById(id.getValue());
    }

    @Override
    public void deleteByEmployeeId(EmployeeId employeeId) {
        workExperienceDAO.deleteByEmployeeId(employeeId.getValue());
    }

    @Override
    public boolean existsById(ExperienceId id) {
        return workExperienceDAO.existsById(id.getValue());
    }

    private WorkExperience toDomain(WorkExperiencePO po) {
        return WorkExperience.reconstitute(
                new ExperienceId(po.getId()),
                new EmployeeId(po.getEmployeeId()),
                po.getCompanyName(),
                po.getJobTitle(),
                po.getStartDate(),
                po.getEndDate(),
                po.getDescription()
        );
    }

    private WorkExperiencePO toPO(WorkExperience experience) {
        WorkExperiencePO po = new WorkExperiencePO();
        po.setId(experience.getId().getValue());
        po.setEmployeeId(experience.getEmployeeId().getValue());
        po.setCompanyName(experience.getCompanyName());
        po.setJobTitle(experience.getJobTitle());
        po.setStartDate(experience.getStartDate());
        po.setEndDate(experience.getEndDate());
        po.setDescription(experience.getDescription());
        return po;
    }
}
