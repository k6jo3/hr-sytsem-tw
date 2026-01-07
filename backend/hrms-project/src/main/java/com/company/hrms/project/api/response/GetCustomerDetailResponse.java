package com.company.hrms.project.api.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerDetailResponse {
    private String customerId;
    private String customerCode;
    private String customerName;
    private String taxId;
    private String industry;
    private String email;
    private String phoneNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
