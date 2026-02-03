package com.company.hrms.organization.infrastructure.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 學歷持久化對象
 */
@Data
@Entity
@Table(name = "educations")
public class EducationPO {

    @Id
    private String id;
    private String employeeId;
    private String schoolName;
    private String degree;
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isGraduated;

    // 審計欄位
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
