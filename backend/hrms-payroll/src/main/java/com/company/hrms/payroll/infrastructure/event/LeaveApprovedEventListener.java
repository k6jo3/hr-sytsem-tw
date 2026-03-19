package com.company.hrms.payroll.infrastructure.event;

import java.math.BigDecimal;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 監聽請假核准事件（Kafka）
 *
 * <p>訂閱 Attendance 服務發布的請假核准事件，記錄待計算的薪資扣款項。
 * 支援兩個 Topic：{@code leave.approved}（簡稱）與 {@code hrms.leave.approved}（完整命名空間）。
 * 僅在 Kafka 啟用時生效。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class LeaveApprovedEventListener {

    private final LeavePayrollEventHandler leavePayrollEventHandler;
    private final ObjectMapper objectMapper;

    /**
     * 處理請假核准事件
     *
     * <p>從 JSON 訊息中解析員工 ID、請假類型、請假天數等資訊，
     * 並委派給 {@link LeavePayrollEventHandler} 記錄待計算的薪資扣款。
     *
     * @param message Kafka JSON 訊息
     */
    @KafkaListener(topics = {"leave.approved", "hrms.leave.approved"}, groupId = "payroll-service")
    public void handleLeaveApproved(String message) {
        log.info("[LeaveApprovedListener] 收到請假核准事件: {}", message);

        try {
            JsonNode json = objectMapper.readTree(message);
            String employeeId = json.path("employeeId").asText();
            String applicationId = json.path("applicationId").asText();
            String leaveType = json.path("leaveType").asText(null);
            BigDecimal leaveDays = parseBigDecimal(json.path("leaveDays"));
            BigDecimal leaveHours = parseBigDecimal(json.path("leaveHours"));
            String approvedBy = json.path("approvedBy").asText(null);

            log.info("[LeaveApprovedListener] 員工 ID={}, 請假單號={}, 請假類型={}, 天數={}, 時數={}, 核准人={}",
                    employeeId, applicationId, leaveType, leaveDays, leaveHours, approvedBy);

            leavePayrollEventHandler.recordLeaveDeduction(
                    employeeId, applicationId, leaveType, leaveDays, leaveHours);

        } catch (Exception e) {
            log.error("[LeaveApprovedListener] 處理請假核准事件失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * 安全解析 BigDecimal，若欄位缺失或格式錯誤則回傳 null
     */
    private BigDecimal parseBigDecimal(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        try {
            return new BigDecimal(node.asText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 請假薪資事件處理介面
     *
     * <p>由 Application 層實作，負責將請假核准事件轉化為薪資扣款記錄。
     */
    public interface LeavePayrollEventHandler {

        /**
         * 記錄請假對應的薪資扣款
         *
         * @param employeeId    員工 ID
         * @param applicationId 請假單號
         * @param leaveType     請假類型（如：事假、病假、特休等）
         * @param leaveDays     請假天數（可為 null）
         * @param leaveHours    請假時數（可為 null）
         */
        void recordLeaveDeduction(String employeeId, String applicationId,
                                  String leaveType, BigDecimal leaveDays, BigDecimal leaveHours);
    }
}
