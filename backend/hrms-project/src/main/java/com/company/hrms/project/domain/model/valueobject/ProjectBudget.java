package com.company.hrms.project.domain.model.valueobject;

import java.math.BigDecimal;

import com.company.hrms.common.domain.model.ValueObject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectBudget extends ValueObject {

    @Enumerated(EnumType.STRING)
    @Column(name = "budget_type")
    private BudgetType budgetType;

    @Column(name = "budget_amount")
    private BigDecimal budgetAmount;

    @Column(name = "budget_hours")
    private BigDecimal budgetHours;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProjectBudget that = (ProjectBudget) o;
        if (budgetType != that.budgetType)
            return false;
        if (budgetAmount != null ? !budgetAmount.equals(that.budgetAmount) : that.budgetAmount != null)
            return false;
        return budgetHours != null ? budgetHours.equals(that.budgetHours) : that.budgetHours == null;
    }

    @Override
    public int hashCode() {
        int result = budgetType != null ? budgetType.hashCode() : 0;
        result = 31 * result + (budgetAmount != null ? budgetAmount.hashCode() : 0);
        result = 31 * result + (budgetHours != null ? budgetHours.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProjectBudget{" +
                "budgetType=" + budgetType +
                ", budgetAmount=" + budgetAmount +
                ", budgetHours=" + budgetHours +
                '}';
    }
}
