package com.company.hrms.reporting.application.service.export;

import java.util.List;

import org.springframework.stereotype.Service;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.reporting.application.service.export.context.ExcelExportContext;
import com.company.hrms.reporting.application.service.export.task.FinalizeExcelTask;
import com.company.hrms.reporting.application.service.export.task.PrepareWorkbookTask;
import com.company.hrms.reporting.application.service.export.task.WriteExcelContentTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel 匯出服務
 * 
 * <p>
 * 使用 Business Pipeline 編排 Excel 產生流程
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelExportService {

    private final PrepareWorkbookTask prepareWorkbookTask;
    private final WriteExcelContentTask writeExcelContentTask;
    private final FinalizeExcelTask finalizeExcelTask;

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

        log.info("開始匯出 Excel，工作表: {}, 資料筆數: {}", sheetName, data.size());

        ExcelExportContext ctx = new ExcelExportContext(headers, data, sheetName);

        BusinessPipeline.start(ctx)
                .next(prepareWorkbookTask)
                .next(writeExcelContentTask)
                .next(finalizeExcelTask)
                .execute();

        return ctx.getResult();
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
