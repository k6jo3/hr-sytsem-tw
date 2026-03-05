package com.company.hrms.attendance.domain.model.aggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.company.hrms.attendance.domain.model.valueobject.RotationPatternId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

/**
 * 輪班模式聚合根
 * 定義循環的班別序列（如：早班→中班→晚班→休息→早班...）
 */
@Getter
public class RotationPattern extends AggregateRoot<RotationPatternId> {

    private String organizationId;
    private String name;
    private String code;
    private int cycleDays; // 一個完整循環的天數
    private List<RotationDay> rotationDays; // 循環中每天的班別
    private boolean isActive;
    private boolean isDeleted;

    /**
     * 輪班模式中的單日定義
     */
    @Getter
    public static class RotationDay {
        private final int dayOrder; // 循環中的第幾天（1-based）
        private final ShiftId shiftId; // null 表示休息日
        private final boolean isRestDay;

        public RotationDay(int dayOrder, ShiftId shiftId, boolean isRestDay) {
            if (dayOrder < 1) {
                throw new IllegalArgumentException("天序必須 >= 1");
            }
            this.dayOrder = dayOrder;
            this.shiftId = isRestDay ? null : shiftId;
            this.isRestDay = isRestDay;
        }

        public static RotationDay workDay(int dayOrder, ShiftId shiftId) {
            if (shiftId == null) {
                throw new IllegalArgumentException("工作日必須指定班別");
            }
            return new RotationDay(dayOrder, shiftId, false);
        }

        public static RotationDay restDay(int dayOrder) {
            return new RotationDay(dayOrder, null, true);
        }
    }

    public RotationPattern(RotationPatternId id, String organizationId, String name,
            String code, int cycleDays) {
        super(id);
        validate(organizationId, name, code, cycleDays);
        this.organizationId = organizationId;
        this.name = name;
        this.code = code;
        this.cycleDays = cycleDays;
        this.rotationDays = new ArrayList<>();
        this.isActive = true;
        this.isDeleted = false;
    }

    /**
     * 設定輪班天序（整批替換）
     */
    public void setRotationDays(List<RotationDay> days) {
        if (days == null || days.isEmpty()) {
            throw new IllegalArgumentException("輪班天序不可為空");
        }
        if (days.size() != this.cycleDays) {
            throw new IllegalArgumentException(
                    String.format("天序數量 (%d) 必須等於循環天數 (%d)", days.size(), this.cycleDays));
        }
        this.rotationDays = new ArrayList<>(days);
    }

    /**
     * 取得某日在循環中應該排的班別
     * @param dayIndex 從輪班開始日算起的第幾天（0-based）
     */
    public RotationDay getDayForIndex(int dayIndex) {
        if (rotationDays.isEmpty()) {
            throw new IllegalStateException("尚未設定輪班天序");
        }
        int index = dayIndex % cycleDays;
        return rotationDays.get(index);
    }

    public List<RotationDay> getRotationDays() {
        return Collections.unmodifiableList(rotationDays);
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    private void validate(String organizationId, String name, String code, int cycleDays) {
        if (organizationId == null || organizationId.isBlank()) {
            throw new IllegalArgumentException("組織 ID 不可為空");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("輪班模式名稱不可為空");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("輪班模式代碼不可為空");
        }
        if (cycleDays < 1) {
            throw new IllegalArgumentException("循環天數必須 >= 1");
        }
    }

    /**
     * 從持久層重建
     */
    private RotationPattern(RotationPatternId id, String organizationId, String name, String code,
            int cycleDays, List<RotationDay> rotationDays, boolean isActive, boolean isDeleted) {
        super(id);
        this.organizationId = organizationId;
        this.name = name;
        this.code = code;
        this.cycleDays = cycleDays;
        this.rotationDays = rotationDays != null ? new ArrayList<>(rotationDays) : new ArrayList<>();
        this.isActive = isActive;
        this.isDeleted = isDeleted;
    }

    public static RotationPattern reconstitute(RotationPatternId id, String organizationId, String name,
            String code, int cycleDays, List<RotationDay> rotationDays, boolean isActive, boolean isDeleted) {
        return new RotationPattern(id, organizationId, name, code, cycleDays,
                rotationDays, isActive, isDeleted);
    }
}
