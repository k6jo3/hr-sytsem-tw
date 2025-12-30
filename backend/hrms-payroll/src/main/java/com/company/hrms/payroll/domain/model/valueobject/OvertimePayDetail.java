package com.company.hrms.payroll.domain.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 加班費明細值物件
 * 封裝各類型加班的時數與金額
 * 
 * <p>
 * 依勞基法計算加班費：
 * </p>
 * <ul>
 * <li>平日加班前 2 小時：時薪 × 1.34</li>
 * <li>平日加班後 2 小時：時薪 × 1.67</li>
 * <li>休息日前 2 小時：時薪 × 1.34</li>
 * <li>休息日 2-8 小時：時薪 × 1.67</li>
 * <li>休息日 8 小時後：時薪 × 2.67</li>
 * <li>國定假日：時薪 × 2.0</li>
 * </ul>
 */
@Getter
@EqualsAndHashCode
@Builder
public class OvertimePayDetail {

    /**
     * 平日加班時數
     */
    @Builder.Default
    private final BigDecimal weekdayHours = BigDecimal.ZERO;

    /**
     * 平日加班費
     */
    @Builder.Default
    private final BigDecimal weekdayPay = BigDecimal.ZERO;

    /**
     * 休息日加班時數
     */
    @Builder.Default
    private final BigDecimal restDayHours = BigDecimal.ZERO;

    /**
     * 休息日加班費
     */
    @Builder.Default
    private final BigDecimal restDayPay = BigDecimal.ZERO;

    /**
     * 國定假日加班時數
     */
    @Builder.Default
    private final BigDecimal holidayHours = BigDecimal.ZERO;

    /**
     * 國定假日加班費
     */
    @Builder.Default
    private final BigDecimal holidayPay = BigDecimal.ZERO;

    /**
     * 計算加班費總額
     * 
     * @return 加班費總額
     */
    public BigDecimal getTotal() {
        return weekdayPay.add(restDayPay).add(holidayPay)
                .setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 計算加班總時數
     * 
     * @return 加班總時數
     */
    public BigDecimal getTotalHours() {
        return weekdayHours.add(restDayHours).add(holidayHours);
    }

    /**
     * 建立空的加班費明細
     * 
     * @return 空的加班費明細
     */
    public static OvertimePayDetail empty() {
        return OvertimePayDetail.builder().build();
    }

    /**
     * 計算平日加班費 (依勞基法)
     * 
     * @param hourlyRate 時薪
     * @param hours      加班時數
     * @return 平日加班費
     */
    public static BigDecimal calculateWeekdayPay(BigDecimal hourlyRate, BigDecimal hours) {
        if (hours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal first2Hours = hours.min(BigDecimal.valueOf(2));
        BigDecimal remaining = hours.subtract(first2Hours).max(BigDecimal.ZERO);

        // 前 2 小時 × 1.34
        BigDecimal pay = hourlyRate.multiply(first2Hours)
                .multiply(BigDecimal.valueOf(1.34));

        // 超過 2 小時 × 1.67
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            pay = pay.add(hourlyRate.multiply(remaining)
                    .multiply(BigDecimal.valueOf(1.67)));
        }

        return pay.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 計算休息日加班費 (依勞基法)
     * 
     * @param hourlyRate 時薪
     * @param hours      加班時數
     * @return 休息日加班費
     */
    public static BigDecimal calculateRestDayPay(BigDecimal hourlyRate, BigDecimal hours) {
        if (hours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal pay = BigDecimal.ZERO;

        // 前 2 小時 × 1.34
        BigDecimal band1 = hours.min(BigDecimal.valueOf(2));
        pay = pay.add(hourlyRate.multiply(band1).multiply(BigDecimal.valueOf(1.34)));

        // 2-8 小時 × 1.67
        if (hours.compareTo(BigDecimal.valueOf(2)) > 0) {
            BigDecimal band2 = hours.min(BigDecimal.valueOf(8)).subtract(BigDecimal.valueOf(2))
                    .max(BigDecimal.ZERO);
            pay = pay.add(hourlyRate.multiply(band2).multiply(BigDecimal.valueOf(1.67)));
        }

        // 8+ 小時 × 2.67
        if (hours.compareTo(BigDecimal.valueOf(8)) > 0) {
            BigDecimal band3 = hours.subtract(BigDecimal.valueOf(8));
            pay = pay.add(hourlyRate.multiply(band3).multiply(BigDecimal.valueOf(2.67)));
        }

        return pay.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 計算國定假日加班費
     * 
     * @param hourlyRate 時薪
     * @param hours      加班時數
     * @return 國定假日加班費
     */
    public static BigDecimal calculateHolidayPay(BigDecimal hourlyRate, BigDecimal hours) {
        if (hours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return hourlyRate.multiply(hours).multiply(BigDecimal.valueOf(2.0))
                .setScale(0, RoundingMode.HALF_UP);
    }
}
