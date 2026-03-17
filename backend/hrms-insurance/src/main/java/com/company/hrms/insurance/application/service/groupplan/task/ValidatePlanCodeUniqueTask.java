package com.company.hrms.insurance.application.service.groupplan.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.ResourceAlreadyExistsException;
import com.company.hrms.insurance.application.service.groupplan.context.CreateGroupPlanContext;
import com.company.hrms.insurance.domain.repository.IGroupInsurancePlanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 驗證方案代碼唯一性 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ValidatePlanCodeUniqueTask implements PipelineTask<CreateGroupPlanContext> {

    private final IGroupInsurancePlanRepository planRepository;

    @Override
    public void execute(CreateGroupPlanContext context) throws Exception {
        String planCode = context.getRequest().getPlanCode();
        log.debug("驗證方案代碼唯一性: planCode={}", planCode);

        planRepository.findByPlanCode(planCode).ifPresent(existing -> {
            throw new ResourceAlreadyExistsException(
                    "PLAN_CODE_EXISTS", "方案代碼已存在: " + planCode);
        });

        log.info("方案代碼驗證通過: planCode={}", planCode);
    }

    @Override
    public String getName() {
        return "驗證方案代碼唯一性";
    }
}
