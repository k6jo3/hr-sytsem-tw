package com.company.hrms.timesheet.api.request;

import com.company.hrms.common.api.request.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GetPendingApprovalsRequest extends PageRequest {
}
