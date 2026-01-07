package com.company.hrms.recruitment.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBaseRepository;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.recruitment.domain.model.aggregate.Interview;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewId;
import com.company.hrms.recruitment.domain.model.valueobject.OverallRating;
import com.company.hrms.recruitment.domain.repository.IInterviewRepository;
import com.company.hrms.recruitment.infrastructure.entity.InterviewEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 面試 Repository 實作
 */
@Repository
public class InterviewRepositoryImpl
        extends CommandBaseRepository<InterviewEntity, UUID>
        implements IInterviewRepository {

    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public InterviewRepositoryImpl(
            JPAQueryFactory factory,
            EventPublisher eventPublisher,
            ObjectMapper objectMapper) {
        super(factory, InterviewEntity.class);
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public Interview save(Interview interview) {
        InterviewEntity entity = toEntity(interview);
        super.save(entity);

        interview.getDomainEvents().forEach(eventPublisher::publish);
        interview.clearDomainEvents();

        return interview;
    }

    @Override
    public void delete(Interview interview) {
        InterviewEntity entity = toEntity(interview);
        super.delete(entity);
    }

    @Override
    public Optional<Interview> findById(InterviewId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Page<Interview> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    @Override
    public List<Interview> findByCandidateId(CandidateId candidateId) {
        QueryGroup query = QueryBuilder.where()
                .and("candidateId", com.company.hrms.common.query.Operator.EQ, candidateId.getValue())
                .build();
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ================= Entity 映射 =================

    private InterviewEntity toEntity(Interview domain) {
        InterviewEntity entity = new InterviewEntity();
        entity.setInterviewId(domain.getId().getValue());
        entity.setCandidateId(domain.getCandidateId().getValue());
        entity.setCandidateName(domain.getCandidateName());
        entity.setInterviewRound(domain.getInterviewRound());
        entity.setInterviewType(domain.getInterviewType());
        entity.setInterviewDate(domain.getInterviewDate());
        entity.setLocation(domain.getLocation());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        // 序列化面試官 ID 列表
        try {
            entity.setInterviewerIdsJson(objectMapper.writeValueAsString(domain.getInterviewerIds()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("無法序列化面試官列表", e);
        }

        // 序列化評估列表
        try {
            entity.setEvaluationsJson(objectMapper.writeValueAsString(
                    domain.getEvaluations().stream()
                            .map(this::toEvaluationDto)
                            .collect(Collectors.toList())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("無法序列化評估列表", e);
        }

        return entity;
    }

    private Interview toDomain(InterviewEntity entity) {
        // 反序列化面試官 ID 列表
        List<UUID> interviewerIds;
        try {
            interviewerIds = entity.getInterviewerIdsJson() != null
                    ? objectMapper.readValue(entity.getInterviewerIdsJson(),
                            new TypeReference<List<UUID>>() {
                            })
                    : new ArrayList<>();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("無法反序列化面試官列表", e);
        }

        // 反序列化評估列表
        List<Interview.InterviewEvaluation> evaluations;
        try {
            List<EvaluationDto> dtos = entity.getEvaluationsJson() != null
                    ? objectMapper.readValue(entity.getEvaluationsJson(),
                            new TypeReference<List<EvaluationDto>>() {
                            })
                    : new ArrayList<>();
            evaluations = dtos.stream()
                    .map(this::toEvaluation)
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("無法反序列化評估列表", e);
        }

        return Interview.reconstitute(
                InterviewId.of(entity.getInterviewId()),
                CandidateId.of(entity.getCandidateId()),
                entity.getCandidateName(),
                entity.getInterviewRound(),
                entity.getInterviewType(),
                entity.getInterviewDate(),
                entity.getLocation(),
                interviewerIds,
                entity.getStatus(),
                evaluations,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    // 評估 DTO（用於 JSON 序列化）
    private static class EvaluationDto {
        public UUID interviewerId;
        public Integer technicalScore;
        public Integer communicationScore;
        public Integer cultureFitScore;
        public String overallRating;
        public String comments;
        public String strengths;
        public String concerns;
    }

    private EvaluationDto toEvaluationDto(Interview.InterviewEvaluation eval) {
        EvaluationDto dto = new EvaluationDto();
        dto.interviewerId = eval.getInterviewerId();
        dto.technicalScore = eval.getTechnicalScore();
        dto.communicationScore = eval.getCommunicationScore();
        dto.cultureFitScore = eval.getCultureFitScore();
        dto.overallRating = eval.getOverallRating() != null ? eval.getOverallRating().name() : null;
        dto.comments = eval.getComments();
        dto.strengths = eval.getStrengths();
        dto.concerns = eval.getConcerns();
        return dto;
    }

    private Interview.InterviewEvaluation toEvaluation(EvaluationDto dto) {
        return new Interview.InterviewEvaluation(
                dto.interviewerId,
                dto.technicalScore,
                dto.communicationScore,
                dto.cultureFitScore,
                dto.overallRating != null ? OverallRating.valueOf(dto.overallRating) : null,
                dto.comments,
                dto.strengths,
                dto.concerns);
    }
}
