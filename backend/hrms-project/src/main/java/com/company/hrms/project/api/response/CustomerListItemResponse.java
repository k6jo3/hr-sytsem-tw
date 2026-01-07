package com.company.hrms.project.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerListItemResponse {
    private String customerId;
    private String customerCode;
    private String customerName;
    private String taxId;
    private String industry;
    private String contactInfo; // Email / Phone combined or separate? Keep separate usually but here just
                                // sample fields
    private String email;
    private String phoneNumber;
    private String status;
}
