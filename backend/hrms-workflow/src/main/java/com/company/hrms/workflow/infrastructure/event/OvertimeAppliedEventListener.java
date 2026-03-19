package com.company.hrms.workflow.infrastructure.event;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.company.hrms.workflow.application.service.WorkflowEventApplicationService;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 監聽加班申請事件（Kafka）
 *
 * <p>訂閱 Attendance 服務發布的加班申請事件，
 * 自動建立對應的 OVERTIME_APPROVAL 簽核流程實例。
 *
 * <p>支援兩個 Topic：
 * <ul>
 *   <li>{@code overtimeapplication.applied} — KafkaEventPublisher 自動生成的 topic 名稱</li>
 *   <li>{@code overtime.applied} — 簡稱（向前相容）</li>
 * </ul>
 *
 * <p>僅在 Kafka 啟用時生效（{@code spring.kafka.enabled=true}）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class OvertimeAppliedEventListener {

    private final WorkflowEventApplicationService workflowEventApplicationService;
    private final ObjectMapper objectMapper;

    /**
     * 處理加班申請事件
     *
     * <p>從 JSON 訊息解析申請單號、員工 ID、加班時數等資訊，
     * 並透過 {@link WorkflowEventApplicationService} 建立 OVERTIME_APPROVAL 流程實例。
     *
     * @param message Kafka JSON 訊息
     */
    @KafkaListener(topics = {"overtimeapplication.applied", "overtime.applied"}, groupId = "${spring.kafka.consumer.group-id:hrms-workflow-group}")
    public void handleOvertimeApplied(String message) {
        log.info("[OvertimeAppliedListener] 收到加班申請事件: {}", message);

        try {
            JsonNode json = objectMapper.readTree(message);

            String applicationId = json.path("applicationId").asText(null);
            String employeeId = json.path("employeeId").asText(null);
            Double hours = parseDouble(json.path("hours"));

            if (applicationId == null || employeeId == null) {
                log.warn("[OvertimeAppliedListener] 事件缺少必要欄位（applicationId 或 employeeId），忽略此事件");
                return;
            }

            // 組裝流程變數
            Map<String, Object> variables = new HashMap<>();
            variables.put("applicationId", applicationId);
            if (hours != null) {
                variables.put("hours", hours);
            }

            String summary = String.format("加班申請 - 員工:%s, 時數:%s", employeeId, hours);

            workflowEventApplicationService.startWorkflowByEvent(
                    FlowType.OVERTIME_APPROVAL,
                    employeeId,
                    applicationId,
                    "OVERTIME",
                    summary,
                    variables);

            log.info("[OvertimeAppliedListener] 已為加班單 {} 建立簽核流程", applicationId);

        } catch (Exception e) {
            log.error("[OvertimeAppliedListener] 處理加班申請事件失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * 安全解析 Double，若欄位缺失或格式錯誤則回傳 null
     */
    private Double parseDouble(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        try {
            return Double.parseDouble(node.asText());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
