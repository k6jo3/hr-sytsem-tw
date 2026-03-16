package com.company.hrms.iam.infrastructure.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.company.hrms.iam.application.service.user.AutoCreateUserFromEmployeeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 監聽 EmployeeCreatedEvent（Kafka）
 *
 * <p>訂閱 Organization 服務發布的員工建立事件，自動建立 IAM 帳號。
 * 僅在 Kafka 啟用時生效。
 *
 * <p>Topic 命名遵循 KafkaEventPublisher 的規則：
 * aggregate-type.event-action → employee.created
 *
 * <p>帳號建立規則：
 * <ul>
 *   <li>username: 使用 employeeNumber（員工編號）</li>
 *   <li>email: 使用 companyEmail</li>
 *   <li>displayName: 使用 fullName</li>
 *   <li>password: 系統自動產生</li>
 *   <li>角色: 預設指派 EMPLOYEE 角色</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class EmployeeCreatedEventListener {

    private final AutoCreateUserFromEmployeeService autoCreateUserService;
    private final ObjectMapper objectMapper;

    /**
     * 處理員工建立事件
     *
     * @param message Kafka 訊息（JSON 格式）
     */
    @KafkaListener(topics = "employee.created", groupId = "iam-service")
    public void handleEmployeeCreated(String message) {
        log.info("[EmployeeCreatedListener] 收到員工建立事件: {}", message);

        try {
            JsonNode json = objectMapper.readTree(message);

            EmployeeCreatedEventDto dto = EmployeeCreatedEventDto.builder()
                    .employeeId(getTextOrNull(json, "employeeId"))
                    .employeeNumber(getTextOrNull(json, "employeeNumber"))
                    .fullName(getTextOrNull(json, "fullName"))
                    .companyEmail(getTextOrNull(json, "companyEmail"))
                    .organizationId(getTextOrNull(json, "organizationId"))
                    .departmentId(getTextOrNull(json, "departmentId"))
                    .jobTitle(getTextOrNull(json, "jobTitle"))
                    .hireDate(getTextOrNull(json, "hireDate"))
                    .build();

            autoCreateUserService.createUserForEmployee(dto);

        } catch (Exception e) {
            log.error("[EmployeeCreatedListener] 處理員工建立事件失敗: {}", e.getMessage(), e);
            // TODO: 發送到 DLQ (Dead Letter Queue) - 待基礎設施支援
        }
    }

    /**
     * 安全地從 JsonNode 取得文字值
     */
    private String getTextOrNull(JsonNode json, String fieldName) {
        JsonNode node = json.get(fieldName);
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }
}
