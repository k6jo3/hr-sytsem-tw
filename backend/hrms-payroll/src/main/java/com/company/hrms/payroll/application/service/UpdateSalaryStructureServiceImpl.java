package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.UpdateSalaryStructureRequest;
import com.company.hrms.payroll.application.dto.response.SalaryStructureResponse;
import com.company.hrms.payroll.application.factory.SalaryStructureDtoFactory;
import com.company.hrms.payroll.application.service.context.UpdateSalaryStructureContext;
import com.company.hrms.payroll.application.service.task.ApplySalaryStructureChangesTask;
import com.company.hrms.payroll.application.service.task.FetchSalaryStructureTask;
import com.company.hrms.payroll.application.service.task.PersistSalaryStructureTask;

import lombok.RequiredArgsConstructor;

/**
 * 更新薪資結構服務
 * 使用 Pipeline 模式編排更新流程
 */
@Service("updateSalaryStructureServiceImpl")
@Transactional
@RequiredArgsConstructor
public class UpdateSalaryStructureServiceImpl
        extends AbstractCommandService<UpdateSalaryStructureRequest, SalaryStructureResponse> {

    private final FetchSalaryStructureTask fetchSalaryStructureTask;
    private final ApplySalaryStructureChangesTask applySalaryStructureChangesTask;
    private final PersistSalaryStructureTask persistSalaryStructureTask;

    @Override
    protected SalaryStructureResponse doExecute(UpdateSalaryStructureRequest request, JWTModel currentUser,
            String... args) throws Exception {

        // 取得結構 ID
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("結構 ID 為必填");
        }
        String structureId = args[0];

        // 1. 建立 Context
        UpdateSalaryStructureContext context = new UpdateSalaryStructureContext(structureId, request, currentUser);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(fetchSalaryStructureTask)
                .next(applySalaryStructureChangesTask)
                .next(persistSalaryStructureTask)
                .execute();

        // 3. 回傳結果
        return SalaryStructureDtoFactory.toResponse(context.getSalaryStructure());
    }
}
