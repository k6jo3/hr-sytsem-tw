package com.company.hrms.payroll.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRunActionRequest {
    private String runId;
    private String reason; // For Reject/Cancel
    private String bankFileUrl; // For MarkPaid
}
