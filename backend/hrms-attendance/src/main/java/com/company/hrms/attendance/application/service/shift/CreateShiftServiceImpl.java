package com.company.hrms.attendance.application.service.shift;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.shift.CreateShiftRequest;
import com.company.hrms.attendance.api.response.shift.CreateShiftResponse;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftType;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 建立班別服務實作
 * <p>
 * 對應 Controller 方法：HR03ShiftCmdController.createShift()
 * 流程：建立 Shift aggregate -> 設定可選屬性 -> 儲存 -> 回傳結果
 * </p>
 */
@Slf4j
@Service("createShiftServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CreateShiftServiceImpl
        implements CommandApiService<CreateShiftRequest, CreateShiftResponse> {

    private final IShiftRepository shiftRepository;

    @Override
    public CreateShiftResponse execCommand(
            CreateShiftRequest request, JWTModel currentUser, String... args) throws Exception {

        log.info("建立班別: code={}, name={}, organizationId={}",
                request.getShiftCode(), request.getShiftName(), request.getOrganizationId());

        // 1. 解析班別類型
        ShiftType shiftType = parseShiftType(request.getShiftType());

        // 2. 建立 Shift aggregate
        Shift shift = new Shift(
                new ShiftId(UUID.randomUUID().toString()),
                request.getOrganizationId(),
                request.getShiftCode(),
                request.getShiftName(),
                shiftType,
                request.getWorkStartTime(),
                request.getWorkEndTime());

        // 3. 設定可選屬性：休息時間
        if (request.getBreakStartTime() != null && request.getBreakEndTime() != null) {
            shift.setBreakTime(request.getBreakStartTime(), request.getBreakEndTime());
        }

        // 4. 設定可選屬性：遲到/早退容許分鐘
        if (request.getLateToleranceMinutes() != null || request.getEarlyLeaveToleranceMinutes() != null) {
            int lateTolerance = request.getLateToleranceMinutes() != null
                    ? request.getLateToleranceMinutes() : 0;
            int earlyLeaveTolerance = request.getEarlyLeaveToleranceMinutes() != null
                    ? request.getEarlyLeaveToleranceMinutes() : 0;
            shift.setTolerances(lateTolerance, earlyLeaveTolerance);
        }

        // 5. 儲存
        shiftRepository.save(shift);

        log.info("班別建立成功: shiftId={}", shift.getId().getValue());

        // 6. 計算工作時數並回傳
        BigDecimal workingHours = calculateWorkingHours(shift);

        return CreateShiftResponse.builder()
                .shiftId(shift.getId().getValue())
                .shiftCode(shift.getCode())
                .shiftName(shift.getName())
                .workingHours(workingHours)
                .createdAt(LocalDateTime.now())
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
                // 嘗試直接解析 Domain 列舉值
                try {
                    yield ShiftType.valueOf(shiftTypeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("不支援的班別類型: " + shiftTypeStr);
                }
            }
        };
    }

    /**
     * 計算工作時數（上下班時間差 - 休息時間）
     */
    private BigDecimal calculateWorkingHours(Shift shift) {
        Duration workDuration = Duration.between(shift.getWorkStartTime(), shift.getWorkEndTime());

        // 扣除休息時間
        if (shift.getBreakStartTime() != null && shift.getBreakEndTime() != null) {
            Duration breakDuration = Duration.between(shift.getBreakStartTime(), shift.getBreakEndTime());
            workDuration = workDuration.minus(breakDuration);
        }

        // 轉換為小時（保留 1 位小數）
        long totalMinutes = workDuration.toMinutes();
        return BigDecimal.valueOf(totalMinutes)
                .divide(BigDecimal.valueOf(60), 1, RoundingMode.HALF_UP);
    }
}
