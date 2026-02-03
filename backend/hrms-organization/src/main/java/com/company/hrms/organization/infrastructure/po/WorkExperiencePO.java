package com.company.hrms.organization.infrastructure.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "work_experiences")
public class WorkExperiencePO {
    @Id
    private String id;
    private String employeeId;
    private String companyName;
    private String jobTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
