package com.company.hrms.reporting.application.service.report.context;

import java.util.List;
import java.util.Map;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.reporting.api.request.GetHeadcountReportRequest;
import com.company.hrms.reporting.api.response.HeadcountReportResponse;
import com.company.hrms.reporting.api.response.HeadcountReportResponse.HeadcountItem;
import com.company.hrms.reporting.api.response.HeadcountReportResponse.HeadcountSummary;
import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 人力盤點報表上下文
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetHeadcountReportContext extends PipelineContext {

    // 輸入
    private final GetHeadcountReportRequest request;
    private final String tenantId;

    // 中間數據
    private List<EmployeeRosterReadModel> employees;
    private Map<String, List<EmployeeRosterReadModel>> groupedData;
    private List<HeadcountItem> items;
    private HeadcountSummary summary;

    // 輸出
    private HeadcountReportResponse response;

    public GetHeadcountReportContext(GetHeadcountReportRequest request, String tenantId) {
        this.request = request;
        this.tenantId = tenantId;
    }
}
