package com.company.hrms.attendance.application.service.shift;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.shift.UpdateShiftRequest;
import com.company.hrms.attendance.api.response.shift.UpdateShiftResponse;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftType;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 更新班別服務實作
 * <p>
 * 對應 Controller 方法：HR03ShiftCmdController.updateShift()
 * 流程：查詢既有班別 -> 以 reconstitute 方式套用更新欄位 -> 儲存 -> 回傳結果
 * </p>
 */
@Slf4j
@Service("updateShiftServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UpdateShiftServiceImpl
        implements CommandApiService<UpdateShiftRequest, UpdateShiftResponse> {

    private final IShiftRepository shiftRepository;

    @Override
    public UpdateShiftResponse execCommand(
            UpdateShiftRequest request, JWTModel currentUser, String... args) throws Exception {

        // args[0] 為 Controller 傳入的 shiftId（PathVariable）
        String shiftIdValue = args[0];
        log.info("更新班別: shiftId={}", shiftIdValue);

        // 1. 查詢既有班別
        ShiftId shiftId = new ShiftId(shiftIdValue);
        Shift existingShift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new DomainException("SHIFT_NOT_FOUND", "班別不存在: " + shiftIdValue));

        // 2. 合併更新欄位（request 中非 null 的欄位覆蓋既有值）
        String updatedName = request.getShiftName() != null
                ? request.getShiftName() : existingShift.getName();
        ShiftType updatedType = request.getShiftType() != null
                ? parseShiftType(request.getShiftType()) : existingShift.getType();
        LocalTime updatedWorkStart = request.getWorkStartTime() != null
                ? request.getWorkStartTime() : existingShift.getWorkStartTime();
        LocalTime updatedWorkEnd = request.getWorkEndTime() != null
                ? request.getWorkEndTime() : existingShift.getWorkEndTime();
        LocalTime updatedBreakStart = request.getBreakStartTime() != null
                ? request.getBreakStartTime() : existingShift.getBreakStartTime();
        LocalTime updatedBreakEnd = request.getBreakEndTime() != null
                ? request.getBreakEndTime() : existingShift.getBreakEndTime();
        int updatedLateTolerance = request.getLateToleranceMinutes() != null
                ? request.getLateToleranceMinutes() : existingShift.getLateToleranceMinutes();
        int updatedEarlyLeaveTolerance = request.getEarlyLeaveToleranceMinutes() != null
                ? request.getEarlyLeaveToleranceMinutes() : existingShift.getEarlyLeaveToleranceMinutes();

        // 3. 使用 reconstitute 建立更新後的 Shift（保留原始 ID 與狀態）
        Shift updatedShift = Shift.reconstitute(
                existingShift.getId(),
                existingShift.getOrganizationId(),
                existingShift.getCode(),
                updatedName,
                updatedType,
                updatedWorkStart,
                updatedWorkEnd,
                updatedBreakStart,
                updatedBreakEnd,
                updatedLateTolerance,
                updatedEarlyLeaveTolerance,
                existingShift.isLateCheckEnabled(),
                existingShift.isLateSalaryDeduction(),
                existingShift.isActive(),
                existingShift.isDeleted());

        // 4. 儲存
        shiftRepository.save(updatedShift);

        log.info("班別更新成功: shiftId={}", shiftIdValue);

        // 5. 回傳結果
        return UpdateShiftResponse.builder()
                .shiftId(updatedShift.getId().getValue())
                .shiftName(updatedShift.getName())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 解析班別類型字串為 ShiftType 列舉
     * <p>
     * API 層使用 STANDARD/FLEXIBLE/ROTATING，Domain 層使用 REGULAR/FLEXIBLE/SHIFT
     * </p>
     */
    private ShiftType parseShiftType(String shiftTypeStr) {
        return switch (shiftTypeStr.toUpperCase()) {
            case "STANDARD" -> ShiftType.REGULAR;
            case "FLEXIBLE" -> ShiftType.FLEXIBLE;
            case "ROTATING" -> ShiftType.SHIFT;
            default -> {
                try {
                    yield ShiftType.valueOf(shiftTypeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("不支援的班別類型: " + shiftTypeStr);
                }
            }
        };
    }
}
