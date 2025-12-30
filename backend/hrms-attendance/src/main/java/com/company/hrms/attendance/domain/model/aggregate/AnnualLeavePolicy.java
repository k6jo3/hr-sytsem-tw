package com.company.hrms.attendance.domain.model.aggregate;

import java.util.ArrayList;
import java.util.List;

import com.company.hrms.attendance.domain.model.entity.AnnualLeaveRule;
import com.company.hrms.attendance.domain.model.valueobject.PolicyId;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

@Getter
public class AnnualLeavePolicy extends AggregateRoot<PolicyId> {

    private String name;
    private boolean active;
    private List<AnnualLeaveRule> rules;

    public AnnualLeavePolicy(PolicyId id, String name) {
        super(id);
        this.name = name;
        this.active = true;
        this.rules = new ArrayList<>();
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
}
