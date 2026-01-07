package com.company.hrms.project.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.project.api.response.GetCustomerDetailResponse;
import com.company.hrms.project.domain.model.aggregate.Customer;

import lombok.Getter;
import lombok.Setter;

/**
 * 客戶詳情查詢 Context
 */
@Getter
@Setter
public class CustomerDetailContext extends PipelineContext {

    // 輸入
    private String customerId;

    // 中間結果
    private Customer customer;

    // 輸出
    private GetCustomerDetailResponse response;

    public CustomerDetailContext(String customerId) {
        this.customerId = customerId;
    }
}
