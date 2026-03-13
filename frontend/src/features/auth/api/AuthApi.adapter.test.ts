// @ts-nocheck
/**
 * HR01 IAM 模組 Adapter 測試
 *
 * 三層驗證：
 *  1. 後端 camelCase DTO → 前端 snake_case DTO 欄位對映正確
 *  2. null / undefined 防護不拋錯
 *  3. 未知 enum 值觸發 guardEnum 警告並使用原始值
 *
 * 依據：
 *  - backend Response DTOs (LoginResponse, UserDetailResponse, UserListResponse,
 *    AssignUserRolesResponse, BatchDeactivateUsersResponse, RoleListResponse,
 *    RoleDetailResponse, CreateRoleResponse, SystemParameterResponse,
 *    FeatureToggleResponse, ScheduledJobConfigResponse)
 *  - contracts/iam_contracts.md requiredFields
 *  - frontend adapters (AuthApi.ts, UserApi.ts, RoleApi.ts, SystemApi.ts)
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';

// ─────────────────────────────────────────────
// vi.hoisted：在 vi.mock factory 執行前建立共享 mock 物件
// ─────────────────────────────────────────────
const { mockedApiClient } = vi.hoisted(() => {
  const mockedApiClient = {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    patch: vi.fn(),
  };
  return { mockedApiClient };
});

// ─────────────────────────────────────────────
// 模組 Mock：讓 MockConfig 預設關閉，讓 apiClient 不實際發請求
// ─────────────────────────────────────────────
vi.mock('../../../config/MockConfig', () => ({
  MockConfig: { isEnabled: () => false },
}));

vi.mock('@shared/api', () => ({
  apiClient: mockedApiClient,
}));

// SystemApi.ts 使用相對路徑 import，需同步 mock
vi.mock('../../../shared/api/apiClient', () => ({
  apiClient: mockedApiClient,
}));

vi.mock('./MockAuthApi', () => ({
  MockAuthApi: {},
}));

// 被測試的 adapter 函式透過實際模組載入（確保走真實程式碼路徑）
import { AuthApi } from './AuthApi';
import { UserApi } from './UserApi';
import { RoleApi } from './RoleApi';
import { SystemApi, type SystemParameterDto, type FeatureToggleDto, type ScheduledJobConfigDto } from './SystemApi';

// ─────────────────────────────────────────────
// 工廠：產生標準後端 LoginResponse payload
// ─────────────────────────────────────────────
function buildBackendLoginResponse(overrides: Record<string, unknown> = {}) {
  return {
    accessToken: 'eyJhbGciOiJIUzI1NiJ9.access',
    refreshToken: 'eyJhbGciOiJIUzI1NiJ9.refresh',
    tokenType: 'Bearer',
    expiresIn: 900,
    user: {
      userId: 'user-001',
      username: 'john.doe@company.com',
      displayName: 'John Doe',
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@company.com',
      employeeId: 'emp-001',
      roles: ['SYSTEM_ADMIN', 'HR_ADMIN'],
    },
    ...overrides,
  };
}

// 工廠：產生標準後端 UserListResponse / UserDetailResponse payload（共用欄位）
function buildBackendUserItem(overrides: Record<string, unknown> = {}) {
  return {
    userId: 'user-002',
    username: 'jane.doe@company.com',
    email: 'jane.doe@company.com',
    displayName: 'Jane Doe',
    firstName: 'Jane',
    lastName: 'Doe',
    employeeId: 'emp-002',
    tenantId: 'T001',
    status: 'ACTIVE',
    roles: ['EMPLOYEE'],
    lastLoginAt: '2026-01-15T09:30:00',
    createdAt: '2025-06-01T00:00:00',
    updatedAt: '2026-01-15T09:30:00',
    ...overrides,
  };
}

// 工廠：後端 UserDetailResponse 額外欄位
function buildBackendUserDetail(overrides: Record<string, unknown> = {}) {
  return {
    ...buildBackendUserItem(),
    roleDetails: [
      { roleId: 'role-001', roleName: 'Employee', displayName: '員工' },
    ],
    failedLoginAttempts: 0,
    lastLoginIp: '192.168.1.1',
    passwordChangedAt: '2025-06-01T00:00:00',
    mustChangePassword: false,
    ...overrides,
  };
}

// 工廠：後端 RoleListResponse / RoleDetailResponse
function buildBackendRoleItem(overrides: Record<string, unknown> = {}) {
  return {
    roleId: 'role-001',
    roleName: '系統管理員',
    roleCode: 'SYSTEM_ADMIN',
    description: '擁有所有權限',
    isSystemRole: true,
    status: 'ACTIVE',
    permissionCount: 5,
    permissions: ['user:read', 'user:write'],
    createdAt: '2025-01-01T00:00:00',
    ...overrides,
  };
}

function buildBackendRoleDetail(overrides: Record<string, unknown> = {}) {
  return {
    ...buildBackendRoleItem(),
    tenantId: 'T001',
    userCount: 3,
    permissionDetails: [
      { permissionId: 'perm-001', permissionCode: 'user:read', permissionName: '使用者讀取' },
    ],
    updatedAt: '2026-01-01T00:00:00',
    ...overrides,
  };
}

// 工廠：後端 SystemParameterResponse
function buildBackendSystemParameter(overrides: Record<string, unknown> = {}): Record<string, unknown> {
  return {
    paramCode: 'MAX_FAILED_LOGIN_ATTEMPTS',
    paramName: '登入失敗上限',
    paramValue: '5',
    paramType: 'INTEGER',
    module: 'HR01',
    category: 'SECURITY',
    description: '最大登入失敗次數',
    defaultValue: '5',
    isEncrypted: false,
    updatedAt: '2026-01-01T00:00:00',
    updatedBy: 'admin',
    ...overrides,
  };
}

// 工廠：後端 FeatureToggleResponse
function buildBackendFeatureToggle(overrides: Record<string, unknown> = {}): Record<string, unknown> {
  return {
    featureCode: 'LATE_CHECK',
    featureName: '遲到判定',
    module: 'HR03',
    enabled: true,
    description: '啟用遲到自動判定',
    updatedAt: '2026-01-01T00:00:00',
    updatedBy: 'admin',
    ...overrides,
  };
}

// 工廠：後端 ScheduledJobConfigResponse
function buildBackendScheduledJob(overrides: Record<string, unknown> = {}): Record<string, unknown> {
  return {
    jobCode: 'ABSENT_DETECTION',
    jobName: '曠職自動判定',
    module: 'HR03',
    cronExpression: '0 0 19 * * ?',
    enabled: true,
    description: '每日 19:00 掃描',
    lastExecutedAt: '2026-01-14T19:00:00',
    lastExecutionStatus: 'SUCCESS',
    lastErrorMessage: null,
    consecutiveFailures: 0,
    updatedAt: '2026-01-01T00:00:00',
    updatedBy: 'admin',
    ...overrides,
  };
}

// ════════════════════════════════════════════════════════════════
// AuthApi.ts — adaptLoginResponse / adaptUserDto
// ════════════════════════════════════════════════════════════════

describe('AuthApi — adaptLoginResponse', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確將後端 camelCase 欄位對映至前端 snake_case LoginResponse', async () => {
    const raw = buildBackendLoginResponse();
    mockedApiClient.post.mockResolvedValueOnce(raw);

    const result = await AuthApi.login({ username: 'john', password: 'pass' });

    // 合約要求欄位：accessToken → access_token, refreshToken → refresh_token, expiresIn → expires_in
    expect(result.access_token).toBe('eyJhbGciOiJIUzI1NiJ9.access');
    expect(result.refresh_token).toBe('eyJhbGciOiJIUzI1NiJ9.refresh');
    expect(result.expires_in).toBe(900);
    expect(result.user).toBeDefined();
  });

  it('應正確對映 user 物件至 UserDto（合約 AUTH_CMD_001 requiredFields）', async () => {
    const raw = buildBackendLoginResponse();
    mockedApiClient.post.mockResolvedValueOnce(raw);

    const result = await AuthApi.login({ username: 'john', password: 'pass' });
    const user = result.user;

    // 後端 userId → 前端 id
    expect(user.id).toBe('user-001');
    expect(user.username).toBe('john.doe@company.com');
    expect(user.email).toBe('john.doe@company.com');
    // 後端 displayName → 前端 display_name
    expect(user.display_name).toBe('John Doe');
    // 後端 firstName → 前端 first_name
    expect(user.first_name).toBe('John');
    // 後端 lastName → 前端 last_name
    expect(user.last_name).toBe('Doe');
    // 後端 employeeId → 前端 employee_id
    expect(user.employee_id).toBe('emp-001');
  });

  it('角色應透過 ROLE_CODE_MAP 轉換：SYSTEM_ADMIN → ADMIN, HR_ADMIN → HR', async () => {
    const raw = buildBackendLoginResponse();
    mockedApiClient.post.mockResolvedValueOnce(raw);

    const result = await AuthApi.login({ username: 'john', password: 'pass' });

    expect(result.user.role_list).toContain('ADMIN');
    expect(result.user.role_list).toContain('HR');
  });

  it('當 user 物件為 undefined 時應回傳帶有預設值的 UserDto（不拋錯）', async () => {
    const raw = buildBackendLoginResponse({ user: undefined });
    mockedApiClient.post.mockResolvedValueOnce(raw);

    const result = await AuthApi.login({ username: 'john', password: 'pass' });

    expect(result.user).toBeDefined();
    expect(result.user.id).toBeUndefined();
    expect(result.user.username).toBe('');
    expect(result.user.email).toBe('');
    expect(result.user.role_list).toEqual([]);
  });

  it('當 roles 為空陣列時 role_list 應為空陣列', async () => {
    const raw = buildBackendLoginResponse({ user: { ...buildBackendLoginResponse().user, roles: [] } });
    mockedApiClient.post.mockResolvedValueOnce(raw);

    const result = await AuthApi.login({ username: 'john', password: 'pass' });
    expect(result.user.role_list).toEqual([]);
  });
});

describe('AuthApi — adaptUserDto (status guardEnum)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('有效 status ACTIVE 不應觸發 console.warn', async () => {
    const warnSpy = vi.spyOn(console, 'warn');
    const raw = buildBackendLoginResponse({
      user: { ...buildBackendLoginResponse().user, status: 'ACTIVE' },
    });
    mockedApiClient.post.mockResolvedValueOnce(raw);

    const result = await AuthApi.login({ username: 'john', password: 'pass' });

    expect(result.user.status).toBe('ACTIVE');
    // guardEnum 不應觸發警告
    expect(warnSpy).not.toHaveBeenCalledWith(expect.stringContaining('欄位 "user.status"'));
    warnSpy.mockRestore();
  });

  it('未知 status "SUSPENDED" 應觸發 console.warn 並使用原始值', async () => {
    const warnSpy = vi.spyOn(console, 'warn');
    const raw = buildBackendLoginResponse({
      user: { ...buildBackendLoginResponse().user, status: 'SUSPENDED' },
    });
    mockedApiClient.post.mockResolvedValueOnce(raw);

    const result = await AuthApi.login({ username: 'john', password: 'pass' });

    expect(warnSpy).toHaveBeenCalledWith(
      expect.stringContaining('user.status')
    );
    // guardEnum 保留原始值
    expect(result.user.status).toBe('SUSPENDED');
    warnSpy.mockRestore();
  });

  it('status 為 null 時應回傳 fallback "ACTIVE"', async () => {
    const raw = buildBackendLoginResponse({
      user: { ...buildBackendLoginResponse().user, status: null },
    });
    mockedApiClient.post.mockResolvedValueOnce(raw);

    const result = await AuthApi.login({ username: 'john', password: 'pass' });
    expect(result.user.status).toBe('ACTIVE');
  });

  it('status 為 undefined 時應回傳 fallback "ACTIVE"', async () => {
    const raw = buildBackendLoginResponse({
      user: { ...buildBackendLoginResponse().user, status: undefined },
    });
    mockedApiClient.post.mockResolvedValueOnce(raw);

    const result = await AuthApi.login({ username: 'john', password: 'pass' });
    expect(result.user.status).toBe('ACTIVE');
  });

  it('有效 status LOCKED 應正確對映', async () => {
    const raw = buildBackendLoginResponse({
      user: { ...buildBackendLoginResponse().user, status: 'LOCKED' },
    });
    mockedApiClient.post.mockResolvedValueOnce(raw);

    const result = await AuthApi.login({ username: 'john', password: 'pass' });
    expect(result.user.status).toBe('LOCKED');
  });
});

describe('AuthApi — refreshToken', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確對映 RefreshTokenResponse（合約 AUTH_CMD_003 requiredFields）', async () => {
    // RefreshTokenResponse 只有 accessToken, tokenType, expiresIn（無 user）
    // refreshToken adapter 複用 adaptLoginResponse，需能處理 user 缺失
    const raw = {
      accessToken: 'new-access-token',
      tokenType: 'Bearer',
      expiresIn: 900,
    };
    mockedApiClient.post.mockResolvedValueOnce(raw);

    const result = await AuthApi.refreshToken('old-refresh-token');

    expect(result.access_token).toBe('new-access-token');
    expect(result.expires_in).toBe(900);
    // user 物件應可以被建立（不拋錯）
    expect(result.user).toBeDefined();
  });
});

describe('AuthApi — getCurrentUser', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確對映 /auth/me 回應（合約 IAM_QRY_301 user 欄位）', async () => {
    const raw = buildBackendUserDetail();
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await AuthApi.getCurrentUser();

    expect(result.id).toBe('user-002');
    expect(result.username).toBe('jane.doe@company.com');
    expect(result.display_name).toBe('Jane Doe');
    expect(result.must_change_password).toBe(false);
    expect(result.last_login_at).toBe('2026-01-15T09:30:00');
    expect(result.password_changed_at).toBe('2025-06-01T00:00:00');
  });
});

// ════════════════════════════════════════════════════════════════
// UserApi.ts — adaptUserItem / adaptGetUsersResponse
// ════════════════════════════════════════════════════════════════

describe('UserApi — adaptUserItem', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確對映所有必要欄位（合約 IAM_QRY_001 + IAM_QRY_002 requiredFields）', async () => {
    const raw = buildBackendUserDetail();
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await UserApi.getUser('user-002');

    // userId → id
    expect(result.id).toBe('user-002');
    // username
    expect(result.username).toBe('jane.doe@company.com');
    // email
    expect(result.email).toBe('jane.doe@company.com');
    // displayName → display_name
    expect(result.display_name).toBe('Jane Doe');
    // firstName → first_name
    expect(result.first_name).toBe('Jane');
    // lastName → last_name
    expect(result.last_name).toBe('Doe');
    // employeeId → employee_id
    expect(result.employee_id).toBe('emp-002');
    // tenantId → tenant_id
    expect(result.tenant_id).toBe('T001');
    // status
    expect(result.status).toBe('ACTIVE');
    // roles → role_list（EMPLOYEE → EMPLOYEE，無對映）
    expect(result.role_list).toContain('EMPLOYEE');
    // lastLoginAt → last_login_at
    expect(result.last_login_at).toBe('2026-01-15T09:30:00');
    // createdAt → created_at
    expect(result.created_at).toBe('2025-06-01T00:00:00');
    // updatedAt → updated_at
    expect(result.updated_at).toBe('2026-01-15T09:30:00');
    // mustChangePassword → must_change_password
    expect(result.must_change_password).toBe(false);
  });

  it('userId 為 null 時應回傳 id = undefined（不拋錯）', async () => {
    const raw = buildBackendUserItem({ userId: null });
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await UserApi.getUser('user-002');
    expect(result.id).toBeUndefined();
  });

  it('roles 為 null 時 role_list 應回傳 []', async () => {
    const raw = buildBackendUserItem({ roles: null });
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await UserApi.getUser('user-002');
    expect(result.role_list).toEqual([]);
  });

  it('roles 包含未知代碼時應保留原始字串（無對映 fallback）', async () => {
    const raw = buildBackendUserItem({ roles: ['UNKNOWN_ROLE'] });
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await UserApi.getUser('user-002');
    expect(result.role_list).toContain('UNKNOWN_ROLE');
  });

  it('displayName 使用 display_name fallback 時應正確對映', async () => {
    const raw = buildBackendUserItem({ displayName: undefined, display_name: 'Fallback Name' });
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await UserApi.getUser('user-002');
    expect(result.display_name).toBe('Fallback Name');
  });

  it('roleIds 欄位應正確對映至 role_ids（後端 AssignUserRolesResponse 確認欄位）', async () => {
    const raw = buildBackendUserItem({ roleIds: ['role-001', 'role-002'] });
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await UserApi.getUser('user-002');
    expect(result.role_ids).toEqual(['role-001', 'role-002']);
  });
});

describe('UserApi — adaptGetUsersResponse', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確解析 items 格式回應（合約 dataPath: items）', async () => {
    const raw = {
      items: [buildBackendUserItem(), buildBackendUserItem({ userId: 'user-003' })],
      page: 1,
      size: 20,
      total: 2,
      totalPages: 1,
    };
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await UserApi.getUsers({ status: 'ACTIVE' });

    expect(result.content).toHaveLength(2);
    expect(result.content[0].id).toBe('user-002');
    expect(result.pagination.page).toBe(1);
    expect(result.pagination.page_size).toBe(20);
    expect(result.pagination.total).toBe(2);
    expect(result.pagination.total_pages).toBe(1);
  });

  it('應正確解析 Spring Page (content) 格式回應', async () => {
    const raw = {
      content: [buildBackendUserItem()],
      number: 0,
      size: 10,
      totalElements: 1,
      totalPages: 1,
    };
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await UserApi.getUsers({});

    expect(result.content).toHaveLength(1);
    // Spring Page: number → page
    expect(result.pagination.page).toBe(0);
    expect(result.pagination.page_size).toBe(10);
    expect(result.pagination.total).toBe(1);
  });

  it('items 為空陣列時 content 應為 []', async () => {
    const raw = { items: [], page: 1, size: 20, total: 0, totalPages: 0 };
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await UserApi.getUsers({});
    expect(result.content).toEqual([]);
  });
});

describe('UserApi — adaptUpdateRequest（roles 欄位映射確認）', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('updateUser 應將 role_ids 轉換為後端 roles 欄位（UpdateRequest 映射）', async () => {
    const backendRaw = buildBackendUserItem();
    mockedApiClient.put.mockResolvedValueOnce(backendRaw);

    await UserApi.updateUser('user-002', {
      email: 'new@example.com',
      first_name: 'Jane',
      last_name: 'Doe',
      role_ids: ['role-001', 'role-002'],
    });

    const sentBody = mockedApiClient.put.mock.calls[0][1] as Record<string, unknown>;
    // 後端欄位名稱為 roles（非 roleIds）
    expect(sentBody.roles).toEqual(['role-001', 'role-002']);
  });

  it('createUser 應將 role_ids 轉換為後端 roleIds 欄位', async () => {
    const backendRaw = buildBackendUserItem();
    mockedApiClient.post.mockResolvedValueOnce(backendRaw);

    await UserApi.createUser({
      username: 'test@example.com',
      email: 'test@example.com',
      password: 'Secure123',
      display_name: 'Test User',
      first_name: 'Test',
      last_name: 'User',
      role_ids: ['role-001'],
    });

    const sentBody = mockedApiClient.post.mock.calls[0][1] as Record<string, unknown>;
    // 後端欄位名稱為 roleIds（CreateRequest 映射）
    expect(sentBody.roleIds).toEqual(['role-001']);
    expect(sentBody.displayName).toBe('Test User');
    expect(sentBody.firstName).toBe('Test');
    expect(sentBody.lastName).toBe('User');
  });
});

// ════════════════════════════════════════════════════════════════
// RoleApi.ts — adaptRoleItem / adaptGetRolesResponse / adaptPermissionItem
// ════════════════════════════════════════════════════════════════

describe('RoleApi — adaptRoleItem', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確對映 RoleDetailResponse 所有欄位（合約 IAM_QRY_105 requiredFields）', async () => {
    const raw = buildBackendRoleDetail();
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await RoleApi.getRole('role-001');

    // roleId → id
    expect(result.id).toBe('role-001');
    // roleName → role_name
    expect(result.role_name).toBe('系統管理員');
    // roleCode → role_code
    expect(result.role_code).toBe('SYSTEM_ADMIN');
    // description
    expect(result.description).toBe('擁有所有權限');
    // isSystemRole → is_system
    expect(result.is_system).toBe(true);
    // status === 'ACTIVE' → is_active = true
    expect(result.is_active).toBe(true);
    // permissions → permission_ids
    expect(result.permission_ids).toContain('user:read');
    // userCount → user_count
    expect(result.user_count).toBe(3);
    // createdAt → created_at
    expect(result.created_at).toBe('2025-01-01T00:00:00');
    // updatedAt → updated_at
    expect(result.updated_at).toBe('2026-01-01T00:00:00');
  });

  it('status 為 INACTIVE 時 is_active 應為 false', async () => {
    const raw = buildBackendRoleDetail({ status: 'INACTIVE' });
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await RoleApi.getRole('role-001');
    expect(result.is_active).toBe(false);
  });

  it('isSystemRole 為 false 時 is_system 應為 false', async () => {
    const raw = buildBackendRoleDetail({ isSystemRole: false });
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await RoleApi.getRole('role-001');
    expect(result.is_system).toBe(false);
  });

  it('roleId 為 null 時 id 應為 undefined（不拋錯）', async () => {
    const raw = buildBackendRoleDetail({ roleId: null });
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await RoleApi.getRole('role-001');
    expect(result.id).toBeUndefined();
  });

  it('permissions 為 null 時 permission_ids 應為 []', async () => {
    const raw = buildBackendRoleDetail({ permissions: null });
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await RoleApi.getRole('role-001');
    expect(result.permission_ids).toEqual([]);
  });
});

describe('RoleApi — adaptGetRolesResponse', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('後端直接回傳陣列時應正確處理（合約 IAM_QRY_101 dataPath: ""）', async () => {
    const raw = [buildBackendRoleItem(), buildBackendRoleItem({ roleId: 'role-002', roleCode: 'HR_ADMIN' })];
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await RoleApi.getRoles({});

    expect(result.roles).toHaveLength(2);
    expect(result.roles[0].id).toBe('role-001');
    expect(result.roles[1].role_code).toBe('HR_ADMIN');
    // 陣列格式時分頁應以長度為 total
    expect(result.pagination.total).toBe(2);
    expect(result.pagination.total_pages).toBe(1);
  });

  it('後端回傳分頁物件時應正確解析', async () => {
    const raw = {
      items: [buildBackendRoleItem()],
      page: 1,
      size: 10,
      total: 5,
      totalPages: 1,
    };
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await RoleApi.getRoles({ page: 1, page_size: 10 });

    expect(result.roles).toHaveLength(1);
    expect(result.pagination.total).toBe(5);
  });

  it('getAllRoles 回傳陣列時應回傳所有角色（不分頁）', async () => {
    const raw = [buildBackendRoleItem()];
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await RoleApi.getAllRoles();
    expect(Array.isArray(result)).toBe(true);
    expect(result[0].id).toBe('role-001');
  });
});

describe('RoleApi — adaptPermissionItem', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確對映 PermissionListResponse 欄位（合約 IAM_QRY_201 requiredFields）', async () => {
    // PermissionListResponse: permissionId, permissionCode, permissionName, resource, action, description
    const raw = [
      {
        permissionId: 'perm-001',
        permissionCode: 'user:read',
        permissionName: '使用者讀取',
        resource: 'user',
        action: 'read',
        description: '讀取使用者資訊',
      },
    ];
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await RoleApi.getPermissions();

    const perm = result.permissions[0];
    // permissionId → id
    expect(perm.id).toBe('perm-001');
    // permissionCode → permission_code
    expect(perm.permission_code).toBe('user:read');
    // permissionName → permission_name
    expect(perm.permission_name).toBe('使用者讀取');
    // description
    expect(perm.description).toBe('讀取使用者資訊');
    // resource → module
    expect(perm.module).toBe('user');
  });

  it('應正確對映 PermissionTreeResponse 欄位（合約 IAM_QRY_202 requiredFields）', async () => {
    // PermissionTreeResponse: resource, resourceDisplayName, permissions (含 permissionId, permissionCode, action, description)
    const raw = [
      {
        resource: 'user',
        resourceDisplayName: '使用者管理',
        permissions: [
          {
            permissionId: 'perm-001',
            permissionCode: 'user:read',
            action: 'read',
            description: '讀取使用者',
          },
        ],
      },
    ];
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await RoleApi.getPermissions();

    // 樹狀格式：第一層是 resource 節點，adapter 以 PermissionTreeResponse 作為根項目
    const rootItem = result.permissions[0];
    expect(rootItem.module).toBe('user'); // resource → module
  });

  it('permissionId 為 null 時 id 應為 undefined（不拋錯）', async () => {
    const raw = [{ permissionId: null, permissionCode: 'user:read', permissionName: '讀取' }];
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await RoleApi.getPermissions();
    expect(result.permissions[0].id).toBeUndefined();
  });

  it('空陣列時 permissions 應為 []', async () => {
    mockedApiClient.get.mockResolvedValueOnce([]);

    const result = await RoleApi.getPermissions();
    expect(result.permissions).toEqual([]);
  });

  it('sortOrder 缺失時應 fallback 為 0', async () => {
    const raw = [{ permissionId: 'p1', permissionCode: 'role:read', permissionName: '角色讀取' }];
    mockedApiClient.get.mockResolvedValueOnce(raw);

    const result = await RoleApi.getPermissions();
    expect(result.permissions[0].sort_order).toBe(0);
  });
});

// ════════════════════════════════════════════════════════════════
// SystemApi.ts — adaptParameterDto / adaptToggleDto / adaptJobDto
// ════════════════════════════════════════════════════════════════

describe('SystemApi — adaptParameterDto', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確對映 SystemParameterResponse 所有欄位', async () => {
    const raw = buildBackendSystemParameter();
    mockedApiClient.get.mockResolvedValueOnce([raw]);

    const result: SystemParameterDto[] = await SystemApi.getParameters();
    const param = result[0];

    expect(param.paramCode).toBe('MAX_FAILED_LOGIN_ATTEMPTS');
    expect(param.paramName).toBe('登入失敗上限');
    expect(param.paramValue).toBe('5');
    expect(param.paramType).toBe('INTEGER');
    expect(param.module).toBe('HR01');
    expect(param.category).toBe('SECURITY');
    expect(param.description).toBe('最大登入失敗次數');
    expect(param.defaultValue).toBe('5');
    // 後端 isEncrypted → 前端 encrypted（[差異欄位]）
    expect(param.encrypted).toBe(false);
    expect(param.updatedAt).toBe('2026-01-01T00:00:00');
    expect(param.updatedBy).toBe('admin');
  });

  it('後端回傳 isEncrypted = true 時前端 encrypted 應為 true', async () => {
    const raw = buildBackendSystemParameter({ isEncrypted: true });
    mockedApiClient.get.mockResolvedValueOnce([raw]);

    const result = await SystemApi.getParameters();
    expect(result[0].encrypted).toBe(true);
  });

  it('所有欄位為 null / undefined 時應回傳帶有預設值的 DTO（不拋錯）', async () => {
    mockedApiClient.get.mockResolvedValueOnce([{}]);

    const result = await SystemApi.getParameters();
    const param = result[0];

    expect(param.paramCode).toBe('');
    expect(param.paramName).toBe('');
    expect(param.paramValue).toBe('');
    expect(param.paramType).toBe('STRING');
    expect(param.module).toBe('');
    expect(param.category).toBe('');
    expect(param.encrypted).toBe(false);
    expect(param.updatedAt).toBeNull();
    expect(param.updatedBy).toBeNull();
  });

  it('後端回傳 snake_case 欄位時應以 fallback 正確對映', async () => {
    const raw = {
      param_code: 'TEST',
      param_name: '測試',
      param_value: '123',
      param_type: 'INTEGER',
      module: 'HR01',
      category: 'TEST',
      description: '測試參數',
      default_value: '0',
      is_encrypted: true,
      updated_at: '2026-03-01T00:00:00',
      updated_by: 'system',
    };
    mockedApiClient.get.mockResolvedValueOnce([raw]);

    const result = await SystemApi.getParameters();
    expect(result[0].paramCode).toBe('TEST');
    expect(result[0].encrypted).toBe(true);
    expect(result[0].updatedAt).toBe('2026-03-01T00:00:00');
  });

  it('非陣列回應時應回傳 []（防禦性處理）', async () => {
    mockedApiClient.get.mockResolvedValueOnce(null);

    const result = await SystemApi.getParameters();
    expect(result).toEqual([]);
  });
});

describe('SystemApi — adaptToggleDto', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確對映 FeatureToggleResponse 所有欄位', async () => {
    const raw = buildBackendFeatureToggle();
    mockedApiClient.get.mockResolvedValueOnce([raw]);

    const result: FeatureToggleDto[] = await SystemApi.getFeatures();
    const toggle = result[0];

    expect(toggle.featureCode).toBe('LATE_CHECK');
    expect(toggle.featureName).toBe('遲到判定');
    expect(toggle.module).toBe('HR03');
    expect(toggle.enabled).toBe(true);
    expect(toggle.description).toBe('啟用遲到自動判定');
    expect(toggle.updatedAt).toBe('2026-01-01T00:00:00');
    expect(toggle.updatedBy).toBe('admin');
  });

  it('enabled 為 false 時應正確回傳 false', async () => {
    const raw = buildBackendFeatureToggle({ enabled: false });
    mockedApiClient.get.mockResolvedValueOnce([raw]);

    const result = await SystemApi.getFeatures();
    expect(result[0].enabled).toBe(false);
  });

  it('所有欄位缺失時應回傳預設值（不拋錯）', async () => {
    mockedApiClient.get.mockResolvedValueOnce([{}]);

    const result = await SystemApi.getFeatures();
    const toggle = result[0];

    expect(toggle.featureCode).toBe('');
    expect(toggle.featureName).toBe('');
    expect(toggle.module).toBe('');
    expect(toggle.enabled).toBe(false);
    expect(toggle.description).toBe('');
    expect(toggle.updatedAt).toBeNull();
    expect(toggle.updatedBy).toBeNull();
  });

  it('updateParameter 應正確呼叫 PUT 並對映回傳值', async () => {
    const raw = buildBackendSystemParameter({ paramValue: '10' });
    mockedApiClient.put.mockResolvedValueOnce(raw);

    const result = await SystemApi.updateParameter('MAX_FAILED_LOGIN_ATTEMPTS', '10');
    expect(result.paramValue).toBe('10');
    expect(mockedApiClient.put).toHaveBeenCalledWith(
      '/system/parameters/MAX_FAILED_LOGIN_ATTEMPTS',
      { paramValue: '10' }
    );
  });

  it('toggleFeature 無 enabled 參數時應傳送空 body', async () => {
    const raw = buildBackendFeatureToggle({ enabled: false });
    mockedApiClient.put.mockResolvedValueOnce(raw);

    await SystemApi.toggleFeature('LATE_CHECK');
    const sentBody = mockedApiClient.put.mock.calls[0][1];
    expect(sentBody).toEqual({});
  });

  it('toggleFeature 有 enabled 參數時應傳送 { enabled }', async () => {
    const raw = buildBackendFeatureToggle({ enabled: true });
    mockedApiClient.put.mockResolvedValueOnce(raw);

    await SystemApi.toggleFeature('LATE_CHECK', true);
    const sentBody = mockedApiClient.put.mock.calls[0][1];
    expect(sentBody).toEqual({ enabled: true });
  });
});

describe('SystemApi — adaptJobDto', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確對映 ScheduledJobConfigResponse 所有欄位', async () => {
    const raw = buildBackendScheduledJob();
    mockedApiClient.get.mockResolvedValueOnce([raw]);

    const result: ScheduledJobConfigDto[] = await SystemApi.getJobs();
    const job = result[0];

    expect(job.jobCode).toBe('ABSENT_DETECTION');
    expect(job.jobName).toBe('曠職自動判定');
    expect(job.module).toBe('HR03');
    expect(job.cronExpression).toBe('0 0 19 * * ?');
    expect(job.enabled).toBe(true);
    expect(job.description).toBe('每日 19:00 掃描');
    expect(job.lastExecutedAt).toBe('2026-01-14T19:00:00');
    expect(job.lastExecutionStatus).toBe('SUCCESS');
    expect(job.lastErrorMessage).toBeNull();
    expect(job.consecutiveFailures).toBe(0);
    expect(job.updatedAt).toBe('2026-01-01T00:00:00');
    expect(job.updatedBy).toBe('admin');
  });

  it('所有欄位缺失時應回傳預設值（不拋錯）', async () => {
    mockedApiClient.get.mockResolvedValueOnce([{}]);

    const result = await SystemApi.getJobs();
    const job = result[0];

    expect(job.jobCode).toBe('');
    expect(job.cronExpression).toBe('');
    expect(job.enabled).toBe(false);
    expect(job.consecutiveFailures).toBe(0);
    expect(job.lastExecutedAt).toBeNull();
    expect(job.lastExecutionStatus).toBeNull();
    expect(job.lastErrorMessage).toBeNull();
  });

  it('consecutiveFailures 為 null 時應 fallback 為 0', async () => {
    const raw = buildBackendScheduledJob({ consecutiveFailures: null });
    mockedApiClient.get.mockResolvedValueOnce([raw]);

    const result = await SystemApi.getJobs();
    expect(result[0].consecutiveFailures).toBe(0);
  });

  it('updateJob 應正確呼叫 PUT 並對映回傳值', async () => {
    const raw = buildBackendScheduledJob({ cronExpression: '0 0 20 * * ?' });
    mockedApiClient.put.mockResolvedValueOnce(raw);

    const result = await SystemApi.updateJob('ABSENT_DETECTION', {
      cronExpression: '0 0 20 * * ?',
      enabled: true,
    });

    expect(result.cronExpression).toBe('0 0 20 * * ?');
    expect(mockedApiClient.put).toHaveBeenCalledWith(
      '/system/jobs/ABSENT_DETECTION',
      { cronExpression: '0 0 20 * * ?', enabled: true }
    );
  });

  it('後端回傳 snake_case 欄位時應以 fallback 正確對映', async () => {
    const raw = {
      job_code: 'PAYROLL_CLOSE',
      job_name: '薪資月結',
      module: 'HR04',
      cron_expression: '0 0 2 1 * ?',
      enabled: true,
      description: '每月月結',
      last_executed_at: '2026-01-01T02:00:00',
      last_execution_status: 'SUCCESS',
      last_error_message: null,
      consecutive_failures: 0,
      updated_at: '2026-01-01T00:00:00',
      updated_by: 'system',
    };
    mockedApiClient.get.mockResolvedValueOnce([raw]);

    const result = await SystemApi.getJobs();
    expect(result[0].jobCode).toBe('PAYROLL_CLOSE');
    expect(result[0].cronExpression).toBe('0 0 2 1 * ?');
    expect(result[0].lastExecutedAt).toBe('2026-01-01T02:00:00');
  });
});

// ════════════════════════════════════════════════════════════════
// CreateRoleResponse 對映驗證（透過 RoleApi.createRole）
// ════════════════════════════════════════════════════════════════

describe('RoleApi — createRole response mapping (CreateRoleResponse)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確對映 CreateRoleResponse 欄位（roleId, roleName, roleCode）', async () => {
    // CreateRoleResponse 只有 roleId, roleName, roleCode
    const raw = {
      roleId: 'new-role-001',
      roleName: '新角色',
      roleCode: 'NEW_ROLE',
    };
    mockedApiClient.post.mockResolvedValueOnce(raw);

    const result = await RoleApi.createRole({
      role_code: 'NEW_ROLE',
      role_name: '新角色',
      permission_ids: [],
    });

    expect(result.id).toBe('new-role-001');
    expect(result.role_name).toBe('新角色');
    expect(result.role_code).toBe('NEW_ROLE');
    // 缺失欄位應有預設值
    expect(result.is_system).toBe(false);
    expect(result.is_active).toBe(false); // status undefined → status !== 'ACTIVE'
    expect(result.permission_ids).toEqual([]);
    expect(result.user_count).toBe(0);
  });
});
