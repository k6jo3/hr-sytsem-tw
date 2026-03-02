import { describe, it, expect } from 'vitest';
import { MenuFactory } from './MenuFactory';
import type { MenuItemConfig, SystemRole } from '../config/menuConfig';

/**
 * MenuFactory 單元測試
 * 驗證選單過濾、動態路由匹配、展開鍵計算
 */
describe('MenuFactory', () => {
  // ========== 角色過濾 ==========
  describe('createMenuItems', () => {
    const testConfig: MenuItemConfig[] = [
      { key: '/dashboard', icon: 'HomeOutlined', label: '首頁' },
      {
        key: 'admin-group',
        icon: 'LockOutlined',
        label: '管理',
        children: [
          { key: '/admin/users', label: '使用者管理', roles: ['ADMIN'] },
          { key: '/admin/employees', label: '員工管理', roles: ['ADMIN', 'HR'] },
        ],
      },
      { key: '/admin/reports', icon: 'BarChartOutlined', label: '報表', roles: ['ADMIN', 'HR'] },
    ];

    it('ADMIN 角色應看到全部選單項', () => {
      const items = MenuFactory.createMenuItems(testConfig, ['ADMIN']);
      expect(items).toHaveLength(3);
      // 子選單也應全部可見
      const adminGroup = items.find((i) => i!.key === 'admin-group') as any;
      expect(adminGroup.children).toHaveLength(2);
    });

    it('HR 角色應過濾掉僅限 ADMIN 的項目', () => {
      const items = MenuFactory.createMenuItems(testConfig, ['HR']);
      expect(items).toHaveLength(3);
      const adminGroup = items.find((i) => i!.key === 'admin-group') as any;
      // 只有 /admin/employees 對 HR 可見
      expect(adminGroup.children).toHaveLength(1);
      expect(adminGroup.children[0].key).toBe('/admin/employees');
    });

    it('EMPLOYEE 角色只能看到無角色限制的項目', () => {
      const items = MenuFactory.createMenuItems(testConfig, ['EMPLOYEE']);
      // /dashboard 無限制，admin-group 子項全被過濾 → 父項也隱藏，/admin/reports 限 ADMIN/HR → 隱藏
      expect(items).toHaveLength(1);
      expect(items[0]!.key).toBe('/dashboard');
    });

    it('子選單全被過濾時父選單也應隱藏', () => {
      const config: MenuItemConfig[] = [
        {
          key: 'group',
          icon: 'LockOutlined',
          label: '管理群組',
          children: [
            { key: '/admin/only', label: '僅管理員', roles: ['ADMIN'] },
          ],
        },
      ];
      const items = MenuFactory.createMenuItems(config, ['EMPLOYEE']);
      expect(items).toHaveLength(0);
    });

    it('無角色限制的項目所有人都能看到', () => {
      const config: MenuItemConfig[] = [
        { key: '/public', icon: 'HomeOutlined', label: '公開頁面' },
      ];
      const items = MenuFactory.createMenuItems(config, ['EMPLOYEE']);
      expect(items).toHaveLength(1);
    });

    it('回傳的項目應包含 icon 屬性（React 節點）', () => {
      const config: MenuItemConfig[] = [
        { key: '/dashboard', icon: 'HomeOutlined', label: '首頁' },
      ];
      const items = MenuFactory.createMenuItems(config, ['ADMIN']);
      expect(items[0]).toHaveProperty('icon');
      expect(items[0]).toHaveProperty('label', '首頁');
    });
  });

  // ========== 動態路由匹配 ==========
  describe('findSelectedKey', () => {
    const config: MenuItemConfig[] = [
      { key: '/dashboard', label: '首頁' },
      {
        key: 'org',
        label: '組織',
        children: [
          { key: '/admin/employees', label: '員工' },
        ],
      },
      { key: '/admin/projects', label: '專案' },
    ];

    it('完全匹配路徑', () => {
      expect(MenuFactory.findSelectedKey('/dashboard', config)).toBe('/dashboard');
    });

    it('動態路由前綴匹配：/admin/employees/123 → /admin/employees', () => {
      expect(MenuFactory.findSelectedKey('/admin/employees/123', config)).toBe('/admin/employees');
    });

    it('帶有更深層子路徑也能匹配：/admin/projects/edit/456 → /admin/projects', () => {
      expect(MenuFactory.findSelectedKey('/admin/projects/edit/456', config)).toBe('/admin/projects');
    });

    it('無匹配時回傳空字串', () => {
      expect(MenuFactory.findSelectedKey('/unknown', config)).toBe('');
    });
  });

  // ========== 自動展開 ==========
  describe('findOpenKeys', () => {
    const config: MenuItemConfig[] = [
      { key: '/dashboard', label: '首頁' },
      {
        key: 'org',
        label: '組織',
        children: [
          { key: '/admin/employees', label: '員工' },
        ],
      },
      {
        key: 'payroll',
        label: '薪資',
        children: [
          { key: '/admin/payroll/runs', label: '計薪' },
        ],
      },
    ];

    it('應展開含有選中項的子選單', () => {
      const keys = MenuFactory.findOpenKeys('/admin/employees', config);
      expect(keys).toContain('org');
    });

    it('動態路由也應展開正確的子選單', () => {
      const keys = MenuFactory.findOpenKeys('/admin/employees/789', config);
      expect(keys).toContain('org');
    });

    it('頂層項目不需展開任何子選單', () => {
      const keys = MenuFactory.findOpenKeys('/dashboard', config);
      expect(keys).toHaveLength(0);
    });

    it('無匹配時回傳空陣列', () => {
      const keys = MenuFactory.findOpenKeys('/unknown', config);
      expect(keys).toHaveLength(0);
    });
  });
});
