package com.company.hrms.reporting.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 政府報表匯出請求事件
 */
@Getter
@NoArgsConstructor
public class GovernmentReportExportRequestedEvent extends DomainEvent {
    private String exportId;
    private String formatType;
    private String period;

    public GovernmentReportExportRequestedEvent(String exportId, String formatType, String period) {
        super();
        this.exportId = exportId;
        this.formatType = formatType;
        this.period = period;
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
