package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.application.service.employee.context.EmployeeExportContext;
import com.company.hrms.organization.application.service.employee.task.FetchAllEmployeesTask;
import com.company.hrms.organization.application.service.employee.task.GenerateEmployeeCsvTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 匯出員工清單服務實作
 * <p>
 * 目前實作 CSV 格式匯出
 * </p>
 */
@Service("exportEmployeesServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExportEmployeesServiceImpl
        implements QueryApiService<Void, byte[]> {

    private final FetchAllEmployeesTask fetchAllEmployeesTask;
    private final GenerateEmployeeCsvTask generateEmployeeCsvTask;

    @Override
    public byte[] getResponse(Void request,
            JWTModel currentUser,
            String... args) throws Exception {
        log.info("Exporting employees to CSV");

        EmployeeExportContext context = new EmployeeExportContext();

        BusinessPipeline.start(context)
                .next(fetchAllEmployeesTask)
                .next(generateEmployeeCsvTask)
                .execute();

        return context.getResult();
    }
}
