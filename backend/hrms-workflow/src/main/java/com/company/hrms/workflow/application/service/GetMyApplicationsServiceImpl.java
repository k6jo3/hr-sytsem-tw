package com.company.hrms.workflow.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.infrastructure.persistence.querydsl.engine.UltimateQueryEngine;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.workflow.api.request.GetMyApplicationsRequest;
import com.company.hrms.workflow.api.response.MyApplicationsResponse;
import com.company.hrms.workflow.infrastructure.entity.WorkflowInstanceEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Service("getMyApplicationsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetMyApplicationsServiceImpl
                implements QueryApiService<GetMyApplicationsRequest, Page<MyApplicationsResponse>> {

        private final JPAQueryFactory factory;

        @Override
        public Page<MyApplicationsResponse> getResponse(GetMyApplicationsRequest req, JWTModel currentUser,
                        String... args)
                        throws Exception {
                // TODO: 不符合business pipeline以及Fluent-Query-Engine設計
                UltimateQueryEngine<WorkflowInstanceEntity> engine = new UltimateQueryEngine<>(factory,
                                WorkflowInstanceEntity.class);

                // Security: Force filter by applicantId = current user
                req.eq("applicantId", currentUser.getUserId());

                BooleanExpression predicate = engine.parse(req);

                JPAQuery<WorkflowInstanceEntity> query = engine.getQuery();
                if (predicate != null) {
                        query.where(predicate);
                }

                // Count Query
                UltimateQueryEngine<WorkflowInstanceEntity> countEngine = new UltimateQueryEngine<>(factory,
                                WorkflowInstanceEntity.class);
                BooleanExpression countPred = countEngine.parse(req);
                long finalTotal = countEngine.getQuery().where(countPred).select(countEngine.getEntityPath().count())
                                .fetchOne();

                List<WorkflowInstanceEntity> entities = query
                                .offset(req.getPageable().getOffset())
                                .limit(req.getPageable().getPageSize())
                                .orderBy(engine.getEntityPath().getString("startedAt").desc())
                                .fetch();

                List<MyApplicationsResponse> content = entities.stream().map(e -> MyApplicationsResponse.builder()
                                .instanceId(e.getInstanceId())
                                .businessType(e.getBusinessType())
                                .businessId(e.getBusinessId())
                                .businessUrl(e.getBusinessUrl())
                                .currentNodeName(e.getCurrentNodeName())
                                .status(e.getStatus().name())
                                .startedAt(e.getStartedAt())
                                .completedAt(e.getCompletedAt())
                                .summary(e.getSummary())
                                .build()).collect(Collectors.toList());

                return new org.springframework.data.domain.PageImpl<>(content, req.getPageable(), finalTotal);
        }
}
