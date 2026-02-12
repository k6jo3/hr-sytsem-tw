package com.company.hrms.iam.api.request.role;

import java.util.List;

/**
 * 指派權限請求
 */
public class AssignPermissionsRequest {
    private List<String> permissionIds;

    public AssignPermissionsRequest() {
    }

    public AssignPermissionsRequest(List<String> permissionIds) {
        this.permissionIds = permissionIds;
    }

    public List<String> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<String> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
