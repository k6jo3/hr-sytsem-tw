package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.PayrollRunActionRequest;
import com.company.hrms.payroll.application.service.context.DeleteSalaryStructureContext;
import com.company.hrms.payroll.application.service.task.DeactivateSalaryStructureTask;
import com.company.hrms.payroll.application.service.task.FetchSalaryStructureForDeleteTask;

import lombok.RequiredArgsConstructor;

/**
 * 刪除薪資結構服務
 * 使用 Pipeline 模式編排刪除流程（邏輯刪除：將結構設為停用狀態）
 */
@Service("deleteSalaryStructureServiceImpl")
@Transactional
@RequiredArgsConstructor
public class DeleteSalaryStructureServiceImpl extends AbstractCommandService<PayrollRunActionRequest, Void> {

    private final FetchSalaryStructureForDeleteTask fetchTask;
    private final DeactivateSalaryStructureTask deactivateTask;

    @Override
    protected Void doExecute(PayrollRunActionRequest request, JWTModel currentUser, String... args)
            throws Exception {

        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("結構 ID 為必填");
        }
        String structureId = args[0];

        // 1. 建立 Context
        DeleteSalaryStructureContext context = new DeleteSalaryStructureContext(structureId, currentUser);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(fetchTask)
                .next(deactivateTask)
                .execute();

        return null;
    }
}
