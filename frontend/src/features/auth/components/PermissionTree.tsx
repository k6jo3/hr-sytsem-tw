/**
 * Permission Tree Component (權限樹元件)
 * Domain Code: HR01
 */

import React, { useMemo } from 'react';
import { Tree, Empty } from 'antd';
import type { TreeProps, DataNode } from 'antd/es/tree';
import type { PermissionDto } from '../api/AuthTypes';

export interface PermissionTreeProps {
  permissions: PermissionDto[];
  checkedKeys: string[];
  disabled?: boolean;
  onCheck: (checkedKeys: string[]) => void;
}

/**
 * 權限樹元件
 */
export const PermissionTree: React.FC<PermissionTreeProps> = ({
  permissions,
  checkedKeys,
  disabled = false,
  onCheck,
}) => {
  // 轉換權限資料為樹節點
  const treeData = useMemo(() => {
    const convertToTreeNode = (permission: PermissionDto): DataNode => ({
      key: permission.id,
      title: permission.permission_name,
      children: permission.children?.map(convertToTreeNode),
      isLeaf: !permission.children || permission.children.length === 0,
    });

    // 依模組分組
    const groupedByModule = permissions.reduce((acc, permission) => {
      const module = permission.module || 'OTHER';
      if (!acc[module]) {
        acc[module] = [];
      }
      acc[module].push(permission);
      return acc;
    }, {} as Record<string, PermissionDto[]>);

    // 模組標籤對照
    const moduleLabels: Record<string, string> = {
      IAM: '身份認證管理',
      ORG: '組織員工管理',
      ATT: '考勤管理',
      PAY: '薪資管理',
      INS: '保險管理',
      PRJ: '專案管理',
      TMS: '工時管理',
      PFM: '績效管理',
      RCT: '招募管理',
      TRN: '訓練管理',
      WFL: '簽核流程',
      NTF: '通知服務',
      DOC: '文件管理',
      RPT: '報表分析',
      OTHER: '其他',
    };

    // 建立模組層級的樹結構
    return Object.entries(groupedByModule).map(([module, modulePermissions]) => ({
      key: `module-${module}`,
      title: moduleLabels[module] || module,
      children: modulePermissions.map(convertToTreeNode),
      selectable: false,
    }));
  }, [permissions]);

  // 處理勾選事件
  const handleCheck: TreeProps['onCheck'] = (checked) => {
    // 過濾掉模組節點 (key 以 module- 開頭)
    const permissionKeys = (Array.isArray(checked) ? checked : checked.checked)
      .filter((key) => !String(key).startsWith('module-'))
      .map(String);
    onCheck(permissionKeys);
  };

  if (permissions.length === 0) {
    return <Empty description="暫無權限資料" />;
  }

  return (
    <Tree
      checkable
      disabled={disabled}
      checkedKeys={checkedKeys}
      onCheck={handleCheck}
      treeData={treeData}
      defaultExpandAll
      style={{ background: '#fafafa', padding: 16, borderRadius: 4 }}
    />
  );
};
