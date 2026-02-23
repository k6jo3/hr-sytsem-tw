package com.company.hrms.reporting.application.service.export.task;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.reporting.api.request.GetEmployeeRosterRequest;
import com.company.hrms.reporting.application.service.export.context.ExportExcelContext;
import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.EmployeeRosterReadModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 載入員工花名冊資料任務
 */
@Component
@RequiredArgsConstructor
@Slf4j

public class LoadEmployeeRosterDataTask implements PipelineTask<ExportExcelContext> {

    private final EmployeeRosterReadModelRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(ExportExcelContext ctx) throws Exception {
        if (!"EMPLOYEE_ROSTER".equals(ctx.getRequest().getReportType())) {
            return;
        }

        log.info("執行載入員工花名冊資料任務");

        // 1. 解析查詢條件
        GetEmployeeRosterRequest searchReq = new GetEmployeeRosterRequest();
        java.util.Map<String, Object> filters = ctx.getRequest().getFilters();
        if (filters != null && !filters.isEmpty()) {
            searchReq = objectMapper.convertValue(filters, GetEmployeeRosterRequest.class);
        }
        searchReq.setTenantId(ctx.getCurrentUser().getTenantId());

        // 2. 查詢資料
        Specification<EmployeeRosterReadModel> spec = buildSpec(searchReq);
        List<EmployeeRosterReadModel> entities = repository.findAll(spec);

        // 3. 準備 Excel 表頭與資料
        ctx.setHeaders(List.of("員工編號", "姓名", "部門", "職位", "到職日期", "年資", "狀態", "電話", "Email"));
        ctx.setSheetName("員工花名冊");

        List<List<Object>> rows = entities.stream()
                .map(this::toRow)
                .toList();
        ctx.setData(rows);
    }

    private Specification<EmployeeRosterReadModel> buildSpec(GetEmployeeRosterRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("tenantId"), request.getTenantId()));
            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (request.getDepartmentId() != null && !request.getDepartmentId().isBlank()) {
                predicates.add(cb.equal(root.get("departmentId"), request.getDepartmentId()));
            }
            if (request.getStatus() != null && !request.getStatus().isBlank()) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getHireDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("hireDate"), request.getHireDateFrom()));
            }
            if (request.getHireDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("hireDate"), request.getHireDateTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<Object> toRow(EmployeeRosterReadModel model) {
        List<Object> row = new ArrayList<>();
        row.add(model.getEmployeeId());
        row.add(model.getName());
        row.add(model.getDepartmentName());
        row.add(model.getPositionName());
        row.add(model.getHireDate());

        // 計算年資
        Double serviceYears = model.getServiceYears();
        if (model.getHireDate() != null) {
            long days = ChronoUnit.DAYS.between(model.getHireDate(), java.time.LocalDate.now());
            serviceYears = Math.round(days / 365.25 * 10.0) / 10.0;
        }
        row.add(serviceYears);

        row.add(model.getStatus());
        row.add(model.getPhone());
        row.add(model.getEmail());
        return row;
    }

    @Override
    public String getName() {
        return "載入員工花名冊資料";
    }
}
