package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
// TODO: 未實作邏輯

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.employee.EmployeeDetailResponse;

/**
 * 取得員工詳情服務實作
 */
@Service("getEmployeeDetailServiceImpl")
public class GetEmployeeDetailServiceImpl implements QueryApiService<Object, EmployeeDetailResponse> {

    @Override
    public EmployeeDetailResponse getResponse(Object request, JWTModel currentUser, String... args) throws Exception {
        // TODO: 未實作邏輯
        return null;
    }
}
