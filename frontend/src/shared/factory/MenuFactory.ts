import React from 'react';
import {
  AuditOutlined,
  BarChartOutlined,
  BellOutlined,
  BookOutlined,
  ClockCircleOutlined,
  DollarOutlined,
  FieldTimeOutlined,
  FileOutlined,
  HomeOutlined,
  LockOutlined,
  ProjectOutlined,
  SafetyOutlined,
  TeamOutlined,
  TrophyOutlined,
  UserAddOutlined,
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import type { MenuItemConfig, SystemRole } from '../config/menuConfig';

type MenuItem = NonNullable<MenuProps['items']>[number];

/** 圖示名稱 → React 元件映射 */
const ICON_MAP: Record<string, React.ComponentType> = {
  HomeOutlined,
  LockOutlined,
  TeamOutlined,
  ClockCircleOutlined,
  DollarOutlined,
  SafetyOutlined,
  ProjectOutlined,
  FieldTimeOutlined,
  TrophyOutlined,
  UserAddOutlined,
  BookOutlined,
  AuditOutlined,
  BellOutlined,
  FileOutlined,
  BarChartOutlined,
};

/**
 * 選單工廠
 * 負責將宣告式選單配置轉為 Ant Design Menu 所需的格式，
 * 並提供角色過濾、動態路由匹配與自動展開功能。
 */
export class MenuFactory {
  /**
   * 根據使用者角色過濾選單配置，並轉換為 Ant Design MenuProps['items']
   */
  static createMenuItems(config: MenuItemConfig[], userRoles: string[]): MenuItem[] {
    return config
      .map((item) => this.transformItem(item, userRoles))
      .filter((item): item is MenuItem => item !== null);
  }

  /**
   * 根據當前路徑找到對應的選中 key
   * 支援動態路由前綴匹配（例如 /admin/employees/123 → /admin/employees）
   */
  static findSelectedKey(pathname: string, config: MenuItemConfig[]): string {
    const allKeys = this.collectLeafKeys(config);

    // 先嘗試完全匹配
    if (allKeys.includes(pathname)) {
      return pathname;
    }

    // 前綴匹配：取最長匹配的 key
    const matched = allKeys
      .filter((key) => pathname.startsWith(key + '/'))
      .sort((a, b) => b.length - a.length);

    return matched[0] ?? '';
  }

  /**
   * 根據當前路徑計算需要展開的子選單 keys
   */
  static findOpenKeys(pathname: string, config: MenuItemConfig[]): string[] {
    const selectedKey = this.findSelectedKey(pathname, config);
    if (!selectedKey) return [];

    const openKeys: string[] = [];
    for (const item of config) {
      if (item.children) {
        const hasSelected = item.children.some(
          (child) => child.key === selectedKey || pathname.startsWith(child.key + '/')
        );
        if (hasSelected) {
          openKeys.push(item.key);
        }
      }
    }
    return openKeys;
  }

  /** 將單一設定項轉為 Ant Design MenuItem，不符合角色時回傳 null */
  private static transformItem(item: MenuItemConfig, userRoles: string[]): MenuItem | null {
    // 有子選單的情況
    if (item.children) {
      const filteredChildren = item.children
        .map((child) => this.transformItem(child, userRoles))
        .filter((child): child is MenuItem => child !== null);

      // 子選單全被過濾 → 父項也隱藏
      if (filteredChildren.length === 0) return null;

      return {
        key: item.key,
        icon: this.resolveIcon(item.icon),
        label: item.label,
        children: filteredChildren,
      } as MenuItem;
    }

    // 葉節點：檢查角色權限
    if (!this.hasAccess(item.roles, userRoles)) return null;

    return {
      key: item.key,
      icon: this.resolveIcon(item.icon),
      label: item.label,
    } as MenuItem;
  }

  /** 檢查使用者是否有存取權限 */
  private static hasAccess(requiredRoles: SystemRole[] | undefined, userRoles: string[]): boolean {
    if (!requiredRoles || requiredRoles.length === 0) return true;
    return requiredRoles.some((role) => userRoles.includes(role));
  }

  /** 將圖示名稱字串轉為 React 元素 */
  private static resolveIcon(iconName?: string): React.ReactNode {
    if (!iconName) return undefined;
    const IconComponent = ICON_MAP[iconName];
    if (!IconComponent) return undefined;
    return React.createElement(IconComponent);
  }

  /** 收集所有葉節點 key（即有對應路由的 key） */
  private static collectLeafKeys(config: MenuItemConfig[]): string[] {
    const keys: string[] = [];
    for (const item of config) {
      if (item.children) {
        keys.push(...this.collectLeafKeys(item.children));
      } else {
        keys.push(item.key);
      }
    }
    return keys;
  }
}
