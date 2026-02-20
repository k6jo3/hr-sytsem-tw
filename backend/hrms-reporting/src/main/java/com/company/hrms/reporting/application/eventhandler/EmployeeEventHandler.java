package com.company.hrms.reporting.application.eventhandler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.EmployeeRosterReadModelRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 員工事件處理器
 * 
 * <p>
 * 監聽組織服務的員工相關事件，更新員工花名冊讀模型
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class EmployeeEventHandler {

    private final EmployeeRosterReadModelRepository employeeRosterRepository;
    private final ObjectMapper objectMapper;

    /**
     * 處理員工建立事件
     */
    @KafkaListener(topics = "organization.employee.created", groupId = "reporting-service")
    @Transactional
    public void handleEmployeeCreated(String message) {
        try {
            log.info("收到員工建立事件: {}", message);

            JsonNode event = objectMapper.readTree(message);

            EmployeeRosterReadModel readModel = EmployeeRosterReadModel.builder()
                    .employeeId(event.get("employeeId").asText())
                    .tenantId(event.get("tenantId").asText())
                    .name(event.get("name").asText())
                    .departmentId(event.has("departmentId") ? event.get("departmentId").asText() : null)
                    .departmentName(event.has("departmentName") ? event.get("departmentName").asText() : null)
                    .positionId(event.has("positionId") ? event.get("positionId").asText() : null)
                    .positionName(event.has("positionName") ? event.get("positionName").asText() : null)
                    .hireDate(event.has("hireDate") ? java.time.LocalDate.parse(event.get("hireDate").asText()) : null)
                    .serviceYears(0.0)
                    .status(event.has("status") ? event.get("status").asText() : "ACTIVE")
                    .phone(event.has("phone") ? event.get("phone").asText() : null)
                    .email(event.has("email") ? event.get("email").asText() : null)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isDeleted(false)
                    .build();

            // 計算年資
            if (readModel.getHireDate() != null) {
                long days = ChronoUnit.DAYS.between(readModel.getHireDate(), java.time.LocalDate.now());
                readModel.setServiceYears(Math.round(days / 365.25 * 10.0) / 10.0);
            }

            employeeRosterRepository.save(readModel);
            log.info("員工花名冊讀模型已更新: {}", readModel.getEmployeeId());

        } catch (Exception e) {
            log.error("處理員工建立事件失敗", e);
            // Note: 發送到 DLQ (Dead Letter Queue) - 待基礎設施支援
        }
    }

    /**
     * 處理員工更新事件
     */
    @KafkaListener(topics = "organization.employee.updated", groupId = "reporting-service")
    @Transactional
    public void handleEmployeeUpdated(String message) {
        try {
            log.info("收到員工更新事件: {}", message);

            JsonNode event = objectMapper.readTree(message);
            String employeeId = event.get("employeeId").asText();

            employeeRosterRepository.findById(employeeId).ifPresent(readModel -> {
                // 更新欄位
                if (event.has("name")) {
                    readModel.setName(event.get("name").asText());
                }
                if (event.has("departmentId")) {
                    readModel.setDepartmentId(event.get("departmentId").asText());
                }
                if (event.has("departmentName")) {
                    readModel.setDepartmentName(event.get("departmentName").asText());
                }
                if (event.has("positionId")) {
                    readModel.setPositionId(event.get("positionId").asText());
                }
                if (event.has("positionName")) {
                    readModel.setPositionName(event.get("positionName").asText());
                }
                if (event.has("status")) {
                    readModel.setStatus(event.get("status").asText());
                }
                if (event.has("phone")) {
                    readModel.setPhone(event.get("phone").asText());
                }
                if (event.has("email")) {
                    readModel.setEmail(event.get("email").asText());
                }

                readModel.setUpdatedAt(LocalDateTime.now());

                // 重新計算年資
                if (readModel.getHireDate() != null) {
                    long days = ChronoUnit.DAYS.between(readModel.getHireDate(), java.time.LocalDate.now());
                    readModel.setServiceYears(Math.round(days / 365.25 * 10.0) / 10.0);
                }

                employeeRosterRepository.save(readModel);
                log.info("員工花名冊讀模型已更新: {}", employeeId);
            });

        } catch (Exception e) {
            log.error("處理員工更新事件失敗", e);
        }
    }

    /**
     * 處理員工刪除事件
     */
    @KafkaListener(topics = "organization.employee.deleted", groupId = "reporting-service")
    @Transactional
    public void handleEmployeeDeleted(String message) {
        try {
            log.info("收到員工刪除事件: {}", message);

            JsonNode event = objectMapper.readTree(message);
            String employeeId = event.get("employeeId").asText();

            employeeRosterRepository.findById(employeeId).ifPresent(readModel -> {
                readModel.setIsDeleted(true);
                readModel.setUpdatedAt(LocalDateTime.now());
                employeeRosterRepository.save(readModel);
                log.info("員工花名冊讀模型已標記為刪除: {}", employeeId);
            });

        } catch (Exception e) {
            log.error("處理員工刪除事件失敗", e);
        }
    }
}
