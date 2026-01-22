package com.company.hrms.workflow.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.workflow.api.request.GetDelegationsRequest;
import com.company.hrms.workflow.api.response.DelegationResponse;
import com.company.hrms.workflow.api.response.GetDelegationsResponse;
import com.company.hrms.workflow.domain.model.aggregate.UserDelegation;
import com.company.hrms.workflow.domain.repository.IUserDelegationRepository;

import lombok.RequiredArgsConstructor;

@Service("getDelegationsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetDelegationsServiceImpl implements QueryApiService<GetDelegationsRequest, GetDelegationsResponse> {

    private final IUserDelegationRepository repository;

    @Override
    public GetDelegationsResponse getResponse(GetDelegationsRequest request, JWTModel currentUser, String... args)
            throws Exception {
        // Query by userId (if provided and authorised) or currentUser
        String targetUserId = (request.getUserId() != null && !request.getUserId().isEmpty())
                ? request.getUserId()
                : currentUser.getUserId();

        List<UserDelegation> list = repository.findAll();

        List<DelegationResponse> dtos = list.stream()
                .filter(d -> d.getDelegatorId().equals(targetUserId))
                .map(this::toDto)
                .collect(Collectors.toList());

        return GetDelegationsResponse.builder().data(dtos).build();
    }

    private DelegationResponse toDto(UserDelegation entity) {
        return DelegationResponse.builder()
                .id(entity.getDelegationId())
                .delegateeId(entity.getDelegateId())
                .delegateeName("Unknown")
                .startDate(entity.getStartDate().toString())
                .endDate(entity.getEndDate().toString())
                .status(entity.isActive() ? "ACTIVE" : "INACTIVE")
                .build();
    }
}
