package com.company.hrms.timesheet.api.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateEntryRequest {
    @NotNull
    private UUID employeeId;

    @NotNull
    private UUID projectId;

    private UUID taskId; // Optional

    @NotNull
    private LocalDate workDate;

    @NotNull
    private BigDecimal hours;

    private String description;
}
