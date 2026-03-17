package com.company.hrms.attendance.domain.model.aggregate;

import java.util.ArrayList;
import java.util.List;

import com.company.hrms.attendance.domain.model.entity.AnnualLeaveRule;
import com.company.hrms.attendance.domain.model.valueobject.AnnualLeaveSystem;
import com.company.hrms.attendance.domain.model.valueobject.ExpiryPolicy;
import com.company.hrms.attendance.domain.model.valueobject.OverdrawPolicy;
import com.company.hrms.attendance.domain.model.valueobject.PolicyId;
import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.common.domain.model.Identifier;

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

    /**
     * 依年數計算特休天數（向後相容）
     *
     * @param yearsOfService 年資年數
     * @return 對應特休天數
     */
    public int calculateDays(int yearsOfService) {
        return rules.stream()
                .filter(r -> r.matches(yearsOfService))
                .findFirst()
                .map(AnnualLeaveRule::getDays)
                .orElse(0);
    }

    /**
     * 依月數計算特休天數
     *
     * @param serviceMonths 年資月數
     * @return 對應特休天數
     */
    public int calculateDaysByMonths(int serviceMonths) {
        return rules.stream()
                .filter(r -> r.matchesMonths(serviceMonths))
                .findFirst()
                .map(AnnualLeaveRule::getDays)
                .orElse(0);
    }

    /**
     * 建立勞基法第 38 條法定特休預設政策
     *
     * <p>法定年資段落與天數：
     * <ul>
     *   <li>6 個月(含) ~ 未滿 1 年：3 天</li>
     *   <li>1 年(含) ~ 未滿 2 年：7 天</li>
     *   <li>2 年(含) ~ 未滿 3 年：10 天</li>
     *   <li>3 年(含) ~ 未滿 5 年：14 天</li>
     *   <li>5 年(含) ~ 未滿 10 年：15 天</li>
     * </ul>
     * <p>注意：10 年以上每年加 1 天（最多 30 天）的計算不在此規則內，
     * 需由 {@link com.company.hrms.attendance.domain.service.StatutoryAnnualLeaveCalculator} 處理。
     *
     * @return 包含法定規則的 AnnualLeavePolicy
     */
    public static AnnualLeavePolicy createStatutoryDefault() {
        AnnualLeavePolicy policy = new AnnualLeavePolicy(
            PolicyId.next(), "勞基法第38條法定特休"
        );
        policy.annualLeaveSystem = AnnualLeaveSystem.ANNIVERSARY;

        int seq = 1;
        // 6 個月(含) ~ 未滿 1 年：3 天
        policy.addRule(createRule(seq++, 6, 12, 3));
        // 1 年(含) ~ 未滿 2 年：7 天
        policy.addRule(createRule(seq++, 12, 24, 7));
        // 2 年(含) ~ 未滿 3 年：10 天
        policy.addRule(createRule(seq++, 24, 36, 10));
        // 3 年(含) ~ 未滿 5 年：14 天
        policy.addRule(createRule(seq++, 36, 60, 14));
        // 5 年(含) ~ 未滿 10 年：15 天
        policy.addRule(createRule(seq++, 60, 120, 15));

        return policy;
    }

    private static AnnualLeaveRule createRule(int seq, int minMonths, int maxMonths, int days) {
        Identifier<String> ruleId = new Identifier<String>("STATUTORY-" + seq) {};
        return new AnnualLeaveRule(ruleId, minMonths, maxMonths, days);
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
