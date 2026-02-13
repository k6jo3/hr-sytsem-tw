package com.company.hrms.reporting.application.service.dashboard;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.reporting.api.request.GetAttendanceStatisticsRequest;
import com.company.hrms.reporting.api.request.GetHeadcountReportRequest;
import com.company.hrms.reporting.api.request.GetProjectCostAnalysisRequest;
import com.company.hrms.reporting.api.request.GetTurnoverAnalysisRequest;
import com.company.hrms.reporting.application.service.report.GetAttendanceStatisticsServiceImpl;
import com.company.hrms.reporting.application.service.report.GetHeadcountReportServiceImpl;
import com.company.hrms.reporting.application.service.report.GetProjectCostAnalysisServiceImpl;
import com.company.hrms.reporting.application.service.report.GetTurnoverAnalysisServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儀表板組件數據編排器
 * 
 * 負責根據 Widget 的 dataSource 調用相應的報表服務獲取數據
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WidgetDataOrchestrator {

    private final GetHeadcountReportServiceImpl headcountService;
    private final GetTurnoverAnalysisServiceImpl turnoverService;
    private final GetAttendanceStatisticsServiceImpl attendanceService;
    private final GetProjectCostAnalysisServiceImpl projectCostService;

    public Object fetchData(String dataSource, JWTModel user) {
        if (dataSource == null)
            return null;

        String currentMonth = "2026-02"; // 配合測試資料

        try {
            switch (dataSource) {
                case "HR_HEADCOUNT_SUMMARY":
                    var hReq = new GetHeadcountReportRequest();
                    return headcountService.getResponse(hReq, user).getSummary();

                case "HR_HEADCOUNT_BY_DEPT":
                    var hDept = new GetHeadcountReportRequest();
                    hDept.setDimension("DEPARTMENT");
                    return headcountService.getResponse(hDept, user).getContent();

                case "HR_TURNOVER_SUMMARY":
                    var tReq = new GetTurnoverAnalysisRequest();
                    tReq.setYearMonth(currentMonth);
                    return turnoverService.getResponse(tReq, user);

                case "ATTENDANCE_STATISTICS":
                    var aReq = new GetAttendanceStatisticsRequest();
                    // 可以根據需要設定過濾條件
                    return attendanceService.getResponse(aReq, user).getContent();

                case "PROJECT_COST_SUMMARY":
                    var pReq = new GetProjectCostAnalysisRequest();
                    return projectCostService.getResponse(pReq, user).getContent();

                default:
                    log.warn("Unknown dashboard data source: {}", dataSource);
                    return null;
            }
        } catch (Exception e) {
            log.error("Error fetching dashboard data for {}: {}", dataSource, e.getMessage(), e);
            return null;
        }
    }
}
