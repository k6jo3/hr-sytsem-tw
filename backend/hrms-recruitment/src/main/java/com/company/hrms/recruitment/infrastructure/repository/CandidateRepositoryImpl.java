package com.company.hrms.recruitment.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateStatus;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;
import com.company.hrms.recruitment.infrastructure.entity.CandidateEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 應徵者 Repository 實作
 */
@Repository
public class CandidateRepositoryImpl
        extends CommandBaseRepository<CandidateEntity, UUID>
        implements ICandidateRepository {

    private final EventPublisher eventPublisher;

    public CandidateRepositoryImpl(
            JPAQueryFactory factory,
            EventPublisher eventPublisher) {
        super(factory, CandidateEntity.class);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Candidate save(Candidate candidate) {
        CandidateEntity entity = toEntity(candidate);
        super.save(entity);

        // 發布 Domain Events
        candidate.getDomainEvents().forEach(eventPublisher::publish);
        candidate.clearDomainEvents();

        return candidate;
    }

    @Override
    public void delete(Candidate candidate) {
        CandidateEntity entity = toEntity(candidate);
        super.delete(entity);
    }

    @Override
    public Optional<Candidate> findById(CandidateId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Page<Candidate> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    @Override
    public List<Candidate> findByOpeningId(OpeningId openingId) {
        QueryGroup query = QueryBuilder.where()
                .and("openingId", Operator.EQ, openingId.getValue())
                .build();
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Candidate> findByOpeningIdAndStatus(OpeningId openingId, CandidateStatus status) {
        QueryGroup query = QueryBuilder.where()
                .and("openingId", Operator.EQ, openingId.getValue())
                .and("status", Operator.EQ, status)
                .build();
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByOpeningId(OpeningId openingId) {
        QueryGroup query = QueryBuilder.where()
                .and("openingId", Operator.EQ, openingId.getValue())
                .build();
        return super.count(query);
    }

    // ================= Entity 映射 =================

    private CandidateEntity toEntity(Candidate domain) {
        CandidateEntity entity = new CandidateEntity();
        entity.setCandidateId(domain.getId().getValue());
        entity.setOpeningId(domain.getOpeningId().getValue());
        entity.setFullName(domain.getFullName());
        entity.setEmail(domain.getEmail());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setResumeUrl(domain.getResumeUrl());
        entity.setSource(domain.getSource());
        entity.setReferrerId(domain.getReferrerId());
        entity.setApplicationDate(domain.getApplicationDate());
        entity.setStatus(domain.getStatus());
        entity.setRejectionReason(domain.getRejectionReason());
        entity.setCoverLetter(domain.getCoverLetter());
        entity.setExpectedSalary(domain.getExpectedSalary());
        entity.setAvailableDate(domain.getAvailableDate());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private Candidate toDomain(CandidateEntity entity) {
        return Candidate.reconstitute(
                CandidateId.of(entity.getCandidateId()),
                OpeningId.of(entity.getOpeningId()),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getResumeUrl(),
                entity.getSource(),
                entity.getReferrerId(),
                entity.getApplicationDate(),
                entity.getStatus(),
                entity.getRejectionReason(),
                entity.getCoverLetter(),
                entity.getExpectedSalary(),
                entity.getAvailableDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    @Override
    public boolean existsByEmailAndOpeningId(String email, OpeningId openingId) {
        QueryGroup query = QueryBuilder.where()
                .eq("email", email)
                .eq("openingId", openingId.getValue())
                .build();
        return super.count(query) > 0;
    }

    @Override
    public long count(QueryGroup query) {
        return super.count(query);
    }
}
