import type {
    DepartmentDto,
    DepartmentRequest,
    EmployeeDto,
    GetEmployeeDetailResponse,
    GetEmployeeListRequest,
    GetEmployeeListResponse,
    OrganizationDto,
    OrganizationRequest,
} from './OrganizationTypes';

const uuidv4 = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export class MockOrganizationApi {
  
  // --- Mock Data ---
  
  private static employees: EmployeeDto[] = [
    {
      id: 'emp001',
      employee_number: 'E001',
      full_name: 'John Doe',
      email: 'john.doe@company.com',
      phone: '0912-345-678',
      department_id: 'dept001',
      department_name: '資訊部',
      position: 'Senior Developer',
      status: 'ACTIVE',
      hire_date: '2020-01-15',
      created_at: '2020-01-15T00:00:00Z',
      updated_at: '2020-01-15T00:00:00Z'
    },
    {
      id: 'emp002',
      employee_number: 'E002',
      full_name: 'Jane Smith',
      email: 'jane.smith@company.com',
      phone: '0923-456-789',
      department_id: 'dept002',
      department_name: '人資部',
      position: 'HR Manager',
      status: 'ACTIVE',
      hire_date: '2019-03-20',
      created_at: '2019-03-20T00:00:00Z',
      updated_at: '2019-03-20T00:00:00Z'
    },
    {
      id: 'emp003',
      employee_number: 'E003',
      full_name: 'Bob Chen',
      email: 'bob.chen@company.com',
      department_id: 'dept001',
      department_name: '資訊部',
      position: 'Junior Developer',
      status: 'ON_LEAVE',
      hire_date: '2021-06-01',
      created_at: '2021-06-01T00:00:00Z',
      updated_at: '2021-06-01T00:00:00Z'
    }
  ];

  private static organizations: OrganizationDto[] = [
    {
      organizationId: 'org001',
      organizationCode: 'HQ',
      organizationName: '總公司',
      organizationType: 'PARENT',
      taxId: '12345678',
      address: '台北市信義區信義路五段7號',
      phoneNumber: '02-2345-6789',
      establishedDate: '2010-01-01',
      status: 'ACTIVE',
      employeeCount: 150,
      createdAt: '2010-01-01T00:00:00Z'
    },
    {
      organizationId: 'org002',
      organizationCode: 'BR01',
      organizationName: '台中分公司',
      organizationType: 'SUBSIDIARY',
      parentOrganizationId: 'org001',
      taxId: '87654321',
      address: '台中市西屯區台灣大道三段99號',
      phoneNumber: '04-2345-6789',
      establishedDate: '2015-06-01',
      status: 'ACTIVE',
      employeeCount: 50,
      createdAt: '2015-06-01T00:00:00Z'
    }
  ];

  private static departments: DepartmentDto[] = [
    {
      departmentId: 'dept001',
      code: 'IT',
      name: '資訊部',
      level: 1,
      sortOrder: 1,
      organizationId: 'org001',
      managerId: 'emp001',
      managerName: 'John Doe',
      status: 'ACTIVE',
      statusDisplay: '啟用',
      employeeCount: 25
    },
    {
      departmentId: 'dept002',
      code: 'HR',
      name: '人資部',
      level: 1,
      sortOrder: 2,
      organizationId: 'org001',
      managerId: 'emp002',
      managerName: 'Jane Smith',
      status: 'ACTIVE',
      statusDisplay: '啟用',
      employeeCount: 10
    },
    {
      departmentId: 'dept003',
      code: 'IT-DEV',
      name: '開發組',
      level: 2,
      sortOrder: 1,
      organizationId: 'org001',
      parentId: 'dept001',
      status: 'ACTIVE',
      statusDisplay: '啟用',
      employeeCount: 15
    }
  ];

  // --- Employee APIs ---

  static async getEmployeeList(params?: GetEmployeeListRequest): Promise<GetEmployeeListResponse> {
    await delay(400);
    
    let filtered = [...this.employees];
    
    if (params?.search) {
      const search = params.search.toLowerCase();
      filtered = filtered.filter(emp => 
        emp.full_name.toLowerCase().includes(search) ||
        emp.email.toLowerCase().includes(search) ||
        emp.employee_number.toLowerCase().includes(search)
      );
    }
    
    if (params?.department_id) {
      filtered = filtered.filter(emp => emp.department_id === params.department_id);
    }
    
    if (params?.status) {
      filtered = filtered.filter(emp => emp.status === params.status);
    }
    
    const page = params?.page || 1;
    const pageSize = params?.page_size || 10;
    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    
    return {
      employees: filtered.slice(start, end),
      total: filtered.length,
      page,
      page_size: pageSize
    };
  }

  static async getEmployeeDetail(id: string): Promise<GetEmployeeDetailResponse> {
    await delay(300);
    const employee = this.employees.find(emp => emp.id === id);
    if (!employee) throw new Error('Employee not found');
    return { employee };
  }

  static async createEmployee(data: any): Promise<EmployeeDto> {
    await delay(600);
    const newEmployee: EmployeeDto = {
      id: uuidv4(),
      employee_number: data.employee_number || `E${String(this.employees.length + 1).padStart(3, '0')}`,
      full_name: data.full_name,
      email: data.email,
      phone: data.phone,
      department_id: data.department_id,
      department_name: this.departments.find(d => d.departmentId === data.department_id)?.name || '',
      position: data.position,
      status: 'ACTIVE',
      hire_date: data.hire_date || new Date().toISOString().split('T')[0],
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString()
    };
    this.employees = [newEmployee, ...this.employees];
    return newEmployee;
  }

  static async updateEmployee(id: string, data: any): Promise<EmployeeDto> {
    await delay(500);
    const index = this.employees.findIndex(emp => emp.id === id);
    if (index === -1) throw new Error('Employee not found');
    
    const existing = this.employees[index]!;
    const updated: EmployeeDto = {
      ...existing,
      ...data,
      id: existing.id,
      updated_at: new Date().toISOString()
    };
    this.employees[index] = updated;
    return updated;
  }

  static async deleteEmployee(id: string): Promise<void> {
    await delay(400);
    this.employees = this.employees.filter(emp => emp.id !== id);
  }

  // --- Organization APIs ---

  static async getOrganizations(): Promise<{ content: OrganizationDto[] }> {
    await delay(400);
    return { content: this.organizations };
  }

  static async getOrganization(id: string): Promise<OrganizationDto> {
    await delay(300);
    const org = this.organizations.find(o => o.organizationId === id);
    if (!org) throw new Error('Organization not found');
    return org;
  }

  static async createOrganization(data: OrganizationRequest): Promise<OrganizationDto> {
    await delay(600);
    const newOrg: OrganizationDto = {
      organizationId: uuidv4(),
      organizationCode: data.organizationCode,
      organizationName: data.organizationName,
      organizationType: data.organizationType,
      parentOrganizationId: data.parentOrganizationId,
      taxId: data.taxId,
      address: data.address,
      phoneNumber: data.phoneNumber,
      establishedDate: data.establishedDate,
      status: 'ACTIVE',
      employeeCount: 0,
      createdAt: new Date().toISOString()
    };
    this.organizations = [newOrg, ...this.organizations];
    return newOrg;
  }

  static async updateOrganization(id: string, data: Partial<OrganizationRequest>): Promise<OrganizationDto> {
    await delay(500);
    const index = this.organizations.findIndex(o => o.organizationId === id);
    if (index === -1) throw new Error('Organization not found');
    
    const existing = this.organizations[index]!;
    const updated: OrganizationDto = {
      ...existing,
      ...data
    };
    this.organizations[index] = updated;
    return updated;
  }

  static async getOrganizationTree(id: string): Promise<{ data: OrganizationDto; departments: DepartmentDto[] }> {
    await delay(400);
    const org = this.organizations.find(o => o.organizationId === id);
    if (!org) throw new Error('Organization not found');
    
    const depts = this.departments.filter(d => d.organizationId === id);
    return { data: org, departments: depts };
  }

  // --- Department APIs ---

  static async getDepartments(params?: { organizationId?: string }): Promise<{ items: DepartmentDto[] }> {
    await delay(400);
    let filtered = [...this.departments];
    
    if (params?.organizationId) {
      filtered = filtered.filter(d => d.organizationId === params.organizationId);
    }
    
    return { items: filtered };
  }

  static async createDepartment(data: DepartmentRequest): Promise<DepartmentDto> {
    await delay(600);
    const newDept: DepartmentDto = {
      departmentId: uuidv4(),
      code: data.code,
      name: data.name,
      level: data.parentId ? 2 : 1,
      sortOrder: data.sortOrder || this.departments.length + 1,
      organizationId: data.organizationId,
      parentId: data.parentId,
      managerId: data.managerId,
      status: 'ACTIVE',
      statusDisplay: '啟用',
      employeeCount: 0
    };
    this.departments = [newDept, ...this.departments];
    return newDept;
  }

  static async updateDepartment(id: string, data: Partial<DepartmentRequest>): Promise<DepartmentDto> {
    await delay(500);
    const index = this.departments.findIndex(d => d.departmentId === id);
    if (index === -1) throw new Error('Department not found');
    
    const existing = this.departments[index]!;
    const updated: DepartmentDto = {
      ...existing,
      code: data.code ?? existing.code,
      name: data.name ?? existing.name,
      managerId: data.managerId ?? existing.managerId,
      sortOrder: data.sortOrder ?? existing.sortOrder
    };
    this.departments[index] = updated;
    return updated;
  }

  static async deleteDepartment(id: string): Promise<void> {
    await delay(400);
    this.departments = this.departments.filter(d => d.departmentId !== id);
  }
}
