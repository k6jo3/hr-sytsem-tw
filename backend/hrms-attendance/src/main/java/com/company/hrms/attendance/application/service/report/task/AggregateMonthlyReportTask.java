package com.company.hrms.attendance.application.service.report.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.company.hrms.attendance.api.response.report.MonthlyReportResponse;
import com.company.hrms.attendance.application.service.report.context.MonthlyReportContext;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.infrastructure.client.organization.dto.EmployeeDto;
import com.company.hrms.common.application.pipeline.PipelineTask;

import lombok.RequiredArgsConstructor;

/**
 * 彙總月報表數據 Task
 */
@Component
@RequiredArgsConstructor
public class AggregateMonthlyReportTask implements PipelineTask<MonthlyReportContext> {

    @Override
    public void execute(MonthlyReportContext context) throws Exception {
        int year = context.getRequest().getYear();
        int month = context.getRequest().getMonth();

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth());

        // 應出勤天數 (週一至週五)
        int scheduledDaysInMonth = calculateWorkDays(firstDay, lastDay);

        List<EmployeeDto> employees = context.getEmployees();
        Map<String, List<AttendanceRecord>> recordMap = context.getEmployeeRecordsMap();
        Map<String, List<LeaveApplication>> leaveMap = context.getEmployeeLeavesMap();
        Map<String, List<OvertimeApplication>> overtimeMap = context.getEmployeeOvertimesMap();

        List<MonthlyReportResponse.MonthlyReportItem> items = new ArrayList<>();

        int totalLateCount = 0;
        int totalEarlyLeaveCount = 0;
        BigDecimal totalOvertimeHours = BigDecimal.ZERO;
        BigDecimal totalAttendanceRate = BigDecimal.ZERO;

        for (EmployeeDto emp : employees) {
            MonthlyReportResponse.MonthlyReportItem item = new MonthlyReportResponse.MonthlyReportItem();
            item.setEmployeeId(emp.getEmployeeId());
            item.setEmployeeName(emp.getFullName());
            item.setEmployeeNumber(emp.getEmployeeNumber());
            item.setDepartmentName(emp.getDepartmentName());

            // 1. 應出勤天數
            item.setScheduledDays(BigDecimal.valueOf(scheduledDaysInMonth));

            // 2. 實際出勤天數 & 遲到早退 & 總工時
            List<AttendanceRecord> records = recordMap.get(emp.getEmployeeId());
            int actualDays = 0;
            int lateCount = 0;
            int earlyLeaveCount = 0;
            double workHours = 0.0;

            if (records != null) {
                actualDays = records.size(); // 假設每天只有一條成功打卡記錄
                for (AttendanceRecord r : records) {
                    if (r.isLate())
                        lateCount++;
                    if (r.isEarlyLeave())
                        earlyLeaveCount++;

                    if (r.getCheckInTime() != null && r.getCheckOutTime() != null) {
                        workHours += Duration.between(r.getCheckInTime(), r.getCheckOutTime()).toMinutes() / 60.0;
                    }
                }
            }

            item.setActualDays(BigDecimal.valueOf(actualDays));
            item.setLateCount(lateCount);
            item.setEarlyLeaveCount(earlyLeaveCount);
            item.setTotalWorkHours(BigDecimal.valueOf(workHours).setScale(2, RoundingMode.HALF_UP));

            // 3. 請假天數
            List<LeaveApplication> leaves = leaveMap.get(emp.getEmployeeId());
            double leaveDays = 0.0;
            if (leaves != null) {
                for (LeaveApplication l : leaves) {
                    // 簡化邏輯：計算該月內的請假天數
                    LocalDate start = l.getStartDate().isBefore(firstDay) ? firstDay : l.getStartDate();
                    LocalDate end = l.getEndDate().isAfter(lastDay) ? lastDay : l.getEndDate();

                    if (!start.isAfter(end)) {
                        // 這裡應該根據 LeaveApplication 的實際小時/天數來計算，暫時用日期差距簡化
                        // 完整版本應該檢查 l.getTotalDays() 並根據範圍比例計算
                        long days = Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays() + 1;
                        leaveDays += days;
                    }
                }
            }
            item.setLeaveDays(BigDecimal.valueOf(leaveDays));

            // 4. 缺勤天數
            double absentDays = Math.max(0, scheduledDaysInMonth - actualDays - leaveDays);
            item.setAbsentDays(BigDecimal.valueOf(absentDays));

            // 5. 加班時數 (含明細)
            List<OvertimeApplication> overtimes = overtimeMap.get(emp.getEmployeeId());
            double totalOt = 0.0;
            double workdayOt = 0.0;
            double restDayOt = 0.0;
            double holidayOt = 0.0;

            if (overtimes != null) {
                for (OvertimeApplication ot : overtimes) {
                    if (ot.getStatus() == com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus.APPROVED) {
                        double hrs = ot.getHours();
                        totalOt += hrs;
                        switch (ot.getOvertimeType()) {
                            case WORKDAY:
                                workdayOt += hrs;
                                break;
                            case REST_DAY:
                                restDayOt += hrs;
                                break;
                            case HOLIDAY:
                                holidayOt += hrs;
                                break;
                        }
                    }
                }
            }
            item.setOvertimeHours(BigDecimal.valueOf(totalOt));
            item.setWorkdayOvertimeHours(BigDecimal.valueOf(workdayOt));
            item.setRestDayOvertimeHours(BigDecimal.valueOf(restDayOt));
            item.setHolidayOvertimeHours(BigDecimal.valueOf(holidayOt));

            items.add(item);

            // 累計全體數據
            totalLateCount += lateCount;
            totalEarlyLeaveCount += earlyLeaveCount;
            totalOvertimeHours = totalOvertimeHours.add(item.getOvertimeHours());

            if (scheduledDaysInMonth > 0) {
                BigDecimal rate = BigDecimal.valueOf(actualDays).divide(BigDecimal.valueOf(scheduledDaysInMonth), 4,
                        RoundingMode.HALF_UP);
                totalAttendanceRate = totalAttendanceRate.add(rate);
            }
        }

        // Summary
        int empCount = employees.size();
        MonthlyReportResponse.ReportSummary summary = MonthlyReportResponse.ReportSummary.builder()
                .totalEmployees(empCount)
                .averageAttendanceRate(empCount > 0
                        ? totalAttendanceRate.divide(BigDecimal.valueOf(empCount), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                        : BigDecimal.ZERO)
                .totalLateCount(totalLateCount)
                .totalEarlyLeaveCount(totalEarlyLeaveCount)
                .totalOvertimeHours(totalOvertimeHours)
                .build();

        context.setResponse(MonthlyReportResponse.builder()
                .year(year)
                .month(month)
                .items(items)
                .summary(summary)
                .build());
    }

    private int calculateWorkDays(LocalDate start, LocalDate end) {
        int workDays = 0;
        LocalDate current = start;
        while (!current.isAfter(end)) {
            DayOfWeek day = current.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                workDays++;
            }
            current = current.plusDays(1);
        }
        return workDays;
    }
}
