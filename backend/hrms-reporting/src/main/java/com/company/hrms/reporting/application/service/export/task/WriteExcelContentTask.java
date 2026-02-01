package com.company.hrms.reporting.application.service.export.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.reporting.application.service.export.context.ExcelExportContext;

/**
 * 寫入 Excel 數據內容任務
 */
@Component
public class WriteExcelContentTask implements PipelineTask<ExcelExportContext> {

    @Override
    public void execute(ExcelExportContext ctx) {
        Sheet sheet = ctx.getWorkbook().createSheet(ctx.getSheetName());
        List<String> headers = ctx.getHeaders();
        List<List<Object>> data = ctx.getData();

        // 建立表頭
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(ctx.getHeaderStyle());
        }

        // 填入資料
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            List<Object> rowData = data.get(i);

            for (int j = 0; j < rowData.size(); j++) {
                Cell cell = row.createCell(j);
                Object value = rowData.get(j);

                if (value != null) {
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value instanceof LocalDateTime) {
                        cell.setCellValue(((LocalDateTime) value)
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
                cell.setCellStyle(ctx.getDataStyle());
            }
        }
    }
}
