package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.application.service.employee.context.EmployeeImportContext;
import com.company.hrms.organization.application.service.employee.task.ProcessEmployeeImportTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 批次匯入員工服務實作
 * <p>
 * 採用 Business Pipeline 模式建構架構
 * </p>
 */
@Service("importEmployeesServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImportEmployeesServiceImpl
        implements CommandApiService<Void, Void> {

    private final ProcessEmployeeImportTask processEmployeeImportTask;

    @Override
    public Void execCommand(Void request,
            JWTModel currentUser,
            String... args) throws Exception {
        log.info("Batch importing employees process started by {}",
                currentUser != null ? currentUser.getUserId() : "unknown");

        EmployeeImportContext context = new EmployeeImportContext();

        BusinessPipeline.start(context)
                .next(processEmployeeImportTask)
                .execute();

        log.info("Batch importing employees process completed. Success: {}, Failure: {}",
                context.getSuccessCount(), context.getFailureCount());

        return null;
    }
}
