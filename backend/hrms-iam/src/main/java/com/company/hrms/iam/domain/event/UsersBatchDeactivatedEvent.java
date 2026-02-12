package com.company.hrms.iam.domain.event;

import java.util.List;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 批次停用使用者事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsersBatchDeactivatedEvent extends DomainEvent {
    /** 成功停用的使用者 ID 列表 */
    private List<String> userIds;

    /** 停用的使用者數量 */
    private Integer count;

    @Override
    public String getAggregateId() {
        return "batch-" + System.currentTimeMillis();
    }

    @Override
    public String getAggregateType() {
        return "User";
    }
}
