package com.company.hrms.notification.domain.model.valueobject;

import java.time.LocalTime;
import java.util.Objects;

/**
 * 靜音時段值物件
 * <p>
 * 定義使用者不希望收到通知的時段，通常為下班時間或睡眠時間
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public class QuietHours {

    private final LocalTime startTime;
    private final LocalTime endTime;
    private final boolean enabled;

    private QuietHours(LocalTime startTime, LocalTime endTime, boolean enabled) {
        if (enabled) {
            Objects.requireNonNull(startTime, "靜音時段開始時間不可為空");
            Objects.requireNonNull(endTime, "靜音時段結束時間不可為空");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.enabled = enabled;
    }

    /**
     * 建立靜音時段
     *
     * @param startTime 開始時間
     * @param endTime   結束時間
     * @return QuietHours
     */
    public static QuietHours of(LocalTime startTime, LocalTime endTime) {
        return new QuietHours(startTime, endTime, true);
    }

    /**
     * 建立停用的靜音時段
     *
     * @return 停用的 QuietHours
     */
    public static QuietHours disabled() {
        return new QuietHours(null, null, false);
    }

    /**
     * 預設靜音時段 (22:00 - 08:00)
     *
     * @return 預設 QuietHours
     */
    public static QuietHours defaultQuietHours() {
        return new QuietHours(LocalTime.of(22, 0), LocalTime.of(8, 0), true);
    }

    /**
     * 檢查指定時間是否在靜音時段內
     *
     * @param time 要檢查的時間
     * @return true 表示在靜音時段內
     */
    public boolean isInQuietHours(LocalTime time) {
        if (!enabled || time == null) {
            return false;
        }

        // 處理跨日情況 (例如 22:00 - 08:00)
        if (startTime.isBefore(endTime)) {
            // 同一天內 (例如 08:00 - 22:00)
            return !time.isBefore(startTime) && time.isBefore(endTime);
        } else {
            // 跨日 (例如 22:00 - 08:00)
            return !time.isBefore(startTime) || time.isBefore(endTime);
        }
    }

    /**
     * 取得開始時間
     *
     * @return 開始時間
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * 取得結束時間
     *
     * @return 結束時間
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * 是否啟用
     *
     * @return true 表示啟用
     */
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuietHours that = (QuietHours) o;
        return enabled == that.enabled &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, enabled);
    }

    @Override
    public String toString() {
        if (!enabled) {
            return "QuietHours(disabled)";
        }
        return String.format("QuietHours(%s - %s)", startTime, endTime);
    }
}
