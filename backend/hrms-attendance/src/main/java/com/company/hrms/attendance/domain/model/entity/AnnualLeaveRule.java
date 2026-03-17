package com.company.hrms.attendance.domain.model.entity;

import com.company.hrms.common.domain.model.Entity;
import com.company.hrms.common.domain.model.Identifier;

import lombok.Getter;

/**
 * 特休規則實體
 *
 * <p>以「月數」為精度定義年資區間與對應特休天數。
 * 支援勞基法第 38 條的 6 個月(含)起算等非整數年資情境。
 *
 * <p>建構參數：
 * <ul>
 *   <li>minServiceMonths — 最低年資月數（含）</li>
 *   <li>maxServiceMonths — 最高年資月數（不含）</li>
 *   <li>days — 對應特休天數</li>
 * </ul>
 */
@Getter
public class AnnualLeaveRule extends Entity<Identifier<String>> {

    /** 最低年資月數（含） */
    private int minServiceMonths;

    /** 最高年資月數（不含） */
    private int maxServiceMonths;

    /** 對應特休天數 */
    private int days;

    /**
     * 建立特休規則
     *
     * @param id               規則 ID
     * @param minServiceMonths 最低年資月數（含）
     * @param maxServiceMonths 最高年資月數（不含）
     * @param days             對應特休天數
     */
    public AnnualLeaveRule(Identifier<String> id, int minServiceMonths, int maxServiceMonths, int days) {
        super(id);
        this.minServiceMonths = minServiceMonths;
        this.maxServiceMonths = maxServiceMonths;
        this.days = days;
    }

    /**
     * 依月數判斷是否匹配此規則
     *
     * @param serviceMonths 員工年資月數
     * @return 是否落在此規則的月數區間 [min, max)
     */
    public boolean matchesMonths(int serviceMonths) {
        return serviceMonths >= minServiceMonths && serviceMonths < maxServiceMonths;
    }

    /**
     * 向後相容：依年數判斷是否匹配（年數 * 12 轉月數）
     *
     * @param yearsOfService 員工年資年數
     * @return 是否匹配
     */
    public boolean matches(int yearsOfService) {
        return matchesMonths(yearsOfService * 12);
    }
}
