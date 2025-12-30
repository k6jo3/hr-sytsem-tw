package com.company.hrms.payroll.domain.model.valueobject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 計薪期間值物件
 * 封裝薪資計算的起迄日期
 */
@Getter
@EqualsAndHashCode
public class PayPeriod {

    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * 建構計薪期間值物件
     * 
     * @param startDate 起始日期
     * @param endDate   結束日期
     * @throws IllegalArgumentException 當日期無效時
     */
    public PayPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("PayPeriod startDate cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("PayPeriod endDate cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("PayPeriod endDate cannot be before startDate");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 建立月份的計薪期間
     * 
     * @param year  年
     * @param month 月
     * @return 該月份的計薪期間
     */
    public static PayPeriod ofMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return new PayPeriod(start, end);
    }

    /**
     * 計算期間的天數
     * 
     * @return 天數 (包含起迄日)
     */
    public long getDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * 計算期間的工作日數 (排除週六日)
     * 簡易計算，不考慮國定假日
     * 
     * @return 工作日數
     */
    public long getWorkingDays() {
        long workingDays = 0;
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            int dayOfWeek = current.getDayOfWeek().getValue();
            if (dayOfWeek < 6) { // Monday = 1, Friday = 5
                workingDays++;
            }
            current = current.plusDays(1);
        }
        return workingDays;
    }

    /**
     * 檢查日期是否在此期間內
     * 
     * @param date 待檢查的日期
     * @return 是否在期間內
     */
    public boolean contains(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * 取得期間的年份
     * 
     * @return 年份 (以起始日為準)
     */
    public int getYear() {
        return startDate.getYear();
    }

    /**
     * 取得期間的月份
     * 
     * @return 月份 (以起始日為準)
     */
    public int getMonth() {
        return startDate.getMonthValue();
    }

    /**
     * 格式化為字串
     * 
     * @return 格式化字串 (例: 2025-12-01 ~ 2025-12-31)
     */
    @Override
    public String toString() {
        return startDate + " ~ " + endDate;
    }
}
