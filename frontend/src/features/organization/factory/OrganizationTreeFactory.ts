/**
 * Organization Tree Data Converter
 * Domain Code: HR02
 * Converts API DTO to Ant Design Tree Data format
 */

import { DataNode as AntDataNode } from 'antd/es/tree';
import { DepartmentDto, OrganizationDto } from '../api/OrganizationTypes';

interface DataNode extends AntDataNode {
  data?: DepartmentDto;
}

export class OrganizationTreeFactory {
  /**
   * Check if the node is a department
   */
  static isDepartment(node: any): node is DepartmentDto {
    return 'departmentCode' in node || 'code' in node;
  }

  /**
   * Convert Organization DTO to Tree Data with Employee Counts
   */
  static createTreeData(
    org: OrganizationDto, 
    departments: DepartmentDto[]
  ): DataNode[] {
    const orgNode: DataNode = {
      key: `org-${org.organizationId}`,
      title: `${org.organizationName} (${org.employeeCount || 0}人)`,
      icon: '🏢', // We can use React Node here in component, but string for simplicity in model
      children: this.buildDepartmentTree(departments),
      isLeaf: false,
    };
    
    return [orgNode];
  }

  /**
   * Recursively build department tree
   */
  private static buildDepartmentTree(departments: DepartmentDto[]): DataNode[] {
    // 1. Find root departments (parentId is null or empty)
    const rootDepartments = departments.filter(d => !d.parentId);
    
    // 2. Build map for quick lookup of children
    const childrenMap = new Map<string, DepartmentDto[]>();
    departments.forEach(d => {
      if (d.parentId) {
        const children = childrenMap.get(d.parentId) || [];
        children.push(d);
        childrenMap.set(d.parentId, children);
      }
    });

    // 3. Recursive function to build nodes
    const mapDepartmentToNode = (dept: DepartmentDto): DataNode => {
      // API might return subDepartments nested, or flat list. Handle both.
      // If flat list strategy (using childrenMap):
      const childrenFromMap = childrenMap.get(dept.departmentId) || [];
      // If nested strategy (dept.subDepartments):
      const childrenFromProp = dept.subDepartments || [];
      
      const allChildren = [...childrenFromMap, ...childrenFromProp];
      
      // Deduplicate if needed (though API should be consistent)
      const uniqueChildren = Array.from(new Map(allChildren.map(c => [c.departmentId, c])).values());

      const employeeCount = dept.employeeCount || 0;

      return {
        key: dept.departmentId,
        title: `${dept.name} (${employeeCount}人) ${dept.managerName ? `- ${dept.managerName}` : ''}`,
        // Additional data for node selection
        data: dept, 
        children: uniqueChildren.length > 0 ? uniqueChildren.map(mapDepartmentToNode) : undefined,
        isLeaf: uniqueChildren.length === 0,
      };
    };

    return rootDepartments.map(mapDepartmentToNode);
  }
}
