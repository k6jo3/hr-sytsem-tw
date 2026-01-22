package com.company.hrms.workflow.domain.model.aggregate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.valueobject.UserDelegationId;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代理人設定聚合根
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDelegation extends AggregateRoot<UserDelegationId> {

    // delegationId managed by AggregateRoot

    private String delegatorId;
    private String delegateId;

    private LocalDate startDate;
    private LocalDate endDate;

    private boolean isActive;

    private String delegationScope; // ALL, SPECIFIC

    private List<FlowType> specificFlowTypes;

    private String reason;

    @Builder
    public UserDelegation(UserDelegationId delegationId, String delegatorId, String delegateId,
            LocalDate startDate, LocalDate endDate, boolean isActive,
            String delegationScope, List<FlowType> specificFlowTypes, String reason, LocalDateTime createdAt) {
        super(delegationId);
        this.delegatorId = delegatorId;
        this.delegateId = delegateId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.delegationScope = delegationScope;
        this.specificFlowTypes = specificFlowTypes;
        this.reason = reason;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // Helper
    public String getDelegationId() {
        return this.getId() != null ? this.getId().getValue() : null;
    }

    public boolean isActiveNow() {
        if (!isActive) {
            return false;
        }
        LocalDate today = LocalDate.now();
        boolean startOk = startDate == null || !today.isBefore(startDate);
        boolean endOk = endDate == null || !today.isAfter(endDate);
        return startOk && endOk;
    }
}
