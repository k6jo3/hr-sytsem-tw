package com.company.hrms.recruitment.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.company.hrms.recruitment.domain.model.valueobject.SalaryRange;
import com.company.hrms.recruitment.domain.repository.IJobOpeningRepository;
import com.company.hrms.recruitment.infrastructure.entity.JobOpeningEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 職缺 Repository 實作
 */
@Repository
public class JobOpeningRepositoryImpl
        extends CommandBaseRepository<JobOpeningEntity, UUID>
        implements IJobOpeningRepository {

    private final EventPublisher eventPublisher;

    public JobOpeningRepositoryImpl(
            JPAQueryFactory factory,
            EventPublisher eventPublisher) {
        super(factory, JobOpeningEntity.class);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public JobOpening save(JobOpening jobOpening) {
        JobOpeningEntity entity = toEntity(jobOpening);
        super.save(entity);

        // 發布 Domain Events
        jobOpening.getDomainEvents().forEach(eventPublisher::publish);
        jobOpening.clearDomainEvents();

        return jobOpening;
    }

    @Override
    public void delete(JobOpening jobOpening) {
        JobOpeningEntity entity = toEntity(jobOpening);
        super.delete(entity);
    }

    @Override
    public Optional<JobOpening> findById(OpeningId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Page<JobOpening> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    // ================= Entity 映射 =================

    private JobOpeningEntity toEntity(JobOpening domain) {
        JobOpeningEntity entity = new JobOpeningEntity();
        entity.setOpeningId(domain.getId().getValue());
        entity.setJobTitle(domain.getJobTitle());
        entity.setDepartmentId(domain.getDepartmentId());
        entity.setNumberOfPositions(domain.getNumberOfPositions());
        entity.setFilledPositions(domain.getFilledPositions());
        entity.setRequirements(domain.getRequirements());
        entity.setResponsibilities(domain.getResponsibilities());
        entity.setEmploymentType(domain.getEmploymentType());
        entity.setWorkLocation(domain.getWorkLocation());
        entity.setStatus(domain.getStatus());
        entity.setOpenDate(domain.getOpenDate());
        entity.setCloseDate(domain.getCloseDate());
        entity.setCloseReason(domain.getCloseReason());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getSalaryRange() != null) {
            entity.setSalaryMin(domain.getSalaryRange().getMin());
            entity.setSalaryMax(domain.getSalaryRange().getMax());
            entity.setSalaryCurrency(domain.getSalaryRange().getCurrency());
        }

        return entity;
    }

    private JobOpening toDomain(JobOpeningEntity entity) {
        SalaryRange salaryRange = null;
        if (entity.getSalaryMin() != null || entity.getSalaryMax() != null) {
            salaryRange = SalaryRange.of(
                    entity.getSalaryMin(),
                    entity.getSalaryMax(),
                    entity.getSalaryCurrency());
        }

        return JobOpening.reconstitute(
                OpeningId.of(entity.getOpeningId()),
                entity.getJobTitle(),
                entity.getDepartmentId(),
                entity.getNumberOfPositions(),
                entity.getFilledPositions(),
                salaryRange,
                entity.getRequirements(),
                entity.getResponsibilities(),
                entity.getEmploymentType(),
                entity.getWorkLocation(),
                entity.getStatus(),
                entity.getOpenDate(),
                entity.getCloseDate(),
                entity.getCloseReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
