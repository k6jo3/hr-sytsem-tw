package com.company.hrms.reporting.application.service.export.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.reporting.application.service.export.ExcelExportService;
import com.company.hrms.reporting.application.service.export.context.ExportExcelContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 產生 Excel 檔案任務
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateExcelTask implements PipelineTask<ExportExcelContext> {

    private final ExcelExportService excelExportService;

    @Override
    public void execute(ExportExcelContext ctx) throws Exception {
        if (ctx.getData() == null || ctx.getData().isEmpty()) {
            log.warn("無資料可供匯出");
            return;
        }

        log.info("執行產生 Excel 檔案任務，筆數: {}", ctx.getData().size());

        byte[] content = excelExportService.exportToExcel(
                ctx.getHeaders(),
                ctx.getData(),
                ctx.getSheetName() != null ? ctx.getSheetName() : "Report");

        ctx.setExcelContent(content);
    }

    @Override
    public String getName() {
        return "產生 Excel 檔案";
    }
}
