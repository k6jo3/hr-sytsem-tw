package com.company.hrms.workflow.application.service;

import lombok.Data;

// TODO: request怎麼會放在service層?
@Data
public class DeleteDelegationRequest {
    private String delegationId;
}
