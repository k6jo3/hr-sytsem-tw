package com.company.hrms.insurance.application.service.groupplan;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.insurance.api.response.GroupInsurancePlanDetailResponse;
import com.company.hrms.insurance.application.factory.GroupInsurancePlanDtoFactory;
import com.company.hrms.insurance.domain.model.aggregate.GroupInsurancePlan;
import com.company.hrms.insurance.domain.repository.IGroupInsurancePlanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 停用團體保險方案服務實作
 */
@Service("deactivateGroupInsurancePlanServiceImpl")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DeactivateGroupInsurancePlanServiceImpl
        implements CommandApiService<Object, GroupInsurancePlanDetailResponse> {

    private final IGroupInsurancePlanRepository planRepository;
    private final GroupInsurancePlanDtoFactory dtoFactory;

    @Override
    public GroupInsurancePlanDetailResponse execCommand(
            Object request, JWTModel currentUser, String... args) throws Exception {

        String planId = args[0];
        log.info("停用團體保險方案: planId={}", planId);

        // 載入方案
        GroupInsurancePlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "PLAN_NOT_FOUND", "找不到團體保險方案: " + planId));

        // 使用 Domain 方法停用
        plan.deactivate();

        // 儲存
        planRepository.save(plan);

        return dtoFactory.toDetailResponse(plan);
    }
}
