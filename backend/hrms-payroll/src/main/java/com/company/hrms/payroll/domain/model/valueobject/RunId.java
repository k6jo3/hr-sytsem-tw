package com.company.hrms.payroll.domain.model.valueobject;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 薪資批次 ID 值物件
 * 封裝薪資計算批次的唯一識別碼
 */
@Getter
@EqualsAndHashCode
public class RunId {

    private final String value;

    /**
     * 建構 RunId 值物件
     * 
     * @param value 薪資批次 ID
     * @throws IllegalArgumentException 當 value 為 null 或空白時
     */
    public RunId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RunId cannot be null or blank");
        }
        this.value = value;
    }

    /**
     * 產生新的 RunId
     * 
     * @return 新的 RunId
     */
    public static RunId generate() {
        return new RunId(UUID.randomUUID().toString());
    }

    /**
     * 根據年月產生 RunId (格式: PR-YYYYMM)
     * 
     * @param year  年
     * @param month 月
     * @return 新的 RunId
     */
    public static RunId fromYearMonth(int year, int month) {
        return new RunId(String.format("PR-%04d%02d", year, month));
    }

    @Override
    public String toString() {
        return value;
    }
}
