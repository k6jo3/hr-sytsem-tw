import {
    CreateUserRequest,
    GetPermissionsResponse,
    GetRolesRequest,
    GetRolesResponse,
    GetUsersRequest,
    GetUsersResponse,
    LoginRequest,
    LoginResponse,
    PermissionDto,
    RoleDto,
    UpdateUserRequest,
    UserDto,
    UserStatus
} from './AuthTypes';

// Mock UUID generator
const uuidv4 = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export class MockAuthApi {

  // --- Mock Data ---
  private static currentUserId: string = 'demo-u001';

  private static users: UserDto[] = [
    {
      id: 'demo-u001',
      username: 'demo',
      email: 'demo@example.com',
      display_name: 'Demo User',
      first_name: 'Demo',
      last_name: 'User',
      status: 'ACTIVE' as UserStatus,
      role_list: ['USER'],
      role_ids: ['r002'],
      must_change_password: false,
      created_at: '2024-01-01T00:00:00Z',
      updated_at: '2024-01-01T00:00:00Z'
    },
    {
      id: 'u001',
      username: 'admin',
      email: 'admin@company.com',
      display_name: 'System Admin',
      first_name: 'System',
      last_name: 'Admin',
      status: 'ACTIVE' as UserStatus,
      role_list: ['ADMIN'],
      role_ids: ['r001'],
      must_change_password: false,
      created_at: '2023-01-01T00:00:00Z',
      updated_at: '2023-01-01T00:00:00Z'
    },
    {
      id: 'u002',
      username: 'user001',
      email: 'user001@company.com',
      display_name: 'John Doe',
      first_name: 'John',
      last_name: 'Doe',
      status: 'ACTIVE' as UserStatus,
      role_list: ['USER', 'HR'],
      role_ids: ['r002', 'r003'],
      must_change_password: false,
      created_at: '2023-02-01T00:00:00Z',
      updated_at: '2023-02-01T00:00:00Z'
    }
  ];

  private static roles: RoleDto[] = [
    {
      id: 'r001',
      role_code: 'ADMIN',
      role_name: 'Administrator',
      description: 'System Administrator with full access',
      is_system: true,
      is_active: true,
      permission_ids: ['p001', 'p002', 'p003', 'p004'],
      user_count: 1,
      created_at: '2023-01-01T00:00:00Z',
      updated_at: '2023-01-01T00:00:00Z'
    },
    {
      id: 'r002',
      role_code: 'USER',
      role_name: 'Standard User',
      description: 'Standard user access',
      is_system: true,
      is_active: true,
      permission_ids: ['p001'],
      user_count: 10,
      created_at: '2023-01-01T00:00:00Z',
      updated_at: '2023-01-01T00:00:00Z'
    },
    {
      id: 'r003',
      role_code: 'HR',
      role_name: 'HR Manager',
      description: 'HR Management access',
      is_system: false,
      is_active: true,
      permission_ids: ['p001', 'p002'],
      user_count: 2,
      created_at: '2023-01-01T00:00:00Z',
      updated_at: '2023-01-01T00:00:00Z'
    }
  ];

  private static permissions: PermissionDto[] = [
    {
      id: 'p001',
      permission_code: 'VIEW_DASHBOARD',
      permission_name: 'View Dashboard',
      module: 'COMMON',
      sort_order: 1
    },
    {
      id: 'p002',
      permission_code: 'MANAGE_USERS',
      permission_name: 'Manage Users',
      module: 'IAM',
      sort_order: 2
    },
    {
      id: 'p003',
      permission_code: 'MANAGE_ROLES',
      permission_name: 'Manage Roles',
      module: 'IAM',
      sort_order: 3
    },
    {
      id: 'p004',
      permission_code: 'VIEW_REPORTS',
      permission_name: 'View Reports',
      module: 'REPORT',
      sort_order: 4
    }
  ];

  // --- Auth Methods ---

  static async login(request: LoginRequest): Promise<LoginResponse> {
    await delay(500);
    
    // Find user by username
    const user = this.users.find(u => u.username === request.username);
    
    // Simple mock logic: allow login for any existing mock user
    if (user) {
      this.currentUserId = user.id;
      return {
        access_token: 'mock-access-token-' + uuidv4(),
        refresh_token: 'mock-refresh-token-' + uuidv4(),
        expires_in: 3600,
        user: user
      };
    }
    
    // Simulate invalid login
    throw new Error('帳號或密碼錯誤');
  }

  static async logout(): Promise<void> {
    await delay(200);
  }

  static async getCurrentUser(): Promise<UserDto> {
    await delay(300);
    const user = this.users.find(u => u.id === this.currentUserId);
    return user || this.users[0]!;
  }

  // --- User Management ---

  static async getUsers(request: GetUsersRequest): Promise<GetUsersResponse> {
    await delay(400);
    let filtered = [...this.users];
    if (request.keyword) {
        const lower = request.keyword.toLowerCase();
        filtered = filtered.filter(u => 
            u.username.toLowerCase().includes(lower) || 
            u.email.toLowerCase().includes(lower) ||
            u.display_name.toLowerCase().includes(lower)
        );
    }
    if (request.status) {
        filtered = filtered.filter(u => u.status === request.status);
    }

    return {
      content: filtered,
      pagination: {
        page: request.page || 1,
        page_size: request.page_size || 10,
        total: filtered.length,
        total_pages: Math.ceil(filtered.length / (request.page_size || 10))
      }
    };
  }

  static async getUser(id: string): Promise<UserDto> {
    await delay(300);
    const user = this.users.find(u => u.id === id);
    if (!user) throw new Error('User not found');
    return user;
  }

  static async createUser(request: CreateUserRequest): Promise<UserDto> {
    await delay(600);
    const newUser: UserDto = {
      id: uuidv4(),
      username: request.username,
      email: request.email,
      display_name: request.display_name,
      first_name: request.first_name,
      last_name: request.last_name,
      status: 'ACTIVE',
      role_list: request.role_ids ? request.role_ids.map(rid => {
          const r = this.roles.find(role => role.id === rid);
          return r ? r.role_code : '';
      }).filter(Boolean) : [],
      role_ids: request.role_ids || [],
      must_change_password: request.must_change_password || false,
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString()
    };
    this.users = [newUser, ...this.users];
    return newUser;
  }

  static async updateUser(id: string, request: UpdateUserRequest): Promise<UserDto> {
    await delay(500);
    const index = this.users.findIndex(u => u.id === id);
    if (index === -1) throw new Error('User not found');
    
    const existing = this.users[index]!;
    const updated: UserDto = {
        id: existing.id,
        username: existing.username,
        email: request.email ?? existing.email,
        display_name: request.display_name ?? existing.display_name,
        first_name: request.first_name ?? existing.first_name,
        last_name: request.last_name ?? existing.last_name,
        employee_id: request.employee_id ?? existing.employee_id,
        tenant_id: existing.tenant_id,
        status: existing.status,
        role_list: request.role_ids ? request.role_ids.map(rid => {
            const r = this.roles.find(role => role.id === rid);
            return r ? r.role_code : '';
        }).filter(Boolean) : existing.role_list,
        role_ids: request.role_ids ?? existing.role_ids,
        avatar_url: existing.avatar_url,
        must_change_password: existing.must_change_password,
        last_login_at: existing.last_login_at,
        password_changed_at: existing.password_changed_at,
        created_at: existing.created_at,
        updated_at: new Date().toISOString()
    };
    this.users[index] = updated;
    return updated;
  }

  static async deleteUser(id: string): Promise<void> {
    await delay(400);
    this.users = this.users.filter(u => u.id !== id);
  }

  // --- Role Management ---

  static async getRoles(_request: GetRolesRequest): Promise<GetRolesResponse> {
    await delay(400);
    return {
        roles: this.roles,
        pagination: {
            page: 1,
            page_size: 100,
            total: this.roles.length,
            total_pages: 1
        }
    };
  }

  // --- Permission ---

  static async getPermissions(): Promise<GetPermissionsResponse> {
    await delay(300);
    return {
        permissions: this.permissions
    };
  }
}
