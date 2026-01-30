package com.company.hrms.training.application.task.statistics;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.statistics.ExportTrainingStatisticsContext;
import com.company.hrms.training.infrastructure.entity.TrainingEnrollmentEntity;

import lombok.SneakyThrows;

@Component
public class GenerateTrainingStatisticsExcelTask implements PipelineTask<ExportTrainingStatisticsContext> {

    @Override
    @SneakyThrows
    public void execute(ExportTrainingStatisticsContext context) {
        byte[] excelData = generateExcel(context.getEnrollments());
        context.setExcelData(excelData);
    }

    private byte[] generateExcel(List<TrainingEnrollmentEntity> enrollments) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("訓練統計");

            // 標題樣式
            CellStyle headerStyle = workbook.createCellStyle();
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 標題列
            Row headerRow = sheet.createRow(0);
            String[] headers = { "員工編號", "課程編號", "課程名稱", "狀態", "出席時數", "完成時數", "完成日期" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 資料列
            int rowNum = 1;
            for (TrainingEnrollmentEntity e : enrollments) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(e.getEmployee_id() != null ? e.getEmployee_id() : "");
                row.createCell(1).setCellValue(e.getCourse_id() != null ? e.getCourse_id() : "");
                row.createCell(2).setCellValue(""); // TODO: 需關聯課程取得名稱
                row.createCell(3).setCellValue(e.getStatus() != null ? e.getStatus().name() : "");
                row.createCell(4).setCellValue(e.getAttendedHours() != null ? e.getAttendedHours().doubleValue() : 0);
                row.createCell(5).setCellValue(e.getCompletedHours() != null ? e.getCompletedHours().doubleValue() : 0);
                row.createCell(6).setCellValue(e.getCompletedAt() != null ? e.getCompletedAt().toString() : "");
            }

            // 自動調整欄寬
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 輸出為 byte[]
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }
}
