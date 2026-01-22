package com.company.hrms.recruitment.application.task.job;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.CreateJobOpeningContext;
import com.company.hrms.recruitment.application.dto.job.CreateJobOpeningRequest;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;
import com.company.hrms.recruitment.domain.model.valueobject.EmploymentType;
import com.company.hrms.recruitment.domain.model.valueobject.SalaryRange;
import com.company.hrms.recruitment.domain.repository.IJobOpeningRepository;

@Component
public class SaveJobOpeningTask implements PipelineTask<CreateJobOpeningContext> {

    private final IJobOpeningRepository repository;

    public SaveJobOpeningTask(IJobOpeningRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(CreateJobOpeningContext context) throws Exception {
        CreateJobOpeningRequest req = context.getRequest();

        UUID deptId = UUID.fromString(req.getDepartmentId());

        JobOpening job = JobOpening.create(
                req.getJobTitle(),
                deptId,
                req.getNumberOfPositions());

        // Set Optional Fields
        if (req.getMinSalary() != null || req.getMaxSalary() != null) {
            BigDecimal min = req.getMinSalary() != null ? req.getMinSalary() : BigDecimal.ZERO;
            BigDecimal max = req.getMaxSalary() != null ? req.getMaxSalary() : BigDecimal.ZERO;
            String currency = req.getCurrency() != null ? req.getCurrency() : "TWD";
            job.setSalaryRange(SalaryRange.of(min, max, currency));
        }

        if (req.getRequirements() != null)
            job.setRequirements(req.getRequirements());
        if (req.getResponsibilities() != null)
            job.setResponsibilities(req.getResponsibilities());
        if (req.getWorkLocation() != null)
            job.setWorkLocation(req.getWorkLocation());

        if (req.getEmploymentType() != null) {
            try {
                job.setEmploymentType(EmploymentType.valueOf(req.getEmploymentType()));
            } catch (IllegalArgumentException e) {
                // warning or default
            }
        }

        repository.save(job);
        context.setJobOpening(job);
    }
}
