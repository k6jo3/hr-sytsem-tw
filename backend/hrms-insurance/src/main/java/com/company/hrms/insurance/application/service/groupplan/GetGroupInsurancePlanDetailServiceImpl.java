package com.company.hrms.insurance.application.service.groupplan;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.insurance.api.response.GroupInsurancePlanDetailResponse;
import com.company.hrms.insurance.application.factory.GroupInsurancePlanDtoFactory;
import com.company.hrms.insurance.domain.model.aggregate.GroupInsurancePlan;
import com.company.hrms.insurance.domain.repository.IGroupInsurancePlanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢團體保險方案詳情服務實作
 */
@Service("getGroupInsurancePlanDetailServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GetGroupInsurancePlanDetailServiceImpl
        implements QueryApiService<String, GroupInsurancePlanDetailResponse> {

    private final IGroupInsurancePlanRepository planRepository;
    private final GroupInsurancePlanDtoFactory dtoFactory;

    @Override
    public GroupInsurancePlanDetailResponse getResponse(
            String id, JWTModel currentUser, String... args) throws Exception {

        log.info("查詢團體保險方案詳情: planId={}", id);

        GroupInsurancePlan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "PLAN_NOT_FOUND", "找不到團體保險方案: " + id));

        return dtoFactory.toDetailResponse(plan);
    }
}
