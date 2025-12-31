package com.company.hrms.insurance.application.service.enrollment.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.insurance.application.service.enrollment.context.EnrollmentContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceUnit;
import com.company.hrms.insurance.domain.model.valueobject.UnitId;
import com.company.hrms.insurance.domain.repository.IInsuranceUnitRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 載入投保單位 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoadInsuranceUnitTask implements PipelineTask<EnrollmentContext> {

    private final IInsuranceUnitRepository unitRepository;

    @Override
    public void execute(EnrollmentContext context) throws Exception {
        String unitId = context.getRequest().getInsuranceUnitId();
        log.debug("載入投保單位: unitId={}", unitId);

        InsuranceUnit unit = unitRepository.findById(new UnitId(unitId))
                .orElseThrow(() -> new IllegalArgumentException("投保單位不存在: " + unitId));

        if (!unit.isActive()) {
            throw new IllegalStateException("投保單位已停用: " + unitId);
        }

        context.setInsuranceUnit(unit);
        log.info("投保單位載入成功: unitCode={}", unit.getUnitCode());
    }

    @Override
    public String getName() {
        return "載入投保單位";
    }

    @Override
    public boolean shouldExecute(EnrollmentContext context) {
        return context.getRequest() != null && context.getRequest().getInsuranceUnitId() != null;
    }
}
