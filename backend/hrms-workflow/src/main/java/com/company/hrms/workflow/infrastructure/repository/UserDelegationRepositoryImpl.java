package com.company.hrms.workflow.infrastructure.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBaseRepository;
import com.company.hrms.workflow.domain.model.aggregate.UserDelegation;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.valueobject.UserDelegationId;
import com.company.hrms.workflow.domain.repository.IUserDelegationRepository;
import com.company.hrms.workflow.infrastructure.entity.QUserDelegationEntity;
import com.company.hrms.workflow.infrastructure.entity.UserDelegationEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class UserDelegationRepositoryImpl
        extends CommandBaseRepository<UserDelegationEntity, String>
        implements IUserDelegationRepository {

    private final ObjectMapper objectMapper;

    public UserDelegationRepositoryImpl(JPAQueryFactory factory, ObjectMapper objectMapper) {
        super(factory, UserDelegationEntity.class);
        this.objectMapper = objectMapper;
    }

    @Override
    public List<UserDelegation> findAll() {
        QUserDelegationEntity qDelegation = QUserDelegationEntity.userDelegationEntity;
        List<UserDelegationEntity> entities = factory.selectFrom(qDelegation).fetch();
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<UserDelegation> findActiveByDelegator(String delegatorId, LocalDate date) {
        QUserDelegationEntity qDelegation = QUserDelegationEntity.userDelegationEntity;

        List<UserDelegationEntity> entities = factory.selectFrom(qDelegation)
                .where(qDelegation.delegatorId.eq(delegatorId)
                        .and(qDelegation.isActive.isTrue())
                        .and(qDelegation.startDate.isNull().or(qDelegation.startDate.loe(date)))
                        .and(qDelegation.endDate.isNull().or(qDelegation.endDate.goe(date))))
                .fetch();

        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public UserDelegation save(UserDelegation delegation) {
        UserDelegationEntity entity = toEntity(delegation);
        super.save(entity);
        return delegation; // In a real implementation, we might update ID if generated
    }

    @Override
    public Optional<UserDelegation> findById(UserDelegationId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    // === Mappers ===

    private UserDelegationEntity toEntity(UserDelegation domain) {
        UserDelegationEntity entity = new UserDelegationEntity();
        entity.setDelegationId(domain.getDelegationId());
        entity.setDelegatorId(domain.getDelegatorId());
        entity.setDelegateId(domain.getDelegateId());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setIsActive(domain.isActive());
        entity.setDelegationScope(domain.getDelegationScope());
        entity.setReason(domain.getReason());
        entity.setCreatedAt(domain.getCreatedAt());

        if (domain.getSpecificFlowTypes() != null) {
            try {
                entity.setSpecificFlowTypes(objectMapper.writeValueAsString(domain.getSpecificFlowTypes()));
            } catch (JsonProcessingException e) {
                log.error("Error serializing specificFlowTypes", e);
                entity.setSpecificFlowTypes("[]");
            }
        }

        return entity;
    }

    private UserDelegation toDomain(UserDelegationEntity entity) {
        List<FlowType> flowTypes = new ArrayList<>();
        if (entity.getSpecificFlowTypes() != null) {
            try {
                flowTypes = objectMapper.readValue(entity.getSpecificFlowTypes(), new TypeReference<List<FlowType>>() {
                });
            } catch (JsonProcessingException e) {
                log.error("Error deserializing specificFlowTypes", e);
            }
        }

        return UserDelegation.builder()
                .delegationId(new UserDelegationId(entity.getDelegationId()))
                .delegatorId(entity.getDelegatorId())
                .delegateId(entity.getDelegateId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isActive(entity.getIsActive() != null && entity.getIsActive())
                .delegationScope(entity.getDelegationScope())
                .specificFlowTypes(flowTypes)
                .reason(entity.getReason())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
