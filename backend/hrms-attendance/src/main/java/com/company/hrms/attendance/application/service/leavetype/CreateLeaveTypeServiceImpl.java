package com.company.hrms.attendance.application.service.leavetype;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.leavetype.CreateLeaveTypeRequest;
import com.company.hrms.attendance.api.response.leavetype.CreateLeaveTypeResponse;
import com.company.hrms.attendance.domain.model.aggregate.LeaveType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.model.valueobject.LeaveUnit;
import com.company.hrms.attendance.domain.repository.ILeaveTypeRepository;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 建立假別服務實作
 *
 * <p>流程：
 * <ol>
 *   <li>驗證假別代碼在組織內唯一</li>
 *   <li>建立 LeaveType 聚合根</li>
 *   <li>儲存假別</li>
 * </ol>
 */
@Service("createLeaveTypeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateLeaveTypeServiceImpl implements CommandApiService<CreateLeaveTypeRequest, CreateLeaveTypeResponse> {

    private final ILeaveTypeRepository leaveTypeRepository;

    @Override
    public CreateLeaveTypeResponse execCommand(CreateLeaveTypeRequest request, JWTModel currentUser, String... args)
            throws Exception {
        log.info("建立假別流程開始: code={}, name={}", request.getLeaveTypeCode(), request.getLeaveTypeName());

        // 1. 驗證假別代碼唯一性
        leaveTypeRepository.findByCode(request.getLeaveTypeCode()).ifPresent(existing -> {
            throw new IllegalArgumentException("假別代碼已存在: " + request.getLeaveTypeCode());
        });

        // 2. 建立 LeaveType 聚合根
        LeaveTypeId id = LeaveTypeId.next();
        boolean isPaid = Boolean.TRUE.equals(request.getIsPaid());

        LeaveType leaveType = new LeaveType(
                id,
                request.getOrganizationId(),
                request.getLeaveTypeName(),
                request.getLeaveTypeCode(),
                LeaveUnit.DAY,
                isPaid);

        // 3. 設定額外屬性
        BigDecimal payRate = isPaid ? BigDecimal.ONE : BigDecimal.ZERO;
        boolean requiresProof = Boolean.TRUE.equals(request.getRequiresProof());
        boolean allowCarryOver = Boolean.TRUE.equals(request.getAllowCarryOver());

        leaveType.updateDetails(
                request.getLeaveTypeName(),
                LeaveUnit.DAY,
                isPaid,
                payRate,
                requiresProof,
                null,
                request.getAnnualQuotaDays(),
                allowCarryOver);

        // 4. 儲存
        leaveTypeRepository.save(leaveType);

        log.info("建立假別流程完成: leaveTypeId={}", id.getValue());

        return CreateLeaveTypeResponse.builder()
                .leaveTypeId(id.getValue())
                .leaveTypeCode(request.getLeaveTypeCode())
                .leaveTypeName(request.getLeaveTypeName())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
