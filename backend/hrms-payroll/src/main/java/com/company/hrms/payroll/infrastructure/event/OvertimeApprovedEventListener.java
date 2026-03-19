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
 * 監聽加班核准事件（Kafka）
 *
 * <p>訂閱 Attendance 服務發布的加班核准事件，記錄待計算的加班費項目。
 * 支援兩個 Topic：{@code overtime.approved}（簡稱）與 {@code hrms.overtime.approved}（完整命名空間）。
 * 僅在 Kafka 啟用時生效。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class OvertimeApprovedEventListener {

    private final OvertimePayrollEventHandler overtimePayrollEventHandler;
    private final ObjectMapper objectMapper;

    /**
     * 處理加班核准事件
     *
     * <p>從 JSON 訊息中解析員工 ID、加班類型、加班時數等資訊，
     * 並委派給 {@link OvertimePayrollEventHandler} 記錄待計算的加班費。
     *
     * @param message Kafka JSON 訊息
     */
    @KafkaListener(topics = {"overtime.approved", "hrms.overtime.approved"}, groupId = "payroll-service")
    public void handleOvertimeApproved(String message) {
        log.info("[OvertimeApprovedListener] 收到加班核准事件: {}", message);

        try {
            JsonNode json = objectMapper.readTree(message);
            String employeeId = json.path("employeeId").asText();
            String applicationId = json.path("applicationId").asText();
            String overtimeType = json.path("overtimeType").asText(null);
            BigDecimal overtimeHours = parseBigDecimal(json.path("overtimeHours"));
            String approvedBy = json.path("approvedBy").asText(null);

            log.info("[OvertimeApprovedListener] 員工 ID={}, 加班單號={}, 加班類型={}, 時數={}, 核准人={}",
                    employeeId, applicationId, overtimeType, overtimeHours, approvedBy);

            overtimePayrollEventHandler.recordOvertimePay(
                    employeeId, applicationId, overtimeType, overtimeHours);

        } catch (Exception e) {
            log.error("[OvertimeApprovedListener] 處理加班核准事件失敗: {}", e.getMessage(), e);
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
     * 加班薪資事件處理介面
     *
     * <p>由 Application 層實作，負責將加班核准事件轉化為加班費計算記錄。
     */
    public interface OvertimePayrollEventHandler {

        /**
         * 記錄加班對應的加班費
         *
         * @param employeeId    員工 ID
         * @param applicationId 加班單號
         * @param overtimeType  加班類型（如：平日、休息日、國定假日）
         * @param overtimeHours 加班時數（可為 null）
         */
        void recordOvertimePay(String employeeId, String applicationId,
                               String overtimeType, BigDecimal overtimeHours);
    }
}
