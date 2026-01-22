package com.company.hrms.recruitment.application.service;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.dto.job.JobOpeningDetailResponse;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.company.hrms.recruitment.domain.repository.IJobOpeningRepository;

import lombok.RequiredArgsConstructor;

@Service("getJobOpeningServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetJobOpeningServiceImpl implements QueryApiService<String, JobOpeningDetailResponse> {

    private final IJobOpeningRepository repository;

    @Override
    public JobOpeningDetailResponse getResponse(String id, JWTModel currentUser, String... args) throws Exception {
        JobOpening job = repository.findById(OpeningId.of(UUID.fromString(id)))
                .orElseThrow(() -> new NoSuchElementException("Job Opening not found: " + id));

        return JobOpeningDetailResponse.builder()
                .id(job.getId().toString())
                .title(job.getJobTitle())
                .departmentId(job.getDepartmentId().toString())
                .numberOfPositions(job.getNumberOfPositions())
                .status(job.getStatus().name())
                .minSalary(job.getSalaryRange() != null ? job.getSalaryRange().getMin() : null)
                .maxSalary(job.getSalaryRange() != null ? job.getSalaryRange().getMax() : null)
                .currency(job.getSalaryRange() != null ? job.getSalaryRange().getCurrency() : null)
                .requirements(job.getRequirements())
                .responsibilities(job.getResponsibilities())
                .employmentType(job.getEmploymentType() != null ? job.getEmploymentType().name() : null)
                .workLocation(job.getWorkLocation())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
