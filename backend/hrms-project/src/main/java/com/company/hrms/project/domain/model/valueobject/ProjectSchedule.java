package com.company.hrms.project.domain.model.valueobject;

import java.time.LocalDate;

import com.company.hrms.common.domain.model.ValueObject;
import com.company.hrms.common.exception.DomainException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSchedule extends ValueObject {

    @Column(name = "planned_start_date", nullable = false)
    private LocalDate plannedStartDate;

    @Column(name = "planned_end_date", nullable = false)
    private LocalDate plannedEndDate;

    @Column(name = "actual_start_date")
    private LocalDate actualStartDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    public ProjectSchedule(LocalDate plannedStartDate, LocalDate plannedEndDate) {
        if (plannedEndDate.isBefore(plannedStartDate)) {
            throw new DomainException("計畫結束日期不能早於開始日期");
        }
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
    }

    public void setActualStartDate(LocalDate date) {
        this.actualStartDate = date;
    }

    public void setActualEndDate(LocalDate date) {
        this.actualEndDate = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProjectSchedule that = (ProjectSchedule) o;
        if (plannedStartDate != null ? !plannedStartDate.equals(that.plannedStartDate) : that.plannedStartDate != null)
            return false;
        return plannedEndDate != null ? plannedEndDate.equals(that.plannedEndDate) : that.plannedEndDate == null;
    }

    @Override
    public int hashCode() {
        int result = plannedStartDate != null ? plannedStartDate.hashCode() : 0;
        result = 31 * result + (plannedEndDate != null ? plannedEndDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProjectSchedule{" +
                "plannedStartDate=" + plannedStartDate +
                ", plannedEndDate=" + plannedEndDate +
                '}';
    }
}
