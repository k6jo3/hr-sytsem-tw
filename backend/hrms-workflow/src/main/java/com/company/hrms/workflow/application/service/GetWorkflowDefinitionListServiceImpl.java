package com.company.hrms.workflow.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.infrastructure.persistence.querydsl.engine.UltimateQueryEngine;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.workflow.api.request.GetWorkflowDefinitionListRequest;
import com.company.hrms.workflow.api.response.WorkflowDefinitionResponse;
import com.company.hrms.workflow.infrastructure.entity.WorkflowDefinitionEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Service("getWorkflowDefinitionListServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetWorkflowDefinitionListServiceImpl
                implements QueryApiService<GetWorkflowDefinitionListRequest, Page<WorkflowDefinitionResponse>> {

        private final JPAQueryFactory factory;

        @Override
        public Page<WorkflowDefinitionResponse> getResponse(GetWorkflowDefinitionListRequest req, JWTModel currentUser,
                        String... args) throws Exception {
                // TODO: 不符合business pipeline以及Fluent-Query-Engine設計
                UltimateQueryEngine<WorkflowDefinitionEntity> engine = new UltimateQueryEngine<>(factory,
                                WorkflowDefinitionEntity.class);
                BooleanExpression predicate = engine.parse(req);
                JPAQuery<WorkflowDefinitionEntity> query = engine.getQuery();

                if (predicate != null) {
                        query.where(predicate);
                }

                // Count
                UltimateQueryEngine<WorkflowDefinitionEntity> countEngine = new UltimateQueryEngine<>(factory,
                                WorkflowDefinitionEntity.class);
                BooleanExpression countPredicate = countEngine.parse(req);
                JPAQuery<WorkflowDefinitionEntity> countQuery = countEngine.getQuery();
                if (countPredicate != null) {
                        countQuery.where(countPredicate);
                }
                long total = countQuery.select(countEngine.getEntityPath().count()).fetchOne();

                // Fetch
                List<WorkflowDefinitionEntity> entities = query
                                .offset(req.getPageable().getOffset())
                                .limit(req.getPageable().getPageSize())
                                .orderBy(engine.getEntityPath().getString("createdAt").desc())
                                .fetch();

                List<WorkflowDefinitionResponse> content = entities.stream()
                                .map(e -> WorkflowDefinitionResponse.builder()
                                                .definitionId(e.getDefinitionId())
                                                .flowName(e.getFlowName())
                                                .flowType(e.getFlowType())
                                                .isActive(e.isActive())
                                                .version(e.getVersion())
                                                .createdAt(e.getCreatedAt())
                                                .nodes(e.getNodesJson())
                                                .edges(e.getEdgesJson())
                                                .build())
                                .collect(Collectors.toList());

                return new PageImpl<>(content, req.getPageable(), total);
        }
}
