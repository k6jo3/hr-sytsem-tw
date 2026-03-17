package com.company.hrms.insurance.application.service.groupplan;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.insurance.api.request.GetGroupInsurancePlanListRequest;
import com.company.hrms.insurance.api.response.GroupInsurancePlanResponse;
import com.company.hrms.insurance.application.factory.GroupInsurancePlanDtoFactory;
import com.company.hrms.insurance.domain.model.aggregate.GroupInsurancePlan;
import com.company.hrms.insurance.domain.repository.IGroupInsurancePlanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢團體保險方案列表服務實作
 */
@Service("getGroupInsurancePlanListServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GetGroupInsurancePlanListServiceImpl
        implements QueryApiService<GetGroupInsurancePlanListRequest, PageResponse<GroupInsurancePlanResponse>> {

    private final IGroupInsurancePlanRepository planRepository;
    private final GroupInsurancePlanDtoFactory dtoFactory;

    @Override
    public PageResponse<GroupInsurancePlanResponse> getResponse(
            GetGroupInsurancePlanListRequest request, JWTModel currentUser, String... args) throws Exception {

        String organizationId = request != null ? request.getOrganizationId() : null;
        log.debug("查詢團體保險方案列表: organizationId={}", organizationId);

        List<GroupInsurancePlan> plans;
        if (organizationId != null && !organizationId.isBlank()) {
            plans = planRepository.findByOrganizationId(organizationId);
        } else {
            // 無組織 ID 時回傳空列表（安全考量）
            plans = List.of();
        }

        // 依查詢條件過濾
        if (request != null && request.getInsuranceType() != null) {
            plans = plans.stream()
                    .filter(p -> p.getInsuranceType().name().equals(request.getInsuranceType()))
                    .collect(Collectors.toList());
        }
        if (request != null && request.getActive() != null) {
            plans = plans.stream()
                    .filter(p -> p.isActive() == request.getActive())
                    .collect(Collectors.toList());
        }

        List<GroupInsurancePlanResponse> items = plans.stream()
                .map(dtoFactory::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(items, 1, 20, items.size());
    }
}
