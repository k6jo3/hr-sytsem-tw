package com.company.hrms.organization.application.service.department;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.department.DepartmentManagersResponse;

/**
 * 查詢部門主管層級 Application Service
 */
@Service("getDepartmentManagersServiceImpl")
public class GetDepartmentManagersServiceImpl implements QueryApiService<Object, DepartmentManagersResponse> {

    @Override
    public DepartmentManagersResponse getResponse(Object request, JWTModel currentUser, String... args)
            throws Exception {
        // TODO: 未實作邏輯
        return null;
    }
}
