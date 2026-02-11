import type {
    BudgetType,
    CreateProjectRequest,
    CreateProjectResponse,
    CustomerDto,
    GetCustomerListRequest,
    GetCustomerListResponse,
    GetProjectListRequest,
    GetProjectListResponse,
    ProjectDto,
    ProjectStatus,
    ProjectType,
    UpdateProjectRequest,
} from './ProjectTypes';

const uuidv4 = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export class MockProjectApi {
  
  private static customers: CustomerDto[] = [
    {
      id: 'cust001',
      customer_code: 'C001',
      customer_name: 'ABC科技股份有限公司',
      tax_id: '12345678',
      industry: '資訊科技',
      email: 'contact@abc-tech.com',
      phone_number: '02-1234-5678',
      status: 'ACTIVE',
      created_at: '2024-01-01T00:00:00Z'
    },
    {
      id: 'cust002',
      customer_code: 'C002',
      customer_name: 'XYZ製造有限公司',
      tax_id: '87654321',
      industry: '製造業',
      email: 'info@xyz-mfg.com',
      phone_number: '03-9876-5432',
      status: 'ACTIVE',
      created_at: '2024-01-15T00:00:00Z'
    },
    {
      id: 'cust003',
      customer_code: 'C003',
      customer_name: '台灣金融集團',
      tax_id: '11223344',
      industry: '金融服務',
      email: 'contact@tw-finance.com',
      phone_number: '02-5555-6666',
      status: 'ACTIVE',
      created_at: '2024-02-01T00:00:00Z'
    }
  ];

  private static projects: ProjectDto[] = [
    {
      id: 'proj001',
      project_code: 'PRJ-2024-001',
      project_name: 'ERP系統開發專案',
      customer_id: 'cust001',
      customer_name: 'ABC科技股份有限公司',
      project_type: 'DEVELOPMENT' as ProjectType,
      project_manager_id: 'emp001',
      project_manager_name: 'John Doe',
      budget_type: 'FIXED_PRICE' as BudgetType,
      budget_amount: 5000000,
      budget_hours: 2000,
      actual_cost: 2500000,
      actual_hours: 1000,
      progress: 50,
      status: 'IN_PROGRESS' as ProjectStatus,
      planned_start_date: '2024-01-01',
      planned_end_date: '2024-12-31',
      actual_start_date: '2024-01-05',
      created_at: '2023-12-15T00:00:00Z',
      updated_at: '2024-06-01T00:00:00Z'
    },
    {
      id: 'proj002',
      project_code: 'PRJ-2024-002',
      project_name: '系統維護專案',
      customer_id: 'cust002',
      customer_name: 'XYZ製造有限公司',
      project_type: 'MAINTENANCE' as ProjectType,
      project_manager_id: 'emp002',
      project_manager_name: 'Jane Smith',
      budget_type: 'TIME_AND_MATERIAL' as BudgetType,
      budget_amount: 1200000,
      budget_hours: 500,
      actual_cost: 800000,
      actual_hours: 320,
      progress: 65,
      status: 'IN_PROGRESS' as ProjectStatus,
      planned_start_date: '2024-03-01',
      planned_end_date: '2024-08-31',
      actual_start_date: '2024-03-01',
      created_at: '2024-02-15T00:00:00Z',
      updated_at: '2024-06-15T00:00:00Z'
    },
    {
      id: 'proj003',
      project_code: 'PRJ-2024-003',
      project_name: '數位轉型顧問專案',
      customer_id: 'cust003',
      customer_name: '台灣金融集團',
      project_type: 'CONSULTING' as ProjectType,
      project_manager_id: 'emp001',
      project_manager_name: 'John Doe',
      budget_type: 'TIME_AND_MATERIAL' as BudgetType,
      budget_amount: 3000000,
      budget_hours: 1000,
      actual_cost: 3000000,
      actual_hours: 1000,
      progress: 100,
      status: 'COMPLETED' as ProjectStatus,
      planned_start_date: '2024-01-01',
      planned_end_date: '2024-05-31',
      actual_start_date: '2024-01-02',
      actual_end_date: '2024-05-30',
      created_at: '2023-12-01T00:00:00Z',
      updated_at: '2024-05-30T00:00:00Z'
    }
  ];

  // --- Customer APIs ---

  static async getCustomers(params?: GetCustomerListRequest): Promise<GetCustomerListResponse> {
    await delay(400);
    
    let filtered = [...this.customers];
    
    if (params?.keyword) {
      const keyword = params.keyword.toLowerCase();
      filtered = filtered.filter(c => 
        c.customer_name.toLowerCase().includes(keyword) ||
        c.customer_code.toLowerCase().includes(keyword)
      );
    }
    
    if (params?.status) {
      filtered = filtered.filter(c => c.status === params.status);
    }
    
    if (params?.industry) {
      filtered = filtered.filter(c => c.industry === params.industry);
    }
    
    const page = params?.page || 1;
    const size = params?.size || 10;
    const start = (page - 1) * size;
    const end = start + size;
    
    return {
      customers: filtered.slice(start, end),
      total: filtered.length,
      page,
      size
    };
  }

  static async getCustomerById(id: string): Promise<CustomerDto> {
    await delay(300);
    const customer = this.customers.find(c => c.id === id);
    if (!customer) throw new Error('Customer not found');
    return customer;
  }

  static async createCustomer(request: any): Promise<CustomerDto> {
    await delay(600);
    
    const newCustomer: CustomerDto = {
      id: uuidv4(),
      customer_code: request.customer_code,
      customer_name: request.customer_name,
      tax_id: request.tax_id,
      industry: request.industry,
      email: request.email,
      phone_number: request.phone_number,
      status: 'ACTIVE',
      created_at: new Date().toISOString()
    };
    
    this.customers = [newCustomer, ...this.customers];
    return newCustomer;
  }

  static async updateCustomer(id: string, request: any): Promise<CustomerDto> {
    await delay(500);
    
    const index = this.customers.findIndex(c => c.id === id);
    if (index === -1) throw new Error('Customer not found');
    
    this.customers[index] = {
      ...this.customers[index]!,
      ...request
    };
    
    return this.customers[index]!;
  }

  // --- Project APIs ---

  static async getProjects(params?: GetProjectListRequest): Promise<GetProjectListResponse> {
    await delay(400);
    
    let filtered = [...this.projects];
    
    if (params?.keyword) {
      const keyword = params.keyword.toLowerCase();
      filtered = filtered.filter(p => 
        p.project_name.toLowerCase().includes(keyword) ||
        p.project_code.toLowerCase().includes(keyword)
      );
    }
    
    if (params?.customer_id) {
      filtered = filtered.filter(p => p.customer_id === params.customer_id);
    }
    
    if (params?.status) {
      filtered = filtered.filter(p => p.status === params.status);
    }
    
    if (params?.project_type) {
      filtered = filtered.filter(p => p.project_type === params.project_type);
    }
    
    const page = params?.page || 1;
    const page_size = params?.page_size || 10;
    const start = (page - 1) * page_size;
    const end = start + page_size;
    
    return {
      projects: filtered.slice(start, end),
      total: filtered.length,
      page,
      page_size
    };
  }

  static async getProjectById(id: string): Promise<ProjectDto> {
    await delay(300);
    const project = this.projects.find(p => p.id === id);
    if (!project) throw new Error('Project not found');
    return project;
  }

  static async createProject(request: CreateProjectRequest): Promise<CreateProjectResponse> {
    await delay(800);
    
    const newProject: ProjectDto = {
      id: uuidv4(),
      project_code: request.project_code,
      project_name: request.project_name,
      customer_id: request.customer_id,
      customer_name: this.customers.find(c => c.id === request.customer_id)?.customer_name || 'Unknown',
      project_type: request.project_type,
      project_manager_id: request.project_manager_id,
      project_manager_name: 'Mock Manager',
      budget_type: request.budget_type,
      budget_amount: request.budget_amount,
      budget_hours: request.budget_hours,
      actual_cost: 0,
      actual_hours: 0,
      progress: 0,
      status: 'PLANNING',
      planned_start_date: request.planned_start_date,
      planned_end_date: request.planned_end_date,
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString()
    };
    
    this.projects = [newProject, ...this.projects];
    
    return {
      project_id: newProject.id,
      project_code: newProject.project_code,
      message: '專案建立成功'
    };
  }

  static async updateProject(id: string, request: UpdateProjectRequest): Promise<ProjectDto> {
    await delay(500);
    
    const index = this.projects.findIndex(p => p.id === id);
    if (index === -1) throw new Error('Project not found');
    
    this.projects[index] = {
      ...this.projects[index]!,
      ...request,
      updated_at: new Date().toISOString()
    };
    
    return this.projects[index]!;
  }

  static async deleteProject(id: string): Promise<void> {
    await delay(400);
    
    const index = this.projects.findIndex(p => p.id === id);
    if (index === -1) throw new Error('Project not found');
    
    this.projects.splice(index, 1);
  }

  static async updateProjectStatus(id: string, status: ProjectStatus): Promise<ProjectDto> {
    await delay(500);
    
    const index = this.projects.findIndex(p => p.id === id);
    if (index === -1) throw new Error('Project not found');
    
    this.projects[index] = {
      ...this.projects[index]!,
      status,
      updated_at: new Date().toISOString()
    };
    
    if (status === 'IN_PROGRESS' && !this.projects[index]!.actual_start_date) {
      this.projects[index]!.actual_start_date = new Date().toISOString().split('T')[0];
    }
    
    if (status === 'COMPLETED') {
      this.projects[index]!.actual_end_date = new Date().toISOString().split('T')[0];
      this.projects[index]!.progress = 100;
    }
    
    return this.projects[index]!;
  }
}
