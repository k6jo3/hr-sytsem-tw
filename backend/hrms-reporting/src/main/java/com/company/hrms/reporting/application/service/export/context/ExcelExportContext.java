package com.company.hrms.reporting.application.service.export.context;

import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import com.company.hrms.common.application.pipeline.PipelineContext;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Excel 匯出上下文
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExcelExportContext extends PipelineContext {
    // 輸入
    private final List<String> headers;
    private final List<List<Object>> data;
    private final String sheetName;

    // 中間數據
    private Workbook workbook;
    private CellStyle headerStyle;
    private CellStyle dataStyle;

    // 輸出
    private byte[] result;

    public ExcelExportContext(List<String> headers, List<List<Object>> data, String sheetName) {
        this.headers = headers;
        this.data = data;
        this.sheetName = sheetName;
    }
}
