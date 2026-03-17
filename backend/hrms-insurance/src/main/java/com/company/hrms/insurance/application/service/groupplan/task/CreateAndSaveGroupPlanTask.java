package com.company.hrms.insurance.application.service.groupplan.task;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.insurance.api.request.CreateGroupInsurancePlanRequest;
import com.company.hrms.insurance.application.service.groupplan.context.CreateGroupPlanContext;
import com.company.hrms.insurance.domain.model.aggregate.GroupInsurancePlan;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.repository.IGroupInsurancePlanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 建立並儲存團體保險方案 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateAndSaveGroupPlanTask implements PipelineTask<CreateGroupPlanContext> {

    private final IGroupInsurancePlanRepository planRepository;

    @Override
    public void execute(CreateGroupPlanContext context) throws Exception {
        CreateGroupInsurancePlanRequest request = context.getRequest();
        log.debug("建立團體保險方案: planCode={}", request.getPlanCode());

        InsuranceType insuranceType = InsuranceType.valueOf(request.getInsuranceType());
        LocalDate startDate = LocalDate.parse(request.getContractStartDate());
        LocalDate endDate = request.getContractEndDate() != null
                ? LocalDate.parse(request.getContractEndDate())
                : null;

        GroupInsurancePlan plan = GroupInsurancePlan.create(
                request.getOrganizationId(),
                request.getPlanName(),
                request.getPlanCode(),
                insuranceType,
                request.getInsurerName(),
                request.getPolicyNumber(),
                startDate,
                endDate);

        planRepository.save(plan);
        context.setPlan(plan);

        log.info("團體保險方案建立完成: planId={}, planCode={}",
                plan.getPlanId(), plan.getPlanCode());
    }

    @Override
    public String getName() {
        return "建立並儲存團體保險方案";
    }
}
