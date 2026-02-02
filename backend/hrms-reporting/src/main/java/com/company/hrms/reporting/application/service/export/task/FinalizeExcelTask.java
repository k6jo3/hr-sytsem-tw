package com.company.hrms.reporting.application.service.export.task;

import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.reporting.application.service.export.context.ExcelExportContext;

/**
 * 完成 Excel 匯出任務（調整格式並輸出）
 */
@Component
public class FinalizeExcelTask implements PipelineTask<ExcelExportContext> {

    @Override
    public void execute(ExcelExportContext ctx) throws Exception {
        Sheet sheet = ctx.getWorkbook().getSheet(ctx.getSheetName());
        int headerSize = ctx.getHeaders().size();

        // 自動調整欄寬
        for (int i = 0; i < headerSize; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            if (currentWidth < 3000) {
                sheet.setColumnWidth(i, 3000);
            }
        }

        // 寫入輸出流
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ctx.getWorkbook().write(outputStream);
            ctx.setResult(outputStream.toByteArray());
        } finally {
            ctx.getWorkbook().close();
        }
    }
}
