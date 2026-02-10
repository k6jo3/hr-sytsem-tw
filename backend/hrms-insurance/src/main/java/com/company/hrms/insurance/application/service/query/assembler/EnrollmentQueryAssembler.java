package com.company.hrms.insurance.application.service.query.assembler;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryFilter;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.insurance.api.request.GetEnrollmentListRequest;

/**
 * 加退保紀錄查詢組裝器
 * 負責將 Request 轉換為 QueryGroup
 */
@Component
public class EnrollmentQueryAssembler {

    /**
     * 轉換請求為查詢群組
     *
     * @param request 查詢請求
     * @return QueryGroup 查詢條件
     */
    public QueryGroup toQueryGroup(GetEnrollmentListRequest request) {
        // 使用 Fluent-Query-Engine 自動解析條件
        var builder = QueryBuilder.where().fromDto(request);

        // 1. 基礎過濾: 未刪除
        builder.and("is_deleted", Operator.EQ, 0);

        // 2. 當前使用者過濾 (個人查詢)
        // 手動處理，因為 employeeId 已經有標註，如果 currentUserId 也有值，
        // 則再加一個 employee_id 條件可能導致衝突或重複，
        // 但若是為了權限控管，通常是強制覆蓋或增加 AND 條件。
        // 原邏輯是: eq("employee_id", request.getCurrentUserId())
        if (request.getCurrentUserId() != null && !request.getCurrentUserId().isBlank()) {
            builder.and("employee_id", Operator.EQ, request.getCurrentUserId());
        }

        return builder.build();
    }

    /**
     * 建構勞保查詢條件
     */
    public QueryGroup toLaborInsuranceQuery(GetEnrollmentListRequest request) {
        QueryGroup query = toQueryGroup(request);
        query.add(new com.company.hrms.common.query.FilterUnit("insurance_type", Operator.EQ, "LABOR"));
        return query;
    }

    /**
     * 建構健保查詢條件
     */
    public QueryGroup toHealthInsuranceQuery(GetEnrollmentListRequest request) {
        var builder = QueryBuilder.where();

        // 1. 基礎過濾
        builder.and("is_deleted", Operator.EQ, 0);
        builder.and("insurance_type", Operator.EQ, "HEALTH");

        // 2. 從 DTO 自動解析條件（但排除 hasDependents，需要特殊處理）
        addDtoFieldsExcept(builder, request, "hasDependents", "currentUserId", "hasVoluntary");

        // 3. 特殊處理：hasDependents (Boolean → 1/0)
        if (request.getHasDependents() != null) {
            builder.and("has_dependents", Operator.EQ, request.getHasDependents() ? 1 : 0);
        }

        // 4. 當前使用者過濾
        if (request.getCurrentUserId() != null && !request.getCurrentUserId().isBlank()) {
            builder.and("employee_id", Operator.EQ, request.getCurrentUserId());
        }

        return builder.build();
    }

    /**
     * 建構勞退查詢條件
     */
    public QueryGroup toPensionQuery(GetEnrollmentListRequest request) {
        var builder = QueryBuilder.where().fromDto(request);

        // 1. 基礎過濾
        builder.and("is_deleted", Operator.EQ, 0);
        builder.and("insurance_type", Operator.EQ, "PENSION");

        // 2. 當前使用者過濾
        if (request.getCurrentUserId() != null && !request.getCurrentUserId().isBlank()) {
            builder.and("employee_id", Operator.EQ, request.getCurrentUserId());
        }

        // 3. 特殊處理：hasVoluntary (voluntary_rate > 0)
        if (Boolean.TRUE.equals(request.getHasVoluntary())) {
            builder.and("voluntary_rate", Operator.GT, 0);
        }

        return builder.build();
    }

    /**
     * 建構眷屬資料查詢條件
     */
    public QueryGroup toDependentQuery(GetEnrollmentListRequest request) {
        var builder = QueryBuilder.where().fromDto(request);

        // 1. 基礎過濾: 未刪除
        builder.and("is_deleted", Operator.EQ, 0);

        // 2. 當前使用者過濾 (個人查詢)
        if (request.getCurrentUserId() != null && !request.getCurrentUserId().isBlank()) {
            builder.and("employee_id", Operator.EQ, request.getCurrentUserId());
        }

        return builder.build();
    }

    /**
     * 建構職災紀錄查詢條件
     */
    public QueryGroup toWorkInjuryQuery(GetEnrollmentListRequest request) {
        var builder = QueryBuilder.where().fromDto(request);

        // 1. 基礎過濾: 未刪除
        builder.and("is_deleted", Operator.EQ, 0);

        // 2. 當前使用者過濾 (個人查詢)
        if (request.getCurrentUserId() != null && !request.getCurrentUserId().isBlank()) {
            builder.and("employee_id", Operator.EQ, request.getCurrentUserId());
        }

        return builder.build();
    }

    /**
     * 從 DTO 添加欄位條件（排除指定欄位）
     *
     * @param builder QueryBuilder
     * @param dto 請求 DTO
     * @param excludeFields 要排除的欄位名稱
     */
    private void addDtoFieldsExcept(QueryBuilder builder, Object dto, String... excludeFields) {
        if (dto == null) {
            return;
        }

        Set<String> excludeSet = Arrays.stream(excludeFields).collect(Collectors.toSet());

        for (Field field : dto.getClass().getDeclaredFields()) {
            if (excludeSet.contains(field.getName())) {
                continue;
            }

            field.setAccessible(true);
            try {
                Object value = field.get(dto);
                if (value != null && field.isAnnotationPresent(QueryFilter.class)) {
                    QueryFilter filter = field.getAnnotation(QueryFilter.class);
                    String property = filter.property().isEmpty() ? field.getName() : filter.property();
                    builder.and(property, filter.operator(), value);
                }
            } catch (IllegalAccessException e) {
                // 忽略無法存取的欄位
            }
        }
    }
}
