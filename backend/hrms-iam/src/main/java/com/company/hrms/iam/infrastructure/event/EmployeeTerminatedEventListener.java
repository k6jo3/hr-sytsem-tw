package com.company.hrms.iam.infrastructure.event;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 監聽 EmployeeTerminatedEvent（Kafka）
 *
 * <p>訂閱 Organization 服務發布的員工離職事件，自動停用 IAM 帳號。
 * 僅在 Kafka 啟用時生效。
 *
 * <p>Topic 命名遵循 KafkaEventPublisher 的規則：
 * aggregate-type.event-action -> employee.terminated
 *
 * <p>帳號停用規則：
 * <ul>
 *   <li>查詢 employeeId 對應的 IAM User</li>
 *   <li>呼叫 User.deactivate() 將帳號設為 INACTIVE</li>
 *   <li>若找不到對應帳號，僅記錄警告（不中斷）</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class EmployeeTerminatedEventListener {

    private final IUserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * 處理員工離職事件
     *
     * @param message Kafka 訊息（JSON 格式）
     */
    @KafkaListener(topics = "employee.terminated", groupId = "iam-service")
    @Transactional
    public void handleEmployeeTerminated(String message) {
        log.info("[EmployeeTerminatedListener] 收到員工離職事件: {}", message);

        try {
            JsonNode json = objectMapper.readTree(message);

            String employeeId = getTextOrNull(json, "employeeId");
            String fullName = getTextOrNull(json, "fullName");
            String terminationType = getTextOrNull(json, "terminationType");

            if (employeeId == null) {
                log.warn("[EmployeeTerminatedListener] 事件缺少 employeeId，跳過處理");
                return;
            }

            // 查詢對應的 IAM 帳號
            Optional<User> userOpt = userRepository.findByEmployeeId(employeeId);
            if (userOpt.isEmpty()) {
                log.warn("[EmployeeTerminatedListener] employeeId={} 找不到對應 IAM 帳號，跳過停用",
                        employeeId);
                return;
            }

            User user = userOpt.get();

            // 停用帳號
            user.deactivate();
            userRepository.update(user);

            log.info("[EmployeeTerminatedListener] 帳號已停用: username={}, employeeId={}, fullName={}, terminationType={}",
                    user.getUsername(), employeeId, fullName, terminationType);

        } catch (Exception e) {
            log.error("[EmployeeTerminatedListener] 處理員工離職事件失敗: {}", e.getMessage(), e);
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
