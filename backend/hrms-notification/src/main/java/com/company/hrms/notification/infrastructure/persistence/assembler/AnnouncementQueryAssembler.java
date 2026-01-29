package com.company.hrms.notification.infrastructure.persistence.assembler;

import java.time.LocalDateTime;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

/**
 * 公告查詢組裝器
 * <p>
 * 負責將公告查詢需求轉換為 QueryGroup
 * </p>
 *
 * @author Claude
 * @since 2026-01-28
 */
public class AnnouncementQueryAssembler {

    /**
     * 查詢已發布公告
     * 
     * @param includeExpired 是否包含已過期
     * @return QueryGroup
     */
    public QueryGroup queryPublishedAnnouncements(boolean includeExpired) {
        var builder = QueryBuilder.where()
                .eq("status", "PUBLISHED")
                .eq("is_deleted", 0);

        if (!includeExpired) {
            // effective_to >= now OR effective_to IS NULL (假設 NULL 代表不過期)
            // 這裡簡化為 effective_to >= now，實際視欄位定義
            String now = LocalDateTime.now().toString();
            builder.and("effective_to", Operator.GTE, now);
        }

        return builder.build();
    }

    public QueryGroup queryAllAnnouncements() {
        return QueryBuilder.where()
                .eq("is_deleted", 0)
                .build();
    }

    public QueryGroup queryByTitle(String title) {
        return QueryBuilder.where()
                .and("title", Operator.LIKE, "%" + title + "%")
                .eq("is_deleted", 0)
                .build();
    }
}
