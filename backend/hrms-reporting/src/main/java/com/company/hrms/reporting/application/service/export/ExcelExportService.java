package com.company.hrms.reporting.application.service.export;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Excel 匯出服務
 * 
 * <p>
 * 使用 Apache POI 產生 Excel 檔案
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service
@Slf4j
public class ExcelExportService {

    /**
     * 匯出資料到 Excel
     * 
     * @param headers   表頭
     * @param data      資料列表
     * @param sheetName 工作表名稱
     * @return Excel 檔案的 byte 陣列
     */
    public byte[] exportToExcel(
            List<String> headers,
            List<List<Object>> data,
            String sheetName) throws Exception {
        // TODO: 不符合business pipeline設計以及clean code
        log.info("開始匯出 Excel，工作表: {}, 資料筆數: {}", sheetName, data.size());

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // 建立工作表
            Sheet sheet = workbook.createSheet(sheetName);

            // 建立樣式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // 建立表頭
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // 填入資料
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                List<Object> rowData = data.get(i);

                for (int j = 0; j < rowData.size(); j++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(j);
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
                    cell.setCellStyle(dataStyle);
                }
            }

            // 自動調整欄寬
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
                // 設定最小寬度
                int currentWidth = sheet.getColumnWidth(i);
                if (currentWidth < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            // 寫入輸出流
            workbook.write(outputStream);

            log.info("Excel 匯出完成，檔案大小: {} bytes", outputStream.size());

            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Excel 匯出失敗", e);
            throw new RuntimeException("Excel 匯出失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 建立表頭樣式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // 設定字體
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        // 設定對齊
        style.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        style.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

        // 設定邊框
        style.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        // 設定背景色
        style.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

        return style;
    }

    /**
     * 建立資料樣式
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // 設定邊框
        style.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        style.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        // 設定對齊
        style.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

        return style;
    }

    /**
     * 匯出員工花名冊範例
     */
    public byte[] exportEmployeeRoster(List<EmployeeRosterData> employees) throws Exception {
        List<String> headers = List.of(
                "員工編號", "姓名", "部門", "職位", "到職日期", "年資", "狀態", "電話", "Email");

        List<List<Object>> data = employees.stream()
                .map(emp -> {
                    List<Object> row = new java.util.ArrayList<>();
                    row.add(emp.getEmployeeId());
                    row.add(emp.getName());
                    row.add(emp.getDepartmentName());
                    row.add(emp.getPositionName());
                    row.add(emp.getHireDate());
                    row.add(emp.getServiceYears());
                    row.add(emp.getStatus());
                    row.add(emp.getPhone());
                    row.add(emp.getEmail());
                    return row;
                })
                .toList();

        return exportToExcel(headers, data, "員工花名冊");
    }

    /**
     * 員工花名冊資料 (內部類別)
     */
    @lombok.Data
    @lombok.Builder
    public static class EmployeeRosterData {
        private String employeeId;
        private String name;
        private String departmentName;
        private String positionName;
        private String hireDate;
        private Double serviceYears;
        private String status;
        private String phone;
        private String email;
    }
}
