package com.company.hrms.attendance.domain.service;

import org.springframework.stereotype.Service;

/**
 * 勞基法第 38 條法定特休天數計算器
 *
 * <p>依據勞動基準法第 38 條，計算員工依年資應享有的法定特休天數：
 * <ul>
 *   <li>未滿 6 個月：0 天</li>
 *   <li>6 個月(含) ~ 未滿 1 年：3 天</li>
 *   <li>1 年(含) ~ 未滿 2 年：7 天</li>
 *   <li>2 年(含) ~ 未滿 3 年：10 天</li>
 *   <li>3 年(含) ~ 未滿 5 年：14 天</li>
 *   <li>5 年(含) ~ 未滿 10 年：15 天</li>
 *   <li>10 年(含)以上：每年加 1 天，最多 30 天</li>
 * </ul>
 *
 * <p>此為純 Domain 邏輯的計算器，不依賴任何 Repository。
 */
@Service
public class StatutoryAnnualLeaveCalculator {

    /** 10 年以上的基礎天數 */
    private static final int BASE_DAYS_AT_TEN_YEARS = 16;

    /** 法定特休上限 */
    private static final int MAX_STATUTORY_DAYS = 30;

    /**
     * 依年資月數計算法定特休天數
     *
     * @param serviceMonths 員工年資月數（不可為負）
     * @return 法定特休天數
     * @throws IllegalArgumentException 若 serviceMonths 為負數
     */
    public int calculateStatutoryDays(int serviceMonths) {
        if (serviceMonths < 0) {
            throw new IllegalArgumentException("年資月數不可為負數: " + serviceMonths);
        }

        if (serviceMonths < 6) {
            return 0;
        } else if (serviceMonths < 12) {
            return 3;
        } else if (serviceMonths < 24) {
            return 7;
        } else if (serviceMonths < 36) {
            return 10;
        } else if (serviceMonths < 60) {
            return 14;
        } else if (serviceMonths < 120) {
            return 15;
        } else {
            // 10 年以上：基礎 16 天 + 每多 1 年加 1 天，上限 30 天
            int yearsAboveTen = (serviceMonths / 12) - 10;
            int days = BASE_DAYS_AT_TEN_YEARS + yearsAboveTen;
            return Math.min(days, MAX_STATUTORY_DAYS);
        }
    }

    /**
     * 依年資年數計算法定特休天數（便利方法）
     *
     * @param yearsOfService 員工年資年數
     * @return 法定特休天數
     */
    public int calculateStatutoryDaysByYears(int yearsOfService) {
        return calculateStatutoryDays(yearsOfService * 12);
    }
}
