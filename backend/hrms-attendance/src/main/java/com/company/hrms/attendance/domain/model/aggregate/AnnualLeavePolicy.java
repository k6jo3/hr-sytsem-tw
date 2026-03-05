package com.company.hrms.attendance.domain.model.aggregate;

import java.util.ArrayList;
import java.util.List;

import com.company.hrms.attendance.domain.model.entity.AnnualLeaveRule;
import com.company.hrms.attendance.domain.model.valueobject.AnnualLeaveSystem;
import com.company.hrms.attendance.domain.model.valueobject.ExpiryPolicy;
import com.company.hrms.attendance.domain.model.valueobject.OverdrawPolicy;
import com.company.hrms.attendance.domain.model.valueobject.PolicyId;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

@Getter
public class AnnualLeavePolicy extends AggregateRoot<PolicyId> {

    private String name;
    private boolean active;
    private List<AnnualLeaveRule> rules;

    /** 特休年度制度（歷年制/週年制） */
    private AnnualLeaveSystem annualLeaveSystem;

    /** 超額請假政策 */
    private OverdrawPolicy overdrawPolicy;

    /** 未休假處理政策 */
    private ExpiryPolicy expiryPolicy;

    /** 結轉上限天數（僅 CARRYOVER 政策使用） */
    private Integer carryOverLimit;

    public AnnualLeavePolicy(PolicyId id, String name) {
        super(id);
        this.name = name;
        this.active = true;
        this.rules = new ArrayList<>();
        this.annualLeaveSystem = AnnualLeaveSystem.CALENDAR_YEAR;
        this.overdrawPolicy = OverdrawPolicy.DENY;
        this.expiryPolicy = ExpiryPolicy.PAY_COMPENSATION;
    }

    public void addRule(AnnualLeaveRule rule) {
        this.rules.add(rule);
    }

    public int calculateDays(int yearsOfService) {
        return rules.stream()
                .filter(r -> r.matches(yearsOfService))
                .findFirst()
                .map(AnnualLeaveRule::getDays)
                .orElse(0);
    }

    /**
     * 變更年度制度
     */
    public void changeAnnualLeaveSystem(AnnualLeaveSystem system) {
        if (system == null) {
            throw new IllegalArgumentException("年度制度不可為空");
        }
        this.annualLeaveSystem = system;
    }

    /**
     * 變更超額請假政策
     */
    public void changeOverdrawPolicy(OverdrawPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("超額請假政策不可為空");
        }
        this.overdrawPolicy = policy;
    }

    /**
     * 變更未休假處理政策
     */
    public void changeExpiryPolicy(ExpiryPolicy policy, Integer carryOverLimit) {
        if (policy == null) {
            throw new IllegalArgumentException("未休假處理政策不可為空");
        }
        if (policy == ExpiryPolicy.CARRYOVER && carryOverLimit != null && carryOverLimit < 0) {
            throw new IllegalArgumentException("結轉上限天數不可為負數");
        }
        this.expiryPolicy = policy;
        this.carryOverLimit = (policy == ExpiryPolicy.CARRYOVER) ? carryOverLimit : null;
    }
}
