package com.company.hrms.project.api.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerListResponse {
    private List<CustomerListItemResponse> items;
    private long total;
    private int page;
    private int size;
    private int totalPages;
}
