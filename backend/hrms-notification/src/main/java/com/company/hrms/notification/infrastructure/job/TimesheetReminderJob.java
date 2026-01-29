package com.company.hrms.notification.infrastructure.job;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.notification.api.request.notification.SendNotificationRequest;
import com.company.hrms.notification.application.service.send.SendNotificationServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工時填報提醒 Job
 * <p>
 * 每日 18:00 提醒員工填寫當日工時（僅工作日）
 * </p>
 *
 * @author Claude
 * @since 2026-01-29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TimesheetReminderJob {

    private final SendNotificationServiceImpl sendNotificationService;
    // TODO: 未來應透過 Feign Client 呼叫 Timesheet Service 取得未填報工時的員工列表

    /**
     * 每日 18:00 執行
     */
    @Scheduled(cron = "0 0 18 * * ?")
    public void sendTimesheetReminders() {
        log.info("[TimesheetReminderJob] 開始執行工時填報提醒任務");

        try {
            LocalDate today = LocalDate.now();

            // 檢查是否為工作日（週一到週五）
            if (today.getDayOfWeek() == DayOfWeek.SATURDAY || today.getDayOfWeek() == DayOfWeek.SUNDAY) {
                log.info("[TimesheetReminderJob] 今日為週末，跳過工時填報提醒");
                return;
            }

            // TODO: 查詢今日尚未填寫工時的員工
            // List<Employee> employeesWithoutTimesheet =
            // timesheetRepository.findEmployeesWithoutTimesheetForDate(today);

            // 暫時實作：空列表
            List<String> employeeIds = List.of();

            int successCount = 0;
            int failCount = 0;

            for (String employeeId : employeeIds) {
                try {
                    SendNotificationRequest request = SendNotificationRequest.builder()
                            .recipientId(employeeId)
                            .notificationType("REMINDER")
                            .priority("NORMAL")
                            .title("⏰ 工時填報提醒")
                            .content(String.format("您尚未填寫今日（%s）的工時記錄，請於下班前完成填報。", today))
                            .channels(List.of("IN_APP"))
                            .businessUrl("/hr/timesheet")
                            .build();

                    sendNotificationService.execCommand(request, createSystemUser());
                    successCount++;

                } catch (Exception e) {
                    log.error("[TimesheetReminderJob] 發送工時填報提醒失敗 - 員工: {}, 錯誤: {}",
                            employeeId, e.getMessage(), e);
                    failCount++;
                }
            }

            log.info("[TimesheetReminderJob] 工時填報提醒任務完成 - 成功: {}, 失敗: {}", successCount, failCount);

        } catch (Exception e) {
            log.error("[TimesheetReminderJob] 工時填報提醒任務執行失敗: {}", e.getMessage(), e);
        }
    }

    private JWTModel createSystemUser() {
        JWTModel systemUser = new JWTModel();
        systemUser.setEmployeeNumber("SYSTEM");
        systemUser.setDisplayName("系統自動通知");
        return systemUser;
    }
}
