package com.company.hrms.reporting.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 報表匯出請求事件
 */
@Getter
@NoArgsConstructor
public class ReportExportRequestedEvent extends DomainEvent {
    private String exportId;
    private String reportType;
    private String format;

    public ReportExportRequestedEvent(String exportId, String reportType, String format) {
        super();
        this.exportId = exportId;
        this.reportType = reportType;
        this.format = format;
    }

    @Override
    public String getAggregateId() {
        return exportId;
    }

    @Override
    public String getAggregateType() {
        return "REPORT_EXPORT";
    }
}
