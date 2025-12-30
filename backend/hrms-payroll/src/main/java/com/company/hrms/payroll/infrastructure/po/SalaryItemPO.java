package com.company.hrms.payroll.infrastructure.po;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 薪資項目 Persistent Object
 */
@Entity
@Table(name = "hr04_salary_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryItemPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id", nullable = false)
    private SalaryStructurePO salaryStructure;

    @Column(name = "item_id", length = 36, nullable = false)
    private String itemId;

    @Column(name = "item_code", length = 50, nullable = false)
    private String code;

    @Column(name = "item_name", length = 100, nullable = false)
    private String name;

    @Column(name = "item_type", length = 20, nullable = false)
    private String type; // EARNING, DEDUCTION

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "is_fixed_amount")
    private boolean fixedAmount;

    @Column(name = "is_taxable")
    private boolean taxable;

    @Column(name = "is_insurable")
    private boolean insurable;
}
