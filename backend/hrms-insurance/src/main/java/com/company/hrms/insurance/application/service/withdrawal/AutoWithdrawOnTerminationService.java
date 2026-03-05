package com.company.hrms.insurance.application.service.withdrawal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.insurance.domain.event.InsuranceWithdrawalCompletedEvent;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.valueobject.EffectiveDateRule;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 離職連動自動退保服務
 *
 * <p>當收到 EmployeeTerminatedEvent 時，自動退保所有有效加保記錄。
 * 退保日期依 EffectiveDateRule 計算：
 * <ul>
 *   <li>勞保/勞退/團保：離職日退保</li>
 *   <li>健保：月底離職者次月 1 日退保</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AutoWithdrawOnTerminationService {

    private final IInsuranceEnrollmentRepository enrollmentRepository;

    /**
     * 執行離職自動退保
     *
     * @param employeeId      離職員工 ID
     * @param terminationDate 離職日期
     * @param tenantId        租戶 ID（可為 null）
     * @return 已退保的記錄事件清單
     */
    @Transactional
    public List<InsuranceWithdrawalCompletedEvent> withdrawAllOnTermination(
            String employeeId, LocalDate terminationDate, String tenantId) {

        log.info("[AutoWithdraw] 開始離職自動退保: employeeId={}, terminationDate={}", employeeId, terminationDate);

        List<InsuranceEnrollment> activeEnrollments = enrollmentRepository
                .findAllActiveByEmployeeId(employeeId);

        if (activeEnrollments.isEmpty()) {
            log.info("[AutoWithdraw] 員工 {} 無有效加保記錄，跳過退保", employeeId);
            return List.of();
        }

        List<InsuranceWithdrawalCompletedEvent> events = new ArrayList<>();

        for (InsuranceEnrollment enrollment : activeEnrollments) {
            try {
                // 依保險類型計算退保生效日
                LocalDate withdrawDate = EffectiveDateRule.calculateWithdrawDate(
                        enrollment.getInsuranceType(), terminationDate);

                enrollment.withdraw(withdrawDate);
                enrollmentRepository.save(enrollment);

                InsuranceWithdrawalCompletedEvent event = InsuranceWithdrawalCompletedEvent.create(
                        employeeId,
                        enrollment.getId().getValue(),
                        enrollment.getInsuranceType().name(),
                        withdrawDate,
                        "離職自動退保",
                        tenantId);
                events.add(event);

                log.info("[AutoWithdraw] 退保成功: enrollmentId={}, type={}, withdrawDate={}",
                        enrollment.getId().getValue(),
                        enrollment.getInsuranceType().getDisplayName(),
                        withdrawDate);

            } catch (Exception e) {
                log.error("[AutoWithdraw] 退保失敗: enrollmentId={}, type={}, 錯誤: {}",
                        enrollment.getId().getValue(),
                        enrollment.getInsuranceType().getDisplayName(),
                        e.getMessage(), e);
            }
        }

        log.info("[AutoWithdraw] 離職自動退保完成: employeeId={}, 退保筆數={}/{}",
                employeeId, events.size(), activeEnrollments.size());

        return events;
    }
}
