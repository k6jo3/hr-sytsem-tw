package com.company.hrms.recruitment.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBaseRepository;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.recruitment.domain.model.aggregate.Offer;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.OfferId;
import com.company.hrms.recruitment.domain.repository.IOfferRepository;
import com.company.hrms.recruitment.infrastructure.entity.OfferEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * Offer Repository 實作
 */
@Repository
public class OfferRepositoryImpl
        extends CommandBaseRepository<OfferEntity, UUID>
        implements IOfferRepository {

    private final EventPublisher eventPublisher;

    public OfferRepositoryImpl(
            JPAQueryFactory factory,
            EventPublisher eventPublisher) {
        super(factory, OfferEntity.class);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Offer save(Offer offer) {
        OfferEntity entity = toEntity(offer);
        super.save(entity);

        offer.getDomainEvents().forEach(eventPublisher::publish);
        offer.clearDomainEvents();

        return offer;
    }

    @Override
    public void delete(Offer offer) {
        OfferEntity entity = toEntity(offer);
        super.delete(entity);
    }

    @Override
    public Optional<Offer> findById(OfferId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Page<Offer> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    @Override
    public Optional<Offer> findByCandidateId(CandidateId candidateId) {
        QueryGroup query = QueryBuilder.where()
                .and("candidateId", com.company.hrms.common.query.Operator.EQ, candidateId.getValue())
                .build();
        return super.findAll(query).stream()
                .map(this::toDomain)
                .findFirst();
    }

    // ================= Entity 映射 =================

    private OfferEntity toEntity(Offer domain) {
        OfferEntity entity = new OfferEntity();
        entity.setOfferId(domain.getId().getValue());
        entity.setCandidateId(domain.getCandidateId().getValue());
        entity.setCandidateName(domain.getCandidateName());
        entity.setOfferedPosition(domain.getOfferedPosition());
        entity.setOfferedSalary(domain.getOfferedSalary());
        entity.setOfferedStartDate(domain.getOfferedStartDate());
        entity.setOfferDate(domain.getOfferDate());
        entity.setExpiryDate(domain.getExpiryDate());
        entity.setStatus(domain.getStatus());
        entity.setResponseDate(domain.getResponseDate());
        entity.setRejectionReason(domain.getRejectionReason());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private Offer toDomain(OfferEntity entity) {
        return Offer.reconstitute(
                OfferId.of(entity.getOfferId()),
                CandidateId.of(entity.getCandidateId()),
                entity.getCandidateName(),
                entity.getOfferedPosition(),
                entity.getOfferedSalary(),
                entity.getOfferedStartDate(),
                entity.getOfferDate(),
                entity.getExpiryDate(),
                entity.getStatus(),
                entity.getResponseDate(),
                entity.getRejectionReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
