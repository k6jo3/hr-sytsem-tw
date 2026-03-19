package com.company.hrms.organization.infrastructure.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.EmploymentType;
import com.company.hrms.organization.domain.model.valueobject.Gender;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.infrastructure.event.service.CandidateOnboardingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 候選人錄取事件監聽器
 *
 * <p>訂閱 Recruitment 服務發布的候選人錄取事件（CandidateHiredEvent），
 * 自動建立員工檔案，預設狀態為試用期（PROBATION）。
 *
 * <p>同時監聽兩個 Topic 以相容不同的命名慣例：
 * <ul>
 *   <li>{@code candidate.hired} — KafkaEventPublisher 預設格式（aggregate.action）</li>
 *   <li>{@code hrms.candidate.hired} — 帶命名空間前綴的格式</li>
 * </ul>
 *
 * <p>處理流程：
 * <ol>
 *   <li>解析 Kafka JSON 訊息，擷取候選人基本資料</li>
 *   <li>檢查是否已有該 Email 的員工（冪等性防重複）</li>
 *   <li>呼叫 {@link CandidateOnboardingService} 建立員工檔案</li>
 * </ol>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class CandidateHiredEventListener {

    private final CandidateOnboardingService candidateOnboardingService;
    private final ObjectMapper objectMapper;

    /**
     * 處理候選人錄取事件，自動建立員工檔案
     *
     * <p>同時監聽 {@code candidate.hired} 與 {@code hrms.candidate.hired} 兩個 Topic，
     * 確保無論事件來源使用哪種命名慣例都能正確消費。
     *
     * @param message Kafka 訊息（JSON 格式的 CandidateHiredEvent）
     */
    @KafkaListener(
            topics = {"candidate.hired", "hrms.candidate.hired"},
            groupId = "organization-service"
    )
    public void handleCandidateHired(String message) {
        log.info("[CandidateHiredEventListener] 收到候選人錄取事件: {}", message);

        try {
            JsonNode json = objectMapper.readTree(message);

            // 解析候選人基本資料
            String candidateId = getTextOrNull(json, "candidateId");
            String fullName = getTextOrNull(json, "fullName");
            String email = getTextOrNull(json, "email");
            String phoneNumber = getTextOrNull(json, "phoneNumber");
            String jobTitle = getTextOrNull(json, "jobTitle");
            String departmentId = getTextOrNull(json, "departmentId");
            String departmentName = getTextOrNull(json, "departmentName");
            String offeredSalary = getTextOrNull(json, "offeredSalary");
            String expectedStartDate = getTextOrNull(json, "expectedStartDate");
            String hiredBy = getTextOrNull(json, "hiredBy");
            String hiredByName = getTextOrNull(json, "hiredByName");

            // 驗證必要欄位
            if (candidateId == null) {
                log.warn("[CandidateHiredEventListener] 事件缺少 candidateId，忽略此訊息");
                return;
            }
            if (fullName == null) {
                log.warn("[CandidateHiredEventListener] 事件缺少 fullName，忽略此訊息 - candidateId={}", candidateId);
                return;
            }
            if (email == null) {
                log.warn("[CandidateHiredEventListener] 事件缺少 email，忽略此訊息 - candidateId={}", candidateId);
                return;
            }

            log.info("[CandidateHiredEventListener] 開始建立員工檔案 - candidateId={}, fullName={}, jobTitle={}, departmentName={}, email={}",
                    candidateId, fullName, jobTitle, departmentName, email);

            // 呼叫 Onboarding Service 建立員工檔案
            candidateOnboardingService.createEmployeeFromCandidate(
                    candidateId,
                    fullName,
                    email,
                    phoneNumber,
                    jobTitle,
                    departmentId,
                    departmentName,
                    offeredSalary,
                    expectedStartDate
            );

            log.info("[CandidateHiredEventListener] 員工檔案建立完成 - candidateId={}, fullName={}", candidateId, fullName);

        } catch (Exception e) {
            log.error("[CandidateHiredEventListener] 處理候選人錄取事件失敗: {}", e.getMessage(), e);
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
