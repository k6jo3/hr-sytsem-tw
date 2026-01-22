package com.company.hrms.recruitment.application.task.job;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.UpdateJobOpeningContext;
import com.company.hrms.recruitment.application.dto.job.UpdateJobOpeningRequest;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;
import com.company.hrms.recruitment.domain.model.valueobject.EmploymentType;
import com.company.hrms.recruitment.domain.model.valueobject.SalaryRange;

@Component
public class UpdateJobOpeningTask implements PipelineTask<UpdateJobOpeningContext> {

    @Override
    public void execute(UpdateJobOpeningContext context) throws Exception {
        JobOpening job = context.getJobOpening();
        UpdateJobOpeningRequest req = context.getRequest();

        if (req.getNumberOfPositions() != null) {
            job.updateNumberOfPositions(req.getNumberOfPositions());
        }

        if (req.getMinSalary() != null || req.getMaxSalary() != null) {
            // Need to merge with existing if only one provided?
            // Or assume Request provides full range if updating?
            // Assuming partial update if provided.
            SalaryRange current = job.getSalaryRange();
            BigDecimal min = req.getMinSalary() != null ? req.getMinSalary()
                    : (current != null ? current.getMin() : BigDecimal.ZERO);
            BigDecimal max = req.getMaxSalary() != null ? req.getMaxSalary()
                    : (current != null ? current.getMax() : BigDecimal.ZERO);
            String currency = req.getCurrency() != null ? req.getCurrency()
                    : (current != null ? current.getCurrency() : "TWD");

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
                // ignore
            }
        }
    }
}
