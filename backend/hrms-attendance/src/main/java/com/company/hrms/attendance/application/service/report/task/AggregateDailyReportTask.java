package com.company.hrms.attendance.application.service.report.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.response.report.DailyReportResponse;
import com.company.hrms.attendance.api.response.report.DailyReportResponse.DailyReportItem;
import com.company.hrms.attendance.api.response.report.DailyReportResponse.DailySummary;
import com.company.hrms.attendance.application.service.report.context.DailyReportContext;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.infrastructure.client.organization.dto.EmployeeDto;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;

/**
 * 彙總日報表數據 Task
 * 遵循 Clean Code 原則，將複雜邏輯拆分為私有方法
 */
@Component
@RequiredArgsConstructor
public class AggregateDailyReportTask implements PipelineTask<DailyReportContext> {

    @Override
    public void execute(DailyReportContext context) throws Exception {
        LocalDate reportDate = context.getRequest().getDate();

        // 1. 建立班別 Lookup Map 以提升查詢效能
        Map<String, Shift> shiftMap = context.getAllShifts().stream()
                .collect(Collectors.toMap(s -> s.getId().getValue(), s -> s));

        // 2. 處理每一位員工的報表明細 (Map Phase)
        List<DailyReportItem> items = context.getEmployees().stream()
                .map(emp -> buildReportItem(emp, reportDate, context, shiftMap))
                .collect(Collectors.toList());

        // 3. 根據明細彙總摘要數據 (Reduce Phase)
        DailySummary summary = calculateSummary(items);

        // 4. 設定回應
        context.setResponse(DailyReportResponse.builder()
                .reportDate(reportDate)
                .items(items)
                .summary(summary)
                .build());
    }

    /**
     * 建立單一員工的日報表明細
     */
    private DailyReportItem buildReportItem(EmployeeDto emp, LocalDate reportDate, DailyReportContext context,
            Map<String, Shift> shiftMap) {
        String empId = emp.getEmployeeId();

        // 取得相關數據
        AttendanceRecord record = getFirstOrNull(context.getEmployeeRecordsMap().get(empId));
        List<LeaveApplication> leaves = context.getEmployeeLeavesMap().get(empId);
        List<OvertimeApplication> overtimes = context.getEmployeeOvertimesMap().get(empId);

        boolean isOnLeave = leaves != null && !leaves.isEmpty();

        DailyReportItem item = DailyReportItem.builder()
                .employeeId(empId)
                .employeeName(emp.getFullName())
                .employeeNumber(emp.getEmployeeNumber())
                .departmentName(emp.getDepartmentName())
                .build();

        // 處理班別資訊
        if (record != null && record.getShiftId() != null) {
            Shift shift = shiftMap.get(record.getShiftId());
            if (shift != null) {
                item.setShiftName(shift.getName());
            }
        }

        // 處理打卡資訊
        if (record != null) {
            item.setCheckInTime(record.getCheckInTime() != null ? record.getCheckInTime().toLocalTime() : null);
            item.setCheckOutTime(record.getCheckOutTime() != null ? record.getCheckOutTime().toLocalTime() : null);
            item.setLateMinutes(record.getLateMinutes());
            item.setEarlyLeaveMinutes(record.getEarlyLeaveMinutes());
        }

        // 處理加班時數
        item.setOvertimeHours(calculateOvertimeHours(overtimes));

        // 判定最終出勤狀態
        item.setAttendanceStatus(determineStatus(reportDate, isOnLeave, record));

        return item;
    }

    /**
     * 判定考勤狀態邏輯
     * 遵循 Decision 與 Orchestration 分離原則
     */
    private String determineStatus(LocalDate reportDate, boolean isOnLeave, AttendanceRecord record) {
        if (isOnLeave) {
            return "LEAVE";
        }

        if (record != null) {
            if (record.isLate() && record.isEarlyLeave()) {
                return "LATE_AND_EARLY_LEAVE";
            }
            if (record.isLate()) {
                return "LATE";
            }
            if (record.isEarlyLeave()) {
                return "EARLY_LEAVE";
            }
            return "NORMAL";
        }

        // 判定缺勤或假日
        boolean isWorkDay = reportDate.getDayOfWeek().getValue() <= 5;
        return isWorkDay ? "ABSENT" : "HOLIDAY";
    }

    /**
     * 計算加班總時數
     */
    private BigDecimal calculateOvertimeHours(List<OvertimeApplication> overtimes) {
        if (overtimes == null || overtimes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        double total = overtimes.stream()
                .mapToDouble(OvertimeApplication::getHours)
                .sum();
        return BigDecimal.valueOf(total);
    }

    /**
     * 彙總摘要數據
     */
    private DailySummary calculateSummary(List<DailyReportItem> items) {
        int scheduledCount = 0;
        int actualCount = 0;
        int lateCount = 0;
        int earlyLeaveCount = 0;
        int absentCount = 0;
        int leaveCount = 0;

        for (DailyReportItem item : items) {
            String status = item.getAttendanceStatus();

            // 應出勤判定: 非 HOLIDAY 且 非 LEAVE 的情況
            switch (status) {
                case "LEAVE":
                    leaveCount++;
                    break;
                case "ABSENT":
                    absentCount++;
                    scheduledCount++;
                    break;
                case "LATE":
                    lateCount++;
                    actualCount++;
                    scheduledCount++;
                    break;
                case "EARLY_LEAVE":
                    earlyLeaveCount++;
                    actualCount++;
                    scheduledCount++;
                    break;
                case "LATE_AND_EARLY_LEAVE":
                    lateCount++;
                    earlyLeaveCount++;
                    actualCount++;
                    scheduledCount++;
                    break;
                case "NORMAL":
                    actualCount++;
                    scheduledCount++;
                    break;
                default: // HOLIDAY
                    break;
            }
        }

        BigDecimal rate = scheduledCount > 0
                ? BigDecimal.valueOf(actualCount)
                        .divide(BigDecimal.valueOf(scheduledCount), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return DailySummary.builder()
                .scheduledCount(scheduledCount)
                .actualCount(actualCount)
                .lateCount(lateCount)
                .earlyLeaveCount(earlyLeaveCount)
                .absentCount(absentCount)
                .leaveCount(leaveCount)
                .attendanceRate(rate)
                .build();
    }

    private <T> T getFirstOrNull(List<T> list) {
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }
}
