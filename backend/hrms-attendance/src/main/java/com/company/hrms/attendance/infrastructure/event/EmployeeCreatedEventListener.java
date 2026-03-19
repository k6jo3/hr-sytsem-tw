package com.company.hrms.attendance.infrastructure.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.company.hrms.attendance.domain.service.LeaveInitializationDomainService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 監聽 EmployeeCreatedEvent（Kafka）
 *
 * <p>訂閱 Organization 服務發布的員工建立事件，自動初始化該員工的年度假期額度。
 * 僅在 Kafka 啟用時生效。
 *
 * <p>同時監聽兩個 Topic 以相容不同的命名慣例：
 * <ul>
 *   <li>{@code employee.created} — KafkaEventPublisher 預設格式（aggregate.action）</li>
 *   <li>{@code hrms.employee.created} — 帶命名空間前綴的格式</li>
 * </ul>
 *
 * <p>處理流程：
 * <ol>
 *   <li>解析 Kafka JSON 訊息，擷取員工基本資料</li>
 *   <li>呼叫 {@link LeaveInitializationDomainService} 初始化年度假期額度</li>
 * </ol>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class EmployeeCreatedEventListener {

    private final LeaveInitializationDomainService leaveInitializationDomainService;
    private final ObjectMapper objectMapper;

    /**
     * 處理員工建立事件，初始化年度假期額度
     *
     * <p>同時監聽 {@code employee.created} 與 {@code hrms.employee.created} 兩個 Topic，
     * 確保無論事件來源使用哪種命名慣例都能正確消費。
     *
     * @param message Kafka 訊息（JSON 格式的 EmployeeCreatedEvent）
     */
    @KafkaListener(
            topics = {"employee.created", "hrms.employee.created"},
            groupId = "attendance-service"
    )
    public void handleEmployeeCreated(String message) {
        log.info("[AttendanceEmployeeCreatedListener] 收到員工建立事件: {}", message);

        try {
            JsonNode json = objectMapper.readTree(message);

            String employeeId = getTextOrNull(json, "employeeId");
            String employeeNumber = getTextOrNull(json, "employeeNumber");
            String fullName = getTextOrNull(json, "fullName");
            String hireDate = getTextOrNull(json, "hireDate");

            if (employeeId == null) {
                log.warn("[AttendanceEmployeeCreatedListener] 事件缺少 employeeId，忽略此訊息");
                return;
            }

            log.info("[AttendanceEmployeeCreatedListener] 開始初始化員工假期額度 - employeeId={}, employeeNumber={}, fullName={}, hireDate={}",
                    employeeId, employeeNumber, fullName, hireDate);

            // 呼叫 Domain Service 初始化年度假期額度
            leaveInitializationDomainService.initializeAnnualLeaveForNewEmployee(employeeId, hireDate);

            log.info("[AttendanceEmployeeCreatedListener] 員工假期額度初始化完成 - employeeId={}", employeeId);

        } catch (Exception e) {
            log.error("[AttendanceEmployeeCreatedListener] 處理員工建立事件失敗: {}", e.getMessage(), e);
            // TODO: 發送到 DLQ (Dead Letter Queue) - 待基礎設施支援
        }
    }

    /**
     * 安全地從 JsonNode 取得文字值
     *
     * @param json      JSON 節點
     * @param fieldName 欄位名稱
     * @return 欄位值，若不存在或為 null 則回傳 null
     */
    private String getTextOrNull(JsonNode json, String fieldName) {
        JsonNode node = json.get(fieldName);
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }
}
