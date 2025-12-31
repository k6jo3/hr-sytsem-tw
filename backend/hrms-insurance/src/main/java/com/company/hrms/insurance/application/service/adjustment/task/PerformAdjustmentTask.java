package com.company.hrms.insurance.application.service.adjustment.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.insurance.application.service.adjustment.context.AdjustmentContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 執行調整級距 Task
 */
@Component
@Slf4j
public class PerformAdjustmentTask implements PipelineTask<AdjustmentContext> {

    @Override
    public void execute(AdjustmentContext context) throws Exception {
        var enrollment = context.getEnrollment();
        var newLevel = context.getNewLevel();
        var oldSalary = enrollment.getMonthlySalary();

        log.debug("執行調整級距: {} -> {}", oldSalary, newLevel.getMonthlySalary());

        // 執行調整 (Domain 方法)
        enrollment.adjustLevel(newLevel);

        log.info("調整完成: oldSalary={}, newSalary={}",
                oldSalary, enrollment.getMonthlySalary());
    }

    @Override
    public String getName() {
        return "執行調整級距";
    }
}
