package com.company.hrms.notification.infrastructure.job;

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
 * 特休到期提醒 Job
 * <p>
 * 每週一 10:00 檢查並提醒即將到期的特休假（年度結算前 30 天）
 * </p>
 *
 * @author Claude
 * @since 2026-01-29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnnualLeaveExpiryJob {

    private final SendNotificationServiceImpl sendNotificationService;

    /**
     * 每週一 10:00 執行
     */
    @Scheduled(cron = "0 0 10 ? * MON")
    public void sendAnnualLeaveExpiryReminders() {
        log.info("[AnnualLeaveExpiryJob] 開始執行特休到期提醒任務");

        try {
            LocalDate today = LocalDate.now();
            LocalDate thirtyDaysLater = today.plusDays(30);
            log.info("[AnnualLeaveExpiryJob] 檢查於 {} 之前即將到期的特休假", thirtyDaysLater);

            // TODO: 此處應透過 Feign Client 呼叫 Attendance Service 或由 Attendance Service 定期發送
            // Kafka 訊息
            // 目前暫不直接注入跨服務的 Repository 以符合微服務架構規範
            // List<AnnualLeaveExpiry> expiringLeaves =
            // attendanceClient.findExpiringAnnualLeave(thirtyDaysLater);

            // 暫時實作：空列表
            List<AnnualLeaveExpiry> expiringLeaves = List.of();

            int successCount = 0;
            int failCount = 0;

            for (AnnualLeaveExpiry leave : expiringLeaves) {
                try {
                    SendNotificationRequest request = SendNotificationRequest.builder()
                            .recipientId(leave.employeeId())
                            .notificationType("REMINDER")
                            .priority("NORMAL")
                            .title("🏖️ 特休假即將到期提醒")
                            .content(String.format("您還有 %.1f 天特休假尚未使用，將於 %s 到期失效，請儘速安排休假。",
                                    leave.remainingDays(), leave.expiryDate()))
                            .channels(List.of("IN_APP", "EMAIL"))
                            .businessUrl("/hr/leaves/balance")
                            .build();

                    sendNotificationService.execCommand(request, createSystemUser());
                    successCount++;

                } catch (Exception e) {
                    log.error("[AnnualLeaveExpiryJob] 發送特休到期提醒失敗 - 員工: {}, 錯誤: {}",
                            leave.employeeId(), e.getMessage(), e);
                    failCount++;
                }
            }

            log.info("[AnnualLeaveExpiryJob] 特休到期提醒任務完成 - 成功: {}, 失敗: {}", successCount, failCount);

        } catch (Exception e) {
            log.error("[AnnualLeaveExpiryJob] 特休到期提醒任務執行失敗: {}", e.getMessage(), e);
        }
    }

    private JWTModel createSystemUser() {
        JWTModel systemUser = new JWTModel();
        systemUser.setEmployeeNumber("SYSTEM");
        systemUser.setDisplayName("系統自動通知");
        return systemUser;
    }

    /**
     * 特休到期資訊（暫時用 record）
     */
    private record AnnualLeaveExpiry(
            String employeeId,
            double remainingDays,
            LocalDate expiryDate) {
    }
}
