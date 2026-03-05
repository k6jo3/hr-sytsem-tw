package com.company.hrms.attendance.infrastructure.job;

import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.company.hrms.attendance.domain.event.AttendanceAnomalyDetectedEvent;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.valueobject.AnomalyType;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.attendance.domain.service.AbsentDetectionDomainService;
import com.company.hrms.common.domain.event.EventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 曠職自動判定排程
 *
 * <p>每日 19:00 執行，掃描當日無打卡且無請假記錄的員工，
 * 寫入 ABSENT 記錄並發布 AttendanceAnomalyDetectedEvent。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AbsentDetectionJob {

    private final IAttendanceRecordRepository attendanceRecordRepository;
    private final ILeaveApplicationRepository leaveApplicationRepository;
    private final EventPublisher eventPublisher;
    private final JdbcTemplate jdbcTemplate;

    private final AbsentDetectionDomainService absentDetectionDomainService = new AbsentDetectionDomainService();

    /**
     * 每日 19:00 執行曠職偵測
     */
    @Scheduled(cron = "0 0 19 * * ?")
    public void detectAbsentEmployees() {
        LocalDate today = LocalDate.now();
        log.info("[AbsentDetectionJob] 開始執行曠職偵測，日期: {}", today);

        try {
            // 1. 查詢所有在職員工 ID（透過 employee_read_models 或回退機制）
            List<String> allEmployeeIds = getAllActiveEmployeeIds();
            if (allEmployeeIds.isEmpty()) {
                log.warn("[AbsentDetectionJob] 無法取得在職員工清單，跳過偵測");
                return;
            }

            // 2. 查詢今日已有打卡記錄的員工 ID
            List<String> employeeIdsWithRecord = attendanceRecordRepository
                    .findEmployeeIdsWithRecordOnDate(today);

            // 3. 查詢今日有核准請假的員工 ID
            List<String> employeeIdsOnLeave = leaveApplicationRepository
                    .findEmployeeIdsWithApprovedLeaveOnDate(today);

            // 4. 透過 Domain Service 判定缺勤員工
            List<String> absentEmployeeIds = absentDetectionDomainService
                    .detectAbsentEmployees(allEmployeeIds, employeeIdsWithRecord, employeeIdsOnLeave);

            int successCount = 0;
            int failCount = 0;

            // 5. 為缺勤員工建立 ABSENT 記錄並發布事件
            for (String employeeId : absentEmployeeIds) {
                try {
                    AttendanceRecord absentRecord = absentDetectionDomainService
                            .createAbsentRecord(employeeId, today);
                    attendanceRecordRepository.save(absentRecord);

                    // 發布異常事件
                    AttendanceAnomalyDetectedEvent event = new AttendanceAnomalyDetectedEvent(
                            absentRecord.getId().getValue(),
                            employeeId,
                            today,
                            AnomalyType.ABSENT.name(),
                            0);
                    eventPublisher.publish(event);

                    successCount++;
                } catch (Exception e) {
                    log.error("[AbsentDetectionJob] 建立缺勤記錄失敗 - 員工: {}, 錯誤: {}",
                            employeeId, e.getMessage(), e);
                    failCount++;
                }
            }

            log.info("[AbsentDetectionJob] 曠職偵測完成 - 在職: {}, 已打卡: {}, 請假: {}, 缺勤: {}, 成功: {}, 失敗: {}",
                    allEmployeeIds.size(), employeeIdsWithRecord.size(),
                    employeeIdsOnLeave.size(), absentEmployeeIds.size(),
                    successCount, failCount);

        } catch (Exception e) {
            log.error("[AbsentDetectionJob] 曠職偵測任務執行失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * 取得所有在職員工 ID
     * <p>
     * 嘗試從 employee_read_models 查詢，若表不存在則從 leave_balances 推導
     */
    private List<String> getAllActiveEmployeeIds() {
        try {
            return jdbcTemplate.queryForList(
                    "SELECT employee_id FROM employee_read_models",
                    String.class);
        } catch (Exception e) {
            log.debug("[AbsentDetectionJob] employee_read_models 不存在，從 leave_balances 推導員工清單");
            // 降級方案：從 leave_balances 取得有餘額記錄的員工
            int currentYear = LocalDate.now().getYear();
            return jdbcTemplate.queryForList(
                    "SELECT DISTINCT employee_id FROM leave_balances WHERE \"year\" = ?",
                    String.class,
                    currentYear);
        }
    }
}
