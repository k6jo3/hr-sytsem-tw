package com.company.hrms.project.domain.model.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCustomerCommand {
    private String customerCode;
    private String customerName;
    private String taxId;
    private String industry;
    private String email;
    private String phoneNumber;
}
