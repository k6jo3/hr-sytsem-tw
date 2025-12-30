package com.company.hrms.payroll.domain.model.valueobject;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class OvertimePayDetailTest {

    @Test
    void shouldCalculateWeekdayPay() {
        BigDecimal rate = new BigDecimal("200"); // 時薪 200
        BigDecimal hours = new BigDecimal("4"); // 加班 4 小時

        // 前 2 小時 = 200 * 2 * 1.34 = 536
        // 後 2 小時 = 200 * 2 * 1.67 = 668
        // 總計 = 1204

        BigDecimal pay = OvertimePayDetail.calculateWeekdayPay(rate, hours);

        assertThat(pay).isEqualByComparingTo("1204");
    }

    @Test
    void shouldCalculateRestDayPay() {
        BigDecimal rate = new BigDecimal("200");
        BigDecimal hours = new BigDecimal("10"); // 加班 10 小時

        // 前 2 小時 = 200 * 2 * 1.34 = 536
        // 2-8 小時 (6hr) = 200 * 6 * 1.67 = 2004
        // 8+ 小時 (2hr) = 200 * 2 * 2.67 = 1068
        // 總計 = 3608

        BigDecimal pay = OvertimePayDetail.calculateRestDayPay(rate, hours);

        assertThat(pay).isEqualByComparingTo("3608");
    }

    @Test
    void shouldCalculateHolidayPay() {
        BigDecimal rate = new BigDecimal("200");
        BigDecimal hours = new BigDecimal("8");

        // 200 * 8 * 2 = 3200
        BigDecimal pay = OvertimePayDetail.calculateHolidayPay(rate, hours);

        assertThat(pay).isEqualByComparingTo("3200");
    }

    @Test
    void shouldCalculateTotal() {
        OvertimePayDetail detail = OvertimePayDetail.builder()
                .weekdayPay(new BigDecimal("1000"))
                .restDayPay(new BigDecimal("2000"))
                .holidayPay(new BigDecimal("3000"))
                .build();

        assertThat(detail.getTotal()).isEqualByComparingTo("6000");
    }
}
