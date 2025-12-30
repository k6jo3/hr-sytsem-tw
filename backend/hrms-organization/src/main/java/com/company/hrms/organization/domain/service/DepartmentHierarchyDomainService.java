package com.company.hrms.organization.domain.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 部門層級管理 Domain Service
 * 負責處理部門階層結構的驗證和操作
 */
@Service
@RequiredArgsConstructor
public class DepartmentHierarchyDomainService {

    private final IDepartmentRepository departmentRepository;

    /**
     * 最大部門層級深度
     */
    private static final int MAX_HIERARCHY_DEPTH = 5;

    /**
     * 驗證新增子部門是否超過最大深度
     * 
     * @param parentId 父部門ID
     * @return 是否可以新增
     */
    public boolean canAddChildDepartment(DepartmentId parentId) {
        if (parentId == null) {
            return true; // 根部門
        }

        int depth = calculateDepth(parentId);
        return depth < MAX_HIERARCHY_DEPTH;
    }

    /**
     * 計算部門深度
     * 
     * @param departmentId 部門ID
     * @return 深度 (根部門為1)
     */
    public int calculateDepth(DepartmentId departmentId) {
        int depth = 1;
        DepartmentId currentId = departmentId;

        while (currentId != null) {
            Optional<Department> dept = departmentRepository.findById(currentId);
            if (dept.isPresent() && dept.get().getParentId() != null) {
                currentId = dept.get().getParentId();
                depth++;
            } else {
                break;
            }
        }

        return depth;
    }

    /**
     * 驗證移動部門是否會造成循環參照
     * 
     * @param departmentId 要移動的部門ID
     * @param newParentId  新的父部門ID
     * @return 是否會造成循環
     */
    public boolean wouldCauseCircularReference(DepartmentId departmentId, DepartmentId newParentId) {
        if (newParentId == null) {
            return false; // 移動到根層級不會造成循環
        }

        if (departmentId.equals(newParentId)) {
            return true; // 不能設定自己為父部門
        }

        // 檢查新父部門是否為目前部門的子部門
        Set<DepartmentId> descendants = getAllDescendants(departmentId);
        return descendants.contains(newParentId);
    }

    /**
     * 取得部門的所有子孫部門ID
     * 
     * @param departmentId 部門ID
     * @return 所有子孫部門ID集合
     */
    public Set<DepartmentId> getAllDescendants(DepartmentId departmentId) {
        Set<DepartmentId> descendants = new HashSet<>();
        Queue<DepartmentId> queue = new LinkedList<>();
        queue.add(departmentId);

        while (!queue.isEmpty()) {
            DepartmentId currentId = queue.poll();
            List<Department> children = departmentRepository.findByParentId(currentId);

            for (Department child : children) {
                if (!descendants.contains(child.getId())) {
                    descendants.add(child.getId());
                    queue.add(child.getId());
                }
            }
        }

        return descendants;
    }

    /**
     * 取得部門的所有祖先部門ID (從根到父)
     * 
     * @param departmentId 部門ID
     * @return 祖先部門ID列表
     */
    public List<DepartmentId> getAncestors(DepartmentId departmentId) {
        List<DepartmentId> ancestors = new ArrayList<>();
        DepartmentId currentId = departmentId;

        while (currentId != null) {
            Optional<Department> dept = departmentRepository.findById(currentId);
            if (dept.isPresent() && dept.get().getParentId() != null) {
                ancestors.add(0, dept.get().getParentId());
                currentId = dept.get().getParentId();
            } else {
                break;
            }
        }

        return ancestors;
    }

    /**
     * 取得部門的完整路徑名稱
     * 
     * @param departmentId 部門ID
     * @return 路徑名稱 (例如: "總公司 > 研發部 > 前端組")
     */
    public String getFullPath(DepartmentId departmentId) {
        List<String> pathNames = new ArrayList<>();

        DepartmentId currentId = departmentId;
        while (currentId != null) {
            Optional<Department> dept = departmentRepository.findById(currentId);
            if (dept.isPresent()) {
                pathNames.add(0, dept.get().getName());
                currentId = dept.get().getParentId();
            } else {
                break;
            }
        }

        return String.join(" > ", pathNames);
    }

    /**
     * 建立部門樹結構
     * 
     * @param organizationId 組織ID
     * @return 部門樹
     */
    public List<DepartmentTreeNode> buildDepartmentTree(String organizationId) {
        List<Department> allDepartments = departmentRepository.findByOrganizationId(organizationId);

        // 建立樹節點
        Map<String, DepartmentTreeNode> nodeMap = allDepartments.stream()
                .collect(Collectors.toMap(
                        d -> d.getId().getValue().toString(),
                        d -> new DepartmentTreeNode(d.getId().getValue().toString(), d.getName(), d.getCode(),
                                new ArrayList<>())));

        // 建立父子關係
        List<DepartmentTreeNode> rootNodes = new ArrayList<>();
        for (Department dept : allDepartments) {
            DepartmentTreeNode node = nodeMap.get(dept.getId().getValue().toString());
            if (dept.getParentId() == null) {
                rootNodes.add(node);
            } else {
                DepartmentTreeNode parentNode = nodeMap.get(dept.getParentId().getValue().toString());
                if (parentNode != null) {
                    parentNode.getChildren().add(node);
                }
            }
        }

        // 排序
        sortTreeNodes(rootNodes);

        return rootNodes;
    }

    private void sortTreeNodes(List<DepartmentTreeNode> nodes) {
        nodes.sort(Comparator.comparing(DepartmentTreeNode::getName));
        for (DepartmentTreeNode node : nodes) {
            if (!node.getChildren().isEmpty()) {
                sortTreeNodes(node.getChildren());
            }
        }
    }

    /**
     * 取得最大層級深度
     * 
     * @return 最大層級深度
     */
    public int getMaxHierarchyDepth() {
        return MAX_HIERARCHY_DEPTH;
    }

    /**
     * 部門樹節點
     */
    public static class DepartmentTreeNode {
        private final String id;
        private final String name;
        private final String code;
        private final List<DepartmentTreeNode> children;

        public DepartmentTreeNode(String id, String name, String code, List<DepartmentTreeNode> children) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.children = children;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        public List<DepartmentTreeNode> getChildren() {
            return children;
        }
    }
}
