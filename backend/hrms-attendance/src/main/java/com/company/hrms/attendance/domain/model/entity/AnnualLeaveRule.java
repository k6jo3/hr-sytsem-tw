package com.company.hrms.attendance.domain.model.entity;

import com.company.hrms.common.domain.model.Entity;
import com.company.hrms.common.domain.model.Identifier;

// Using a simple ID or just treating it as local entity with generated ID
// For simplicity reusing Identifier<String> or creating RuleId if strict.
// Checklist doesn't mention RuleId. I'll use String ID for internal entity.

public class AnnualLeaveRule extends Entity<Identifier<String>> {

    private int minServiceYears;
    private int maxServiceYears;
    private int days;

    public AnnualLeaveRule(Identifier<String> id, int minYear, int maxYear, int days) {
        super(id);
        this.minServiceYears = minYear;
        this.maxServiceYears = maxYear;
        this.days = days;
    }

    public boolean matches(int yearsOfService) {
        return yearsOfService >= minServiceYears && yearsOfService < maxServiceYears;
    }

    public int getDays() {
        return days;
    }
    // Getters etc
}
