package com.company.hrms.attendance.infrastructure.po;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 輪班天序持久化物件
 */
@Entity
@Table(name = "rotation_days")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RotationDayPO {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "pattern_id", length = 50, nullable = false)
    private String patternId;

    @Column(name = "day_order", nullable = false)
    private Integer dayOrder;

    @Column(name = "shift_id", length = 50)
    private String shiftId;

    @Column(name = "is_rest_day")
    private Boolean isRestDay;
}
