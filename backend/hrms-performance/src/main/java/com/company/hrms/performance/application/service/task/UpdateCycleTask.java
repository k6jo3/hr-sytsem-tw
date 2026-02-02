package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.StartCycleContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 更新考核週期 Task (Domain Logic)
 */
@Component
@Slf4j
public class UpdateCycleTask implements PipelineTask<StartCycleContext> {

    @Override
    public void execute(StartCycleContext context) throws Exception {
        log.info("執行更新考核週期任務: {}", context.getCycleId());

        if (context.getCycle() == null) {
            throw new IllegalStateException("考核週期未載入");
        }

        // 更新週期名稱
        if (context.getCycleName() != null) {
            context.getCycle().updateCycleName(context.getCycleName());
        }

        // 更新考核類型
        if (context.getCycleType() != null) {
            context.getCycle().updateCycleType(context.getCycleType());
        }

        // 更新考核期間
        if (context.getStartDate() != null && context.getEndDate() != null) {
            context.getCycle().updatePeriod(context.getStartDate(), context.getEndDate());
        }

        // 更新截止日
        if (context.getSelfEvalDeadline() != null || context.getManagerEvalDeadline() != null) {
            context.getCycle().updateDeadlines(
                    context.getSelfEvalDeadline() != null ? context.getSelfEvalDeadline()
                            : context.getCycle().getSelfEvalDeadline(),
                    context.getManagerEvalDeadline() != null ? context.getManagerEvalDeadline()
                            : context.getCycle().getManagerEvalDeadline());
        }

        // 更新自評/主管評截止日 (如果 Domain 有提供相關方法，目前先依 Service 邏輯實作)
        // 注意：這裡只實作了 Service 原本 Lambda 的邏輯，如果需要更新更多欄位可在此擴展
    }

    @Override
    public String getName() {
        return "更新考核週期內容";
    }
}
