package com.company.hrms.timesheet.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.timesheet.api.request.GetUnreportedEmployeesRequest;
import com.company.hrms.timesheet.api.response.GetUnreportedEmployeesResponse;

import lombok.RequiredArgsConstructor;

@Service("getUnreportedEmployeesServiceImpl")
@RequiredArgsConstructor
public class GetUnreportedEmployeesServiceImpl
        implements QueryApiService<GetUnreportedEmployeesRequest, GetUnreportedEmployeesResponse> {

    @Override
    public GetUnreportedEmployeesResponse getResponse(GetUnreportedEmployeesRequest request, JWTModel currentUser,
            String... args)
            throws Exception {

        // 簡化實作：實際應該查詢所有員工，然後比對工時表
        // 這需要整合 Organization Service 來取得員工清單
        // 目前回傳空列表
        // TODO: 邏輯未實作
        List<GetUnreportedEmployeesResponse.UnreportedEmployee> employees = new ArrayList<>();

        return GetUnreportedEmployeesResponse.builder()
                .employees(employees)
                .totalCount(0)
                .build();
    }
}
