package com.company.hrms.reporting.application.service.report;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetEmployeeRosterRequest;
import com.company.hrms.reporting.api.response.EmployeeRosterResponse;
import com.company.hrms.reporting.api.response.EmployeeRosterResponse.EmployeeRosterItem;
import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.EmployeeRosterReadModelRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

/**
 * 員工花名冊查詢 Service
 * 
 * <p>
 * 從 CQRS 讀模型查詢資料
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service("getEmployeeRosterServiceImpl")
@RequiredArgsConstructor
public class GetEmployeeRosterServiceImpl
                implements QueryApiService<GetEmployeeRosterRequest, EmployeeRosterResponse> {

        private final EmployeeRosterReadModelRepository readModelRepository;

        @Override
        public EmployeeRosterResponse getResponse(
                        GetEmployeeRosterRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                // 設定租戶ID (多租戶隔離)
                request.setTenantId(currentUser.getTenantId());

                // 建立查詢條件
                Specification<EmployeeRosterReadModel> spec = buildSpecification(request);

                // 建立分頁參數
                Pageable pageable = PageRequest.of(
                                request.getPage(),
                                request.getSize());

                // 從讀模型查詢
                Page<EmployeeRosterReadModel> page = readModelRepository.findAll(spec, pageable);

                // 轉換為回應
                var items = page.getContent().stream()
                                .map(this::toEmployeeRosterItem)
                                .toList();

                return EmployeeRosterResponse.builder()
                                .content(items)
                                .totalElements(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .build();
        }

        /**
         * 建立查詢條件
         */
        private Specification<EmployeeRosterReadModel> buildSpecification(GetEmployeeRosterRequest request) {
                return (root, query, cb) -> {
                        List<Predicate> predicates = new java.util.ArrayList<>();

                        // 租戶隔離 (必要條件)
                        predicates.add(cb.equal(root.get("tenantId"), request.getTenantId()));

                        // 未刪除
                        predicates.add(cb.equal(root.get("isDeleted"), false));

                        // 組織ID
                        if (request.getOrganizationId() != null) {
                                predicates.add(cb.equal(root.get("departmentId"), request.getOrganizationId()));
                        }

                        // 部門ID
                        if (request.getDepartmentId() != null) {
                                predicates.add(cb.equal(root.get("departmentId"), request.getDepartmentId()));
                        }

                        // 員工狀態
                        if (request.getStatus() != null) {
                                predicates.add(cb.equal(root.get("status"), request.getStatus()));
                        }

                        // 到職日期起
                        if (request.getHireDateFrom() != null) {
                                predicates.add(cb.greaterThanOrEqualTo(root.get("hireDate"),
                                                request.getHireDateFrom()));
                        }

                        // 到職日期迄
                        if (request.getHireDateTo() != null) {
                                predicates.add(cb.lessThanOrEqualTo(root.get("hireDate"), request.getHireDateTo()));
                        }

                        return cb.and(predicates.toArray(new Predicate[0]));
                };
        }

        /**
         * 轉換為員工花名冊項目
         */
        private EmployeeRosterItem toEmployeeRosterItem(EmployeeRosterReadModel model) {
                // 重新計算年資 (確保最新)
                Double serviceYears = model.getServiceYears();
                if (model.getHireDate() != null) {
                        long days = ChronoUnit.DAYS.between(model.getHireDate(), LocalDate.now());
                        serviceYears = Math.round(days / 365.25 * 10.0) / 10.0;
                }

                return EmployeeRosterItem.builder()
                                .employeeId(model.getEmployeeId())
                                .name(model.getName())
                                .departmentName(model.getDepartmentName())
                                .positionName(model.getPositionName())
                                .hireDate(model.getHireDate())
                                .serviceYears(serviceYears)
                                .status(model.getStatus())
                                .phone(model.getPhone())
                                .email(model.getEmail())
                                .build();
        }
}
