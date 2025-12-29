package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.organization.domain.model.entity.WorkExperience;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.model.valueobject.ExperienceId;
import com.company.hrms.organization.domain.repository.IWorkExperienceRepository;
import com.company.hrms.organization.infrastructure.dao.WorkExperienceDAO;
import com.company.hrms.organization.infrastructure.po.WorkExperiencePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WorkExperienceRepositoryImpl implements IWorkExperienceRepository {

    private final WorkExperienceDAO workExperienceDAO;

    @Override
    public void save(WorkExperience workExperience) {
        WorkExperiencePO po = toPO(workExperience);
        if (workExperienceDAO.existsById(po.getId())) {
            workExperienceDAO.update(po);
        } else {
            workExperienceDAO.insert(po);
        }
    }

    @Override
    public Optional<WorkExperience> findById(ExperienceId id) {
        return workExperienceDAO.findById(id.getValue().toString())
                .map(this::toDomain);
    }

    @Override
    public Optional<WorkExperience> findById(UUID id) {
        return workExperienceDAO.findById(id.toString())
                .map(this::toDomain);
    }

    @Override
    public List<WorkExperience> findByEmployeeId(UUID employeeId) {
        return workExperienceDAO.findByEmployeeId(employeeId.toString())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        workExperienceDAO.deleteById(id.toString());
    }

    @Override
    public void deleteByEmployeeId(UUID employeeId) {
        workExperienceDAO.deleteByEmployeeId(employeeId.toString());
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

    private WorkExperiencePO toPO(WorkExperience entity) {
        WorkExperiencePO po = new WorkExperiencePO();
        po.setId(entity.getId().getValue().toString());
        po.setEmployeeId(entity.getEmployeeId().toString());
        po.setCompanyName(entity.getCompanyName());
        po.setJobTitle(entity.getJobTitle());
        po.setStartDate(entity.getStartDate());
        po.setEndDate(entity.getEndDate());
        po.setDescription(entity.getDescription());
        return po;
    }
}
