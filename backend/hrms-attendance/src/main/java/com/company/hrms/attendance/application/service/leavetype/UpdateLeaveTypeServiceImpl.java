package com.company.hrms.attendance.application.service.leavetype;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.leavetype.UpdateLeaveTypeRequest;
import com.company.hrms.attendance.api.response.leavetype.UpdateLeaveTypeResponse;
import com.company.hrms.attendance.domain.model.aggregate.LeaveType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.model.valueobject.LeaveUnit;
import com.company.hrms.attendance.domain.repository.ILeaveTypeRepository;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 更新假別服務實作
 *
 * <p>流程：
 * <ol>
 *   <li>依 ID 查詢假別（必須存在且啟用）</li>
 *   <li>更新假別詳情</li>
 *   <li>儲存假別</li>
 * </ol>
 */
@Service("updateLeaveTypeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateLeaveTypeServiceImpl implements CommandApiService<UpdateLeaveTypeRequest, UpdateLeaveTypeResponse> {

    private final ILeaveTypeRepository leaveTypeRepository;

    @Override
    public UpdateLeaveTypeResponse execCommand(UpdateLeaveTypeRequest request, JWTModel currentUser, String... args)
            throws Exception {
        String leaveTypeId = args[0];
        log.info("更新假別流程開始: leaveTypeId={}", leaveTypeId);

        // 1. 查詢假別
        LeaveType leaveType = leaveTypeRepository.findById(new LeaveTypeId(leaveTypeId))
                .orElseThrow(() -> new IllegalArgumentException("假別不存在: " + leaveTypeId));

        // 2. 驗證啟用狀態
        if (!leaveType.isActive()) {
            throw new IllegalStateException("無法更新已停用的假別: " + leaveTypeId);
        }

        // 3. 法定假別僅允許修改 proofDescription 與 maxDaysPerYear
        if (leaveType.isStatutoryLeave()) {
            BigDecimal maxDays = request.getAnnualQuotaDays() != null ? request.getAnnualQuotaDays() : leaveType.getMaxDaysPerYear();

            leaveType.updateDetails(
                    leaveType.getName(),
                    leaveType.getUnit(),
                    leaveType.isPaid(),
                    leaveType.getPayRate(),
                    leaveType.isRequiresProof(),
                    leaveType.getProofDescription(),
                    maxDays,
                    leaveType.isCanCarryover());
        } else {
            // 非法定假別可修改所有欄位
            String name = request.getLeaveTypeName() != null ? request.getLeaveTypeName() : leaveType.getName();
            boolean isPaid = request.getIsPaid() != null ? request.getIsPaid() : leaveType.isPaid();
            BigDecimal payRate = isPaid ? BigDecimal.ONE : BigDecimal.ZERO;
            boolean requiresProof = request.getRequiresProof() != null ? request.getRequiresProof() : leaveType.isRequiresProof();
            BigDecimal maxDays = request.getAnnualQuotaDays() != null ? request.getAnnualQuotaDays() : leaveType.getMaxDaysPerYear();
            boolean canCarryOver = request.getAllowCarryOver() != null ? request.getAllowCarryOver() : leaveType.isCanCarryover();

            leaveType.updateDetails(
                    name,
                    leaveType.getUnit(),
                    isPaid,
                    payRate,
                    requiresProof,
                    leaveType.getProofDescription(),
                    maxDays,
                    canCarryOver);
        }

        // 4. 儲存
        leaveTypeRepository.save(leaveType);

        log.info("更新假別流程完成: leaveTypeId={}", leaveTypeId);

        return UpdateLeaveTypeResponse.builder()
                .leaveTypeId(leaveTypeId)
                .leaveTypeName(name)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
