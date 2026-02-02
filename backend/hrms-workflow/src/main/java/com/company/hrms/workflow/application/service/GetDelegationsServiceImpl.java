package com.company.hrms.workflow.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.workflow.api.request.GetDelegationsRequest;
import com.company.hrms.workflow.api.response.DelegationResponse;
import com.company.hrms.workflow.api.response.GetDelegationsResponse;
import com.company.hrms.workflow.infrastructure.entity.UserDelegationEntity;
import com.company.hrms.workflow.infrastructure.repository.UserDelegationQueryRepository;

import lombok.RequiredArgsConstructor;

@Service("getDelegationsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetDelegationsServiceImpl implements QueryApiService<GetDelegationsRequest, GetDelegationsResponse> {

        private final UserDelegationQueryRepository queryRepository;

        @Override
        public GetDelegationsResponse getResponse(GetDelegationsRequest request, JWTModel currentUser, String... args)
                        throws Exception {

                // 1. 使用 Fluent-Query-Engine 定義查詢條件
                QueryGroup group = QueryBuilder.fromCondition(request);

                // 2. 權限/預設值處理：若未指定 userId，則查詢目前使用者的代理設定
                if (request.getUserId() == null || request.getUserId().isEmpty()) {
                        group.eq("delegatorId", currentUser.getUserId());
                }

                // 3. 執行資料庫端分頁/過濾查詢 (解決全查效能問題)
                List<UserDelegationEntity> list = queryRepository.findAll(group);

                // 4. Transform to Response DTO
                List<DelegationResponse> dtos = list.stream()
                                .map(this::toDto)
                                .collect(Collectors.toList());

                return GetDelegationsResponse.builder().data(dtos).build();
        }

        private DelegationResponse toDto(UserDelegationEntity entity) {
                return DelegationResponse.builder()
                                .id(entity.getDelegationId())
                                .delegateeId(entity.getDelegateId())
                                .delegateeName("Unknown") // 可串接組織服務查詢姓名
                                .startDate(entity.getStartDate().toString())
                                .endDate(entity.getEndDate().toString())
                                .status(Boolean.TRUE.equals(entity.getIsActive()) ? "ACTIVE" : "INACTIVE")
                                .build();
        }
}
