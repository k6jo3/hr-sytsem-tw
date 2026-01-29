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
 * 合約到期提醒 Job
 * <p>
 * 每日 09:00 檢查並提醒即將到期的合約（提前 30 天、7 天、1 天）
 * </p>
 *
 * @author Claude
 * @since 2026-01-29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContractExpiryJob {

    private final SendNotificationServiceImpl sendNotificationService;
    // TODO: 注入 EmployeeRepository 或 ContractRepository

    /**
     * 每日 09:00 執行
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendContractExpiryReminders() {
        log.info("[ContractExpiryJob] 開始執行合約到期提醒任務");

        try {
            LocalDate today = LocalDate.now();

            // 檢查三個時間點：30天後、7天後、明天
            sendRemindersForDaysAhead(30, "即將到期");
            sendRemindersForDaysAhead(7, "即將到期");
            sendRemindersForDaysAhead(1, "明天到期");

            log.info("[ContractExpiryJob] 合約到期提醒任務完成");

        } catch (Exception e) {
            log.error("[ContractExpiryJob] 合約到期提醒任務執行失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * 發送指定天數後到期的提醒
     */
    private void sendRemindersForDaysAhead(int daysAhead, String urgencyLabel) {
        try {
            LocalDate expiryDate = LocalDate.now().plusDays(daysAhead);

            // TODO: 查詢指定日期到期的合約
            // List<Contract> expiringContracts =
            // contractRepository.findByExpiryDate(expiryDate);

            // 暫時實作：空列表
            List<ContractExpiry> expiringContracts = List.of();

            for (ContractExpiry contract : expiringContracts) {
                try {
                    String priority = daysAhead <= 7 ? "HIGH" : "NORMAL";

                    SendNotificationRequest request = SendNotificationRequest.builder()
                            .recipientId(contract.employeeId())
                            .notificationType("REMINDER")
                            .priority(priority)
                            .title(String.format("⚠️ 合約%s提醒", urgencyLabel))
                            .content(String.format("您的勞動合約將於 %d 天後（%s）到期，請儘速辦理續約或離職手續。",
                                    daysAhead, expiryDate))
                            .channels(List.of("IN_APP", "EMAIL"))
                            .businessUrl("/hr/contracts/" + contract.contractId())
                            .build();

                    sendNotificationService.execCommand(request, createSystemUser());

                } catch (Exception e) {
                    log.error("[ContractExpiryJob] 發送合約到期提醒失敗 - 員工: {}, 錯誤: {}",
                            contract.employeeId(), e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error("[ContractExpiryJob] 處理 {} 天後到期合約失敗: {}", daysAhead, e.getMessage(), e);
        }
    }

    private JWTModel createSystemUser() {
        JWTModel systemUser = new JWTModel();
        systemUser.setEmployeeNumber("SYSTEM");
        systemUser.setDisplayName("系統自動通知");
        return systemUser;
    }

    /**
     * 合約到期資訊（暫時用 record）
     */
    private record ContractExpiry(String contractId, String employeeId, LocalDate expiryDate) {
    }
}
