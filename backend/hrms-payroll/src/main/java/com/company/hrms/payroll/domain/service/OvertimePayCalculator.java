package com.company.hrms.payroll.domain.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.company.hrms.payroll.domain.model.valueobject.OvertimePayDetail;

/**
 * 加班費計算器 Domain Service
 * 負責計算各類型加班費
 */
@Service
public class OvertimePayCalculator {

    /**
     * 計算加班費明細
     *
     * @param hourlyRate   時薪
     * @param weekdayHours 平日加班時數
     * @param restDayHours 休息日加班時數
     * @param holidayHours 國定假日加班時數
     * @return 加班費明細
     */
    public OvertimePayDetail calculate(BigDecimal hourlyRate,
            BigDecimal weekdayHours,
            BigDecimal restDayHours,
            BigDecimal holidayHours) {

        BigDecimal validWeekdayHours = weekdayHours != null ? weekdayHours : BigDecimal.ZERO;
        BigDecimal validRestDayHours = restDayHours != null ? restDayHours : BigDecimal.ZERO;
        BigDecimal validHolidayHours = holidayHours != null ? holidayHours : BigDecimal.ZERO;

        BigDecimal weekdayPay = OvertimePayDetail.calculateWeekdayPay(hourlyRate, validWeekdayHours);
        BigDecimal restDayPay = OvertimePayDetail.calculateRestDayPay(hourlyRate, validRestDayHours);
        BigDecimal holidayPay = OvertimePayDetail.calculateHolidayPay(hourlyRate, validHolidayHours);

        return OvertimePayDetail.builder()
                .weekdayHours(validWeekdayHours)
                .weekdayPay(weekdayPay)
                .restDayHours(validRestDayHours)
                .restDayPay(restDayPay)
                .holidayHours(validHolidayHours)
                .holidayPay(holidayPay)
                .build();
    }
}
