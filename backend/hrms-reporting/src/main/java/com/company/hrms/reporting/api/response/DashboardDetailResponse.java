package com.company.hrms.reporting.api.response;

import lombok.Data;
import java.util.List;

@Data
public class DashboardDetailResponse {
    private String dashboardId;
    private String dashboardName;
    private String description;
    private Boolean isDefault;
    private List<WidgetDetail> widgets;

    @Data
    public static class WidgetDetail {
        private String widgetId;
        private String widgetType;
        private String title;
        private Object config;
    }
}
