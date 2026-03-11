import type {
    PayrollRunDto,
    PayslipDto,
    PayslipStatus,
    SalaryStructureDto
} from './PayrollTypes';

const uuidv4 = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export class MockPayrollApi {
  
  private static payslips: PayslipDto[] = [
    {
      id: 'ps001',
      payslip_code: 'PS-2024-01-001',
      employee_id: 'emp001',
      employee_name: 'John Doe',
      employee_code: 'E001',
      department_name: '資訊部',
      pay_period_start: '2024-01-01',
      pay_period_end: '2024-01-31',
      payment_date: '2024-02-05',
      status: 'PAID' as PayslipStatus,
      items: [
        {
          item_code: 'BASE_SALARY',
          item_name: '底薪',
          item_type: 'INCOME',
          amount: 50000
        },
        {
          item_code: 'OVERTIME_PAY',
          item_name: '加班費',
          item_type: 'INCOME',
          amount: 5000
        },
        {
          item_code: 'LABOR_INSURANCE',
          item_name: '勞保費',
          item_type: 'DEDUCTION',
          amount: 1500
        },
        {
          item_code: 'HEALTH_INSURANCE',
          item_name: '健保費',
          item_type: 'DEDUCTION',
          amount: 800
        }
      ],
      gross_pay: 55000,
      total_deductions: 2300,
      net_pay: 52700,
      work_days: 22,
      overtime_hours: 10,
      leave_days: 0,
      created_at: '2024-02-01T00:00:00Z',
      updated_at: '2024-02-01T00:00:00Z'
    }
  ];

  private static payrollRuns: PayrollRunDto[] = [
    {
      runId: 'run001',
      organizationId: 'org001',
      name: '2024年1月薪資',
      status: 'COMPLETED',
      payrollSystem: 'MONTHLY',
      start: '2024-01-01',
      end: '2024-01-31',
      payDate: '2024-02-05',
      totalDays: 31,
      totalEmployees: 50,
      processedEmployees: 50,
      successCount: 50,
      failureCount: 0,
      totalGrossPay: 2750000,
      totalNetPay: 2635000,
      totalDeductions: 115000,
      executedAt: '2024-02-01T10:00:00Z',
      completedAt: '2024-02-01T10:30:00Z'
    }
  ];

  // --- Payslip APIs ---

  static async getPayslips(params?: {
    employee_id?: string;
    status?: PayslipStatus;
    start_date?: string;
    end_date?: string;
    page?: number;
    page_size?: number;
  }): Promise<{ payslips: PayslipDto[]; total: number; page: number; page_size: number }> {
    await delay(400);
    
    let filtered = [...this.payslips];
    
    if (params?.employee_id) {
      filtered = filtered.filter(p => p.employee_id === params.employee_id);
    }
    
    if (params?.status) {
      filtered = filtered.filter(p => p.status === params.status);
    }
    
    const page = params?.page || 1;
    const page_size = params?.page_size || 10;
    
    return {
      payslips: filtered,
      total: filtered.length,
      page,
      page_size
    };
  }

  static async getPayslipById(id: string): Promise<PayslipDto> {
    await delay(300);
    const payslip = this.payslips.find(p => p.id === id);
    if (!payslip) throw new Error('Payslip not found');
    return payslip;
  }

  static async downloadPayslip(_id: string): Promise<Blob> {
    await delay(500);
    // Return a mock PDF blob
    return new Blob(['Mock PDF content'], { type: 'application/pdf' });
  }

  // --- Payroll Run APIs ---

  static async getPayrollRuns(params?: {
    status?: string;
    start_date?: string;
    end_date?: string;
  }): Promise<{ runs: PayrollRunDto[]; total: number }> {
    await delay(400);
    
    let filtered = [...this.payrollRuns];
    
    if (params?.status) {
      filtered = filtered.filter(r => r.status === params.status);
    }
    
    return {
      runs: filtered,
      total: filtered.length
    };
  }

  static async getPayrollRunById(id: string): Promise<PayrollRunDto> {
    await delay(300);
    const run = this.payrollRuns.find(r => r.runId === id);
    if (!run) throw new Error('Payroll run not found');
    return run;
  }

  static async createPayrollRun(request: {
    name: string;
    start: string;
    end: string;
    payDate: string;
  }): Promise<PayrollRunDto> {
    await delay(800);
    
    const newRun: PayrollRunDto = {
      runId: uuidv4(),
      organizationId: 'org001',
      name: request.name,
      status: 'DRAFT',
      payrollSystem: 'MONTHLY',
      start: request.start,
      end: request.end,
      payDate: request.payDate,
      totalDays: 30,
      totalEmployees: 0,
      processedEmployees: 0,
      successCount: 0,
      failureCount: 0,
      totalGrossPay: 0,
      totalNetPay: 0,
      totalDeductions: 0
    };
    
    this.payrollRuns = [newRun, ...this.payrollRuns];
    return newRun;
  }

  static async executePayrollRun(id: string): Promise<{ success: boolean; message: string }> {
    await delay(2000); // Simulate long calculation
    
    const index = this.payrollRuns.findIndex(r => r.runId === id);
    if (index === -1) throw new Error('Payroll run not found');
    
    this.payrollRuns[index] = {
      ...this.payrollRuns[index]!,
      status: 'COMPLETED',
      processedEmployees: 50,
      successCount: 50,
      executedAt: new Date().toISOString(),
      completedAt: new Date().toISOString()
    };
    
    return {
      success: true,
      message: '薪資計算完成'
    };
  }

  static async approvePayrollRun(id: string): Promise<void> {
    await delay(500);
    
    const index = this.payrollRuns.findIndex(r => r.runId === id);
    if (index === -1) throw new Error('Payroll run not found');
    
    this.payrollRuns[index] = {
      ...this.payrollRuns[index]!,
      status: 'APPROVED',
      approvedAt: new Date().toISOString()
    };
  }

  static async payPayrollRun(id: string): Promise<void> {
    await delay(500);
    
    const index = this.payrollRuns.findIndex(r => r.runId === id);
    if (index === -1) throw new Error('Payroll run not found');
    
    this.payrollRuns[index] = {
      ...this.payrollRuns[index]!,
      status: 'PAID',
      paidAt: new Date().toISOString()
    };
  }

  // --- Salary Structure APIs ---

  static async getSalaryStructure(employeeId: string): Promise<SalaryStructureDto> {
    await delay(400);
    
    return {
      id: uuidv4(),
      employeeId,
      payrollSystem: 'MONTHLY',
      payrollCycle: 'MONTHLY',
      monthlySalary: 50000,
      effectiveDate: '2024-01-01',
      active: true,
      items: [
        {
          itemId: 'item001',
          code: 'BASE',
          name: '底薪',
          type: 'EARNING',
          amount: 50000,
          fixedAmount: true,
          taxable: true,
          insurable: true
        }
      ]
    };
  }

  static async createSalaryStructure(request: any): Promise<SalaryStructureDto> {
    await delay(600);
    
    return {
      id: uuidv4(),
      employeeId: request.employeeId,
      payrollSystem: request.payrollSystem,
      payrollCycle: request.payrollCycle,
      monthlySalary: request.monthlySalary,
      hourlyRate: request.hourlyRate,
      effectiveDate: request.effectiveDate,
      active: true,
      items: request.items || []
    };
  }
}
