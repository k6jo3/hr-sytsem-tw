/**
 * 系統選單宣告式配置
 * 集中管理所有側邊欄選單項目、路由、圖示與角色權限
 */

/** 系統角色類型 */
export type SystemRole = 'ADMIN' | 'HR' | 'FINANCE' | 'PM' | 'MANAGER' | 'EMPLOYEE';

/** 選單項目配置介面 */
export interface MenuItemConfig {
  /** 唯一 key，對應路由路徑 */
  key: string;
  /** 顯示標籤 */
  label: string;
  /** 圖示名稱（對應 ICON_MAP） */
  icon?: string;
  /** 可存取的角色清單，空陣列或 undefined 表示不限制 */
  roles?: SystemRole[];
  /** 子選單 */
  children?: MenuItemConfig[];
}

/**
 * 完整系統選單配置
 * 涵蓋 HR01-HR14 所有模組
 */
export const MENU_CONFIG: MenuItemConfig[] = [
  { key: '/dashboard', icon: 'HomeOutlined', label: '首頁儀表板' },
  {
    key: 'iam',
    icon: 'LockOutlined',
    label: '帳號與權限',
    children: [
      { key: '/admin/users', label: '使用者管理', roles: ['ADMIN'] },
      { key: '/admin/roles', label: '角色權限分配', roles: ['ADMIN'] },
      { key: '/admin/system', label: '系統管理', roles: ['ADMIN'] },
    ],
  },
  {
    key: 'org',
    icon: 'TeamOutlined',
    label: '組織與員工',
    children: [
      { key: '/admin/organization', label: '部門與編制', roles: ['ADMIN', 'HR'] },
      { key: '/admin/employees', label: '員工基本資料', roles: ['ADMIN', 'HR'] },
    ],
  },
  {
    key: 'attendance',
    icon: 'ClockCircleOutlined',
    label: '考勤管理',
    children: [
      { key: '/attendance/check-in', label: '每日打卡' },
      { key: '/attendance/leave/apply', label: '請假加班申請' },
      { key: '/attendance/my-records', label: '我的考勤日誌' },
      { key: '/attendance/overtime', label: '加班紀錄' },
      { key: '/attendance/leave/balance', label: '假別餘額查詢' },
      { key: '/admin/attendance/approvals', label: '考勤例外審核', roles: ['ADMIN', 'HR', 'MANAGER'] },
      { key: '/admin/attendance/shifts', label: '班別管理', roles: ['ADMIN', 'HR'] },
      { key: '/admin/attendance/leave-types', label: '假別管理', roles: ['ADMIN', 'HR'] },
      { key: '/admin/attendance/reports', label: '考勤報表', roles: ['ADMIN', 'HR'] },
      { key: '/admin/attendance/monthly-close', label: '月結作業', roles: ['ADMIN', 'HR'] },
    ],
  },
  {
    key: 'payroll',
    icon: 'DollarOutlined',
    label: '薪資核算',
    children: [
      { key: '/admin/payroll/runs', label: '計薪作業中心', roles: ['ADMIN', 'HR', 'FINANCE'] },
      { key: '/profile/payslips', label: '我的電子薪資單' },
      { key: '/admin/payroll/structures', label: '薪資結構設定', roles: ['ADMIN', 'HR', 'FINANCE'] },
      { key: '/admin/payroll/items', label: '薪資項目管理', roles: ['ADMIN', 'HR', 'FINANCE'] },
      { key: '/admin/payroll/approval', label: '薪資審核', roles: ['ADMIN', 'HR', 'FINANCE'] },
      { key: '/admin/payroll/bank-transfer', label: '銀行轉帳', roles: ['ADMIN', 'FINANCE'] },
      { key: '/admin/payroll/employees', label: '員工薪資歷史', roles: ['ADMIN', 'HR', 'FINANCE'] },
    ],
  },
  {
    key: 'insurance',
    icon: 'SafetyOutlined',
    label: '保險管理',
    children: [
      { key: '/admin/insurance/enrollments', label: '勞健保加退保', roles: ['ADMIN', 'HR'] },
      { key: '/admin/insurance/levels', label: '投保級距管理', roles: ['ADMIN', 'HR'] },
      { key: '/admin/insurance/calculator', label: '保費試算工具', roles: ['ADMIN', 'HR'] },
      { key: '/profile/insurance', label: '我的保險資料' },
    ],
  },
  {
    key: 'projects',
    icon: 'ProjectOutlined',
    label: '專案管理',
    children: [
      { key: '/admin/projects', label: '專案與客戶維護', roles: ['ADMIN', 'PM'] },
      { key: '/admin/projects/customers', label: '合作客戶管理', roles: ['ADMIN', 'PM'] },
    ],
  },
  {
    key: 'timesheet',
    icon: 'FieldTimeOutlined',
    label: '工時申報',
    children: [
      { key: '/profile/timesheets', label: '每週工時報表' },
      { key: '/admin/timesheets/approval', label: '工時審核看板', roles: ['ADMIN', 'PM', 'MANAGER'] },
      { key: '/admin/timesheets/reports', label: '工時統計報表', roles: ['ADMIN', 'PM', 'MANAGER'] },
    ],
  },
  {
    key: 'performance',
    icon: 'TrophyOutlined',
    label: '績效考核',
    children: [
      { key: '/admin/performance/cycles', label: '考核週期管理', roles: ['ADMIN', 'HR'] },
      { key: '/profile/performance', label: '我的評核表' },
      { key: '/admin/performance/team', label: '團隊績效總覽', roles: ['ADMIN', 'HR', 'MANAGER'] },
      { key: '/admin/performance/reports', label: '績效報表', roles: ['ADMIN', 'HR'] },
    ],
  },
  { key: '/admin/recruitment', icon: 'UserAddOutlined', label: '招募管理', roles: ['ADMIN', 'HR'] },
  { key: '/admin/training', icon: 'BookOutlined', label: '教育訓練' },
  {
    key: 'workflow',
    icon: 'AuditOutlined',
    label: '簽核流程',
    children: [
      { key: '/admin/workflow', label: '我的待辦與申請' },
      { key: '/admin/workflow/definitions', label: '流程定義管理', roles: ['ADMIN', 'HR'] },
      { key: '/profile/delegation', label: '代理人設定' },
    ],
  },
  {
    key: 'notification',
    icon: 'BellOutlined',
    label: '訊息通知',
    children: [
      { key: '/profile/notifications', label: '我的通知' },
      { key: '/profile/notification-settings', label: '通知偏好設定' },
      { key: '/admin/notifications/templates', label: '通知範本管理', roles: ['ADMIN', 'HR'] },
      { key: '/admin/notifications/announcements', label: '公告管理', roles: ['ADMIN', 'HR'] },
    ],
  },
  {
    key: 'document',
    icon: 'FileOutlined',
    label: '文件管理',
    children: [
      { key: '/profile/documents', label: '我的文件' },
      { key: '/admin/documents', label: '文件總管', roles: ['ADMIN', 'HR'] },
      { key: '/admin/documents/templates', label: '範本管理', roles: ['ADMIN', 'HR'] },
    ],
  },
  {
    key: 'reports',
    icon: 'BarChartOutlined',
    label: '報表中心',
    children: [
      { key: '/admin/reports', label: '總覽儀表板', roles: ['ADMIN', 'HR', 'FINANCE', 'PM'] },
      { key: '/admin/reports/hr', label: '人力資源報表', roles: ['ADMIN', 'HR'] },
      { key: '/admin/reports/project', label: '專案管理報表', roles: ['ADMIN', 'PM'] },
      { key: '/admin/reports/finance', label: '財務報表', roles: ['ADMIN', 'FINANCE'] },
    ],
  },
];
