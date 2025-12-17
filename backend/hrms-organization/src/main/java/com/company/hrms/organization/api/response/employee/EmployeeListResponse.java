package com.company.hrms.organization.api.response.employee;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 員工列表回應 DTO
 */
@Data
@Builder
public class EmployeeListResponse {

    private List<EmployeeListItemResponse> items;
    private long total;
    private int page;
    private int size;
    private int totalPages;
}
