package com.company.hrms.attendance.application.service.shift;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.response.shift.DeactivateShiftResponse;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 停用班別服務實作
 * <p>
 * 對應 Controller 方法：HR03ShiftCmdController.deactivateShift()
 * Controller 傳入 null request，shiftId 在 args[0]
 * 流程：查詢班別 -> 執行停用 -> 儲存 -> 回傳結果
 * </p>
 */
@Slf4j
@Service("deactivateShiftServiceImpl")
@RequiredArgsConstructor
@Transactional
public class DeactivateShiftServiceImpl
        implements CommandApiService<Void, DeactivateShiftResponse> {

    private final IShiftRepository shiftRepository;

    @Override
    public DeactivateShiftResponse execCommand(
            Void request, JWTModel currentUser, String... args) throws Exception {

        // args[0] 為 Controller 傳入的 shiftId（PathVariable）
        String shiftIdValue = args[0];
        log.info("停用班別: shiftId={}", shiftIdValue);

        // 1. 查詢班別
        ShiftId shiftId = new ShiftId(shiftIdValue);
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new DomainException("SHIFT_NOT_FOUND", "班別不存在: " + shiftIdValue));

        // 2. 檢查是否已停用
        if (!shift.isActive()) {
            log.warn("班別已處於停用狀態: shiftId={}", shiftIdValue);
        }

        // 3. 執行停用
        shift.deactivate();

        // 4. 儲存
        shiftRepository.save(shift);

        log.info("班別停用成功: shiftId={}", shiftIdValue);

        // 5. 回傳結果
        // TODO: affectedEmployeeCount 需查詢使用此班別的員工數量，目前先回傳 0
        return DeactivateShiftResponse.builder()
                .shiftId(shift.getId().getValue())
                .isActive(false)
                .deactivatedAt(LocalDateTime.now())
                .affectedEmployeeCount(0)
                .build();
    }
}
