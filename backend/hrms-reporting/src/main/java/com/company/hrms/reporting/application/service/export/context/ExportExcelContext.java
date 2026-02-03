package com.company.hrms.reporting.application.service.export.context;

import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.reporting.api.request.ExportExcelRequest;

import lombok.Getter;
import lombok.Setter;

/**
 * Excel 匯出業務流水線上下文
 */
@Getter
@Setter
public class ExportExcelContext extends PipelineContext {
    private final ExportExcelRequest request;
    private final JWTModel currentUser;

    private List<String> headers;
    private List<List<Object>> data;
    private String sheetName;
    private byte[] excelContent;

    public ExportExcelContext(ExportExcelRequest request, JWTModel currentUser) {
        this.request = request;
        this.currentUser = currentUser;
    }
}
