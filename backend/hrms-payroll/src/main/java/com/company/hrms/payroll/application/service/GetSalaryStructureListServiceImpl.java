package com.company.hrms.payroll.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.common.query.Condition;
import com.company.hrms.payroll.application.dto.request.GetSalaryStructureListRequest;
import com.company.hrms.payroll.application.dto.response.SalaryStructureResponse;
import com.company.hrms.payroll.application.factory.SalaryStructureDtoFactory;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.repository.ISalaryStructureRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢薪資結構列表服務
 *
 * <h3>重構說明 (Fluent-Query-Engine)</h3>
 * <p>
 * 本服務採用<b>註解式查詢條件</b>模式，透過 {@code @EQ}、{@code @LIKE} 等註解
 * 宣告於 Request DTO 欄位上，自動建構 WHERE 條件，無需手動撰寫 if-else 判斷。
 * </p>
 *
 * <h4>重構前 (手動建構條件)：</h4>
 * <pre>
 * QueryBuilder builder = QueryBuilder.where();
 * if (request.getEmployeeId() != null &amp;&amp; !request.getEmployeeId().isEmpty()) {
 *     builder.and("employeeId", Operator.EQ, request.getEmployeeId());
 * }
 * if (request.getActive() != null) {
 *     builder.and("active", Operator.EQ, request.getActive());
 * }
 * // ... 每個欄位都要寫一次 if-else
 * </pre>
 *
 * <h4>重構後 (註解式宣告)：</h4>
 * <pre>
 * // Request DTO 只需標註
 * public class GetSalaryStructureListRequest {
 *     &#64;EQ private String employeeId;
 *     &#64;EQ private Boolean active;
 * }
 *
 * // Service 一行搞定
 * Page&lt;SalaryStructure&gt; result = repository.findPage(Condition.of(request));
 * </pre>
 *
 * <p>
 * 此模式符合架構師實踐清單的<b>原則一：能用「宣告」的，就不要用「程式碼」</b>。
 * </p>
 */
@Service("getSalaryStructureListServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSalaryStructureListServiceImpl
        implements QueryApiService<GetSalaryStructureListRequest, PageResponse<SalaryStructureResponse>> {

    private final ISalaryStructureRepository repository;

    @Override
    public PageResponse<SalaryStructureResponse> getResponse(
            GetSalaryStructureListRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // ===== 重構後：一行完成查詢 =====
        // Request DTO 上的 @EQ 註解會自動解析為 WHERE 條件
        // Condition.of() 自動處理分頁參數 (page, size, sortBy, sortDirection)
        Page<SalaryStructure> resultPage = repository.findPageByCondition(
                Condition.of(request)
                        .page(request.getPage() - 1)  // 轉換為 0-based index
                        .size(request.getSize())
                        .sort(request.getSortBy(), toSortDirection(request.getSortDirection()))
        );

        // 轉換為 Response DTO
        List<SalaryStructureResponse> items = resultPage.getContent().stream()
                .map(SalaryStructureDtoFactory::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(items, request.getPage(), request.getSize(), resultPage.getTotalElements());
    }

    /**
     * 轉換排序方向
     */
    private org.springframework.data.domain.Sort.Direction toSortDirection(
            com.company.hrms.common.api.request.PageRequest.SortDirection direction) {
        if (direction == null) {
            return org.springframework.data.domain.Sort.Direction.DESC;
        }
        return direction == com.company.hrms.common.api.request.PageRequest.SortDirection.ASC
                ? org.springframework.data.domain.Sort.Direction.ASC
                : org.springframework.data.domain.Sort.Direction.DESC;
    }
}
