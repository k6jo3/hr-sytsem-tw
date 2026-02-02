package com.company.hrms.reporting.application.service.export;

import org.springframework.stereotype.Service;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.ExportExcelRequest;
import com.company.hrms.reporting.application.service.export.context.ExportExcelContext;
import com.company.hrms.reporting.application.service.export.task.GenerateExcelTask;
import com.company.hrms.reporting.application.service.export.task.LoadEmployeeRosterDataTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel 匯出 API 服務實作
 */
@Service("exportExcelServiceImpl")
@Slf4j
@RequiredArgsConstructor
public class ExportExcelServiceImpl implements QueryApiService<ExportExcelRequest, byte[]> {

    private final LoadEmployeeRosterDataTask loadEmployeeRosterDataTask;
    private final GenerateExcelTask generateExcelTask;

    @Override
    public byte[] getResponse(ExportExcelRequest request, JWTModel currentUser, String... args) throws Exception {
        log.info("開始處理 Excel 匯出請求: {}", request.getReportType());

        ExportExcelContext ctx = new ExportExcelContext(request, currentUser);

        BusinessPipeline.start(ctx)
                .next(loadEmployeeRosterDataTask)
                // 在此可根據 reportType 擴充不同的 LoadTask
                .next(generateExcelTask)
                .execute();

        return ctx.getExcelContent();
    }
}
