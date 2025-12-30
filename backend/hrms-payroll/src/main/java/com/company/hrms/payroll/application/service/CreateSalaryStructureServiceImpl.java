package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.CreateSalaryStructureRequest;
import com.company.hrms.payroll.application.dto.response.SalaryStructureResponse;
import com.company.hrms.payroll.application.factory.SalaryStructureDtoFactory;
import com.company.hrms.payroll.application.service.context.CreateSalaryStructureContext;
import com.company.hrms.payroll.application.service.task.InitSalaryStructureTask;
import com.company.hrms.payroll.application.service.task.SaveSalaryStructureTask;

import lombok.RequiredArgsConstructor;

/**
 * 建立薪資結構服務
 * 使用 Pipeline 模式編排建立流程
 */
@Service("createSalaryStructureServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CreateSalaryStructureServiceImpl
        extends AbstractCommandService<CreateSalaryStructureRequest, SalaryStructureResponse> {

    private final InitSalaryStructureTask initSalaryStructureTask;
    private final SaveSalaryStructureTask saveSalaryStructureTask;

    @Override
    protected SalaryStructureResponse doExecute(CreateSalaryStructureRequest request, JWTModel currentUser,
            String... args) throws Exception {

        // 1. 建立 Context
        CreateSalaryStructureContext context = new CreateSalaryStructureContext(request, currentUser);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(initSalaryStructureTask)
                .next(saveSalaryStructureTask)
                .execute();

        // 3. 回傳結果
        return SalaryStructureDtoFactory.toResponse(context.getSalaryStructure());
    }
}
