package com.company.hrms.insurance.infrastructure.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.company.hrms.insurance.domain.service.InsuranceLevelMatchingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 監聽薪資結構變更事件（Kafka）
 *
 * <p>訂閱 Payroll 服務發布的薪資結構變更事件，觸發保費級距重新匹配。
 * 當員工薪資結構發生變更時，需要重新計算其對應的投保級距，
 * 以確保勞健保費用與實際薪資一致。
 *
 * <p>僅在 Kafka 啟用時生效（透過 {@code spring.kafka.enabled=true} 控制）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class SalaryStructureChangedEventListener {

    private final InsuranceLevelMatchingService insuranceLevelMatchingService;
    private final ObjectMapper objectMapper;

    /**
     * 處理薪資結構變更事件
     *
     * <p>同時監聽兩個 Topic：
     * <ul>
     *   <li>{@code salary-structure-changed} — 簡短格式（向下相容）</li>
     *   <li>{@code hrms.salary-structure-changed} — 命名空間格式（標準）</li>
     * </ul>
     *
     * <p>事件欄位（來自 {@code SalaryStructureChangedEvent}）：
     * <ul>
     *   <li>{@code structureId} — 薪資結構 ID</li>
     *   <li>{@code employeeId} — 員工 ID</li>
     *   <li>{@code reason} — 變更原因</li>
     *   <li>{@code occurredAt} — 事件發生時間</li>
     * </ul>
     *
     * @param message Kafka 訊息（JSON 格式）
     */
    @KafkaListener(
            topics = {"salary-structure-changed", "hrms.salary-structure-changed"},
            groupId = "insurance-service"
    )
    public void handleSalaryStructureChanged(String message) {
        log.info("[SalaryStructureChangedListener] 收到薪資結構變更事件: {}", message);

        try {
            JsonNode json = objectMapper.readTree(message);

            String structureId = json.path("structureId").asText(null);
            String employeeId = json.path("employeeId").asText(null);
            String reason = json.path("reason").asText(null);
            String occurredAt = json.path("occurredAt").asText(null);

            // 驗證必要欄位
            if (employeeId == null || employeeId.isBlank()) {
                log.warn("[SalaryStructureChangedListener] 事件缺少 employeeId，忽略此事件: {}", message);
                return;
            }

            log.info("[SalaryStructureChangedListener] 開始處理級距重算 — 員工ID: {}, 薪資結構ID: {}, 變更原因: {}, 發生時間: {}",
                    employeeId, structureId, reason, occurredAt);

            // 呼叫 Domain Service 執行級距重新匹配
            insuranceLevelMatchingService.recalculateLevelForEmployee(employeeId);

            log.info("[SalaryStructureChangedListener] 級距重算完成 — 員工ID: {}", employeeId);

        } catch (Exception e) {
            // 記錄錯誤但不拋出，避免 Kafka consumer 中斷
            log.error("[SalaryStructureChangedListener] 處理薪資結構變更事件失敗: {}", e.getMessage(), e);
        }
    }
}
