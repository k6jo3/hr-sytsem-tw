package com.company.hrms.attendance.application.service.leavetype;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.response.leavetype.DeactivateLeaveTypeResponse;
import com.company.hrms.attendance.domain.model.aggregate.LeaveType;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.attendance.domain.repository.ILeaveTypeRepository;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 停用假別服務實作
 *
 * <p>流程：
 * <ol>
 *   <li>依 ID 查詢假別（必須存在且啟用）</li>
 *   <li>執行停用（isActive = false）</li>
 *   <li>儲存假別</li>
 * </ol>
 *
 * <p>注意：停用不影響已提交或已核准的請假申請
 */
@Service("deactivateLeaveTypeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeactivateLeaveTypeServiceImpl implements CommandApiService<Void, DeactivateLeaveTypeResponse> {

    private final ILeaveTypeRepository leaveTypeRepository;
    private final ILeaveApplicationRepository leaveApplicationRepository;

    @Override
    public DeactivateLeaveTypeResponse execCommand(Void request, JWTModel currentUser, String... args)
            throws Exception {
        String leaveTypeId = args[0];
        log.info("停用假別流程開始: leaveTypeId={}", leaveTypeId);

        // 1. 查詢假別
        LeaveType leaveType = leaveTypeRepository.findById(new LeaveTypeId(leaveTypeId))
                .orElseThrow(() -> new IllegalArgumentException("假別不存在: " + leaveTypeId));

        // 2. 驗證啟用狀態
        if (!leaveType.isActive()) {
            throw new IllegalStateException("假別已為停用狀態: " + leaveTypeId);
        }

        // 3. 法定假別不可停用
        if (leaveType.isStatutoryLeave()) {
            throw new IllegalStateException("法定假別不可停用: " + leaveTypeId);
        }

        // 4. 檢查是否有進行中的請假申請使用此假別
        boolean hasActiveApplications = leaveApplicationRepository.existsByLeaveTypeIdAndStatusIn(
                leaveTypeId, List.of(ApplicationStatus.PENDING, ApplicationStatus.APPROVED));
        if (hasActiveApplications) {
            throw new IllegalStateException("此假別尚有待審核或已核准的請假申請，無法停用: " + leaveTypeId);
        }

        // 5. 停用
        leaveType.deactivate();

        // 6. 儲存
        leaveTypeRepository.save(leaveType);

        log.info("停用假別流程完成: leaveTypeId={}", leaveTypeId);

        return DeactivateLeaveTypeResponse.builder()
                .leaveTypeId(leaveTypeId)
                .isActive(false)
                .deactivatedAt(LocalDateTime.now())
                .build();
    }
}
