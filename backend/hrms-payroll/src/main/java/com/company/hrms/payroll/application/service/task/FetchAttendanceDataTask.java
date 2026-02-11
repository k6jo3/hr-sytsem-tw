package com.company.hrms.payroll.application.service.task;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.CalculatePayrollContext;
import com.company.hrms.payroll.infrastructure.client.attendance.AttendanceServiceClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 獲取考勤數據任務
 * 從考勤服務獲取整個月的加班時數與出勤摘要
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FetchAttendanceDataTask implements PipelineTask<CalculatePayrollContext> {

    private final AttendanceServiceClient attendanceServiceClient;

    @Override
    public void execute(CalculatePayrollContext context) {
        try {
            int year = context.getPayrollRun().getPayPeriod().getStartDate().getYear();
            int month = context.getPayrollRun().getPayPeriod().getStartDate().getMonthValue();
            String orgId = context.getPayrollRun().getOrganizationId();

            log.info("正在從考勤服務獲取數據: orgId={}, year={}, month={}", orgId, year, month);

            AttendanceServiceClient.MonthlyReportResponse response = attendanceServiceClient.getMonthlyReport(orgId,
                    year, month);

            if (response != null && response.getItems() != null) {
                context.setAttendanceMap(response.getItems().stream()
                        .collect(Collectors.toMap(
                                AttendanceServiceClient.MonthlyReportItem::getEmployeeId,
                                item -> item,
                                (existing, replacement) -> existing // 避免重複 ID 導致異常
                        )));
                log.info("成功載入 {} 筆考勤數據", response.getItems().size());
            }

        } catch (Exception e) {
            log.error("獲取考勤數據失敗: {}", e.getMessage());
            // 由於核心需求是自動計算，如果不通可能需要停止或拋出異常，此處記錄日誌並繼續（將使用 0 計算）
        }
    }

    @Override
    public String getName() {
        return "FetchAttendanceDataTask";
    }
}
