package com.company.hrms.iam.application.service.role.task;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入角色 Task
 */
@Component
@RequiredArgsConstructor
public class LoadRoleTask implements PipelineTask<RoleContext> {

    private final IRoleRepository roleRepository;

    @Override
    public void execute(RoleContext context) throws Exception {
        String roleId = context.getRoleId();

        // 建立查詢條件（包含安全性過濾）
        QueryGroup query = QueryBuilder.where()
                .eq("role_id", roleId)
                .eq("is_deleted", false)  // 軟刪除過濾
                .build();

        // 查詢角色
        var role = roleRepository.findAll(query).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("ROLE_NOT_FOUND",
                        "角色不存在: " + roleId));

        context.setRole(role);
    }

    @Override
    public String getName() {
        return "載入角色";
    }
}
