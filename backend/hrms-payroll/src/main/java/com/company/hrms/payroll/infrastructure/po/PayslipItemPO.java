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

@Data
@Entity
@Table(name = "hr04_payslip_items")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayslipItemPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payslip_id", nullable = false)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private PayslipPO payslip;

    @Column(name = "item_id", length = 36, nullable = false)
    private String itemId;

    @Column(name = "item_code", length = 50, nullable = false)
    private String code;

    @Column(name = "item_name", length = 100, nullable = false)
    private String name;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "item_type", length = 20, nullable = false)
    private String type; // EARNING, DEDUCTION

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "source", length = 20)
    private String source; // SYSTEM, MANUAL, IMPORT

    @Column(name = "is_taxable")
    private boolean taxable;

    @Column(name = "is_insurable")
    private boolean insurable;
}
