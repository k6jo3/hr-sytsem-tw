package com.company.hrms.notification.infrastructure.persistence.assembler;

import com.company.hrms.common.querydsl.model.query.Operator;
import com.company.hrms.common.querydsl.model.query.QueryBuilder;
import com.company.hrms.common.querydsl.model.query.QueryGroup;
import org.springframework.stereotype.Component;

/**
 * 通知偏好設定查詢條件組裝器
 * <p>
 * 負責將查詢條件轉換為 QueryGroup (Fluent-Query-Engine)
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Component
public class PreferenceQueryAssembler {

    /**
     * 根據員工 ID 查詢偏好設定
     *
     * @param employeeId 員工 ID
     * @return QueryGroup
     */
    public QueryGroup queryByEmployeeId(String employeeId) {
        return QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .and("isDeleted", Operator.EQ, false)
                .build();
    }

    /**
     * 檢查員工是否已有偏好設定
     *
     * @param employeeId 員工 ID
     * @return QueryGroup
     */
    public QueryGroup existsByEmployeeId(String employeeId) {
        return QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .and("isDeleted", Operator.EQ, false)
                .build();
    }

    /**
     * 查詢啟用 Email 通知的使用者偏好設定
     *
     * @return QueryGroup
     */
    public QueryGroup queryEmailEnabledPreferences() {
        return QueryBuilder.where()
                .and("emailEnabled", Operator.EQ, true)
                .and("isDeleted", Operator.EQ, false)
                .build();
    }

    /**
     * 查詢啟用推播通知的使用者偏好設定
     *
     * @return QueryGroup
     */
    public QueryGroup queryPushEnabledPreferences() {
        return QueryBuilder.where()
                .and("pushEnabled", Operator.EQ, true)
                .and("isDeleted", Operator.EQ, false)
                .build();
    }
}
