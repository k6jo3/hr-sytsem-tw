/**
 * 截圖用 Mock 資料
 * 提供操作手冊截圖時各 API 端點的假資料，讓頁面呈現有意義的內容
 */

// ========== HR01 Auth / IAM ==========

const mockUsers = {
  items: [
    {
      userId: 'u001', username: 'admin', email: 'admin@company.com',
      displayName: 'System Admin', status: 'ACTIVE',
      roles: ['SYSTEM_ADMIN'], roleIds: ['r001'],
      createdAt: '2026-01-01T00:00:00', updatedAt: '2026-03-01T00:00:00',
    },
    {
      userId: 'u002', username: 'hr_admin', email: 'hr@company.com',
      displayName: '李小美', status: 'ACTIVE',
      roles: ['HR_ADMIN'], roleIds: ['r002'],
      createdAt: '2026-01-05T00:00:00', updatedAt: '2026-03-01T00:00:00',
    },
    {
      userId: 'u003', username: 'wang_ming', email: 'wang.ming@company.com',
      displayName: '王大明', status: 'ACTIVE',
      roles: ['EMPLOYEE'], roleIds: ['r003'],
      createdAt: '2026-01-10T00:00:00', updatedAt: '2026-02-15T00:00:00',
    },
    {
      userId: 'u004', username: 'chen_qiang', email: 'chen.qiang@company.com',
      displayName: '陳志強', status: 'ACTIVE',
      roles: ['MANAGER'], roleIds: ['r004'],
      createdAt: '2026-01-10T00:00:00', updatedAt: '2026-02-20T00:00:00',
    },
    {
      userId: 'u005', username: 'lin_yating', email: 'lin.yating@company.com',
      displayName: '林雅婷', status: 'ACTIVE',
      roles: ['PROJECT_MANAGER'], roleIds: ['r005'],
      createdAt: '2026-01-15T00:00:00', updatedAt: '2026-03-01T00:00:00',
    },
  ],
  totalElements: 5, page: 1, size: 20,
};

const mockRoles = [
  { roleId: 'r001', roleCode: 'SYSTEM_ADMIN', roleName: '系統管理員', description: '系統最高權限管理者', isSystemRole: true, status: 'ACTIVE', userCount: 1, createdAt: '2026-01-01' },
  { roleId: 'r002', roleCode: 'HR_ADMIN', roleName: 'HR 管理員', description: '人資部門管理者，負責員工資料與薪資', isSystemRole: true, status: 'ACTIVE', userCount: 2, createdAt: '2026-01-01' },
  { roleId: 'r003', roleCode: 'EMPLOYEE', roleName: '一般員工', description: '一般員工，可查看個人資料與打卡', isSystemRole: true, status: 'ACTIVE', userCount: 45, createdAt: '2026-01-01' },
  { roleId: 'r004', roleCode: 'MANAGER', roleName: '部門主管', description: '部門主管，可審核部屬假勤與績效', isSystemRole: true, status: 'ACTIVE', userCount: 5, createdAt: '2026-01-01' },
  { roleId: 'r005', roleCode: 'PROJECT_MANAGER', roleName: '專案經理', description: '負責專案管理與工時審核', isSystemRole: false, status: 'ACTIVE', userCount: 3, createdAt: '2026-01-01' },
];

const mockPermissions = [
  {
    permissionId: 'p001', permissionCode: 'user:read', permissionName: '查看使用者', resource: 'IAM', sortOrder: 1,
    children: [
      { permissionId: 'p002', permissionCode: 'user:create', permissionName: '建立使用者', resource: 'IAM', sortOrder: 2 },
      { permissionId: 'p003', permissionCode: 'user:update', permissionName: '修改使用者', resource: 'IAM', sortOrder: 3 },
    ],
  },
  {
    permissionId: 'p010', permissionCode: 'employee:read', permissionName: '查看員工', resource: 'ORGANIZATION', sortOrder: 10,
    children: [
      { permissionId: 'p011', permissionCode: 'employee:create', permissionName: '建立員工', resource: 'ORGANIZATION', sortOrder: 11 },
    ],
  },
  {
    permissionId: 'p020', permissionCode: 'attendance:read', permissionName: '查看考勤', resource: 'ATTENDANCE', sortOrder: 20,
  },
];

// ========== HR02 Organization ==========

const mockOrganizations = {
  content: [
    {
      organizationId: 'org-001', code: 'TTC', name: '台灣科技股份有限公司',
      type: 'PARENT', status: 'ACTIVE', employeeCount: 52,
      taxId: '12345678', address: '台北市信義區信義路五段7號', phone: '02-2345-6789',
      createdAt: '2026-01-01',
    },
  ],
  items: [
    {
      organizationId: 'org-001', code: 'TTC', name: '台灣科技股份有限公司',
      type: 'PARENT', status: 'ACTIVE', employeeCount: 52,
      taxId: '12345678', address: '台北市信義區信義路五段7號', phone: '02-2345-6789',
      createdAt: '2026-01-01',
    },
  ],
};

const mockOrgTree = {
  organizationId: 'org-001', code: 'TTC', name: '台灣科技股份有限公司',
  type: 'PARENT', status: 'ACTIVE', employeeCount: 52,
  departments: [
    { departmentId: 'dept-001', code: 'RD', name: '研發部', level: 1, sortOrder: 1, status: 'ACTIVE', employeeCount: 20, managerId: 'u004', managerName: '陳志強' },
    { departmentId: 'dept-002', code: 'HR', name: '人力資源部', level: 1, sortOrder: 2, status: 'ACTIVE', employeeCount: 8, managerId: 'u002', managerName: '李小美' },
    { departmentId: 'dept-003', code: 'FIN', name: '財務部', level: 1, sortOrder: 3, status: 'ACTIVE', employeeCount: 6, managerId: null, managerName: null },
    { departmentId: 'dept-004', code: 'RD-FE', name: '前端組', level: 2, sortOrder: 1, status: 'ACTIVE', employeeCount: 8, parentId: 'dept-001', parentDepartmentId: 'dept-001' },
    { departmentId: 'dept-005', code: 'RD-BE', name: '後端組', level: 2, sortOrder: 2, status: 'ACTIVE', employeeCount: 12, parentId: 'dept-001', parentDepartmentId: 'dept-001' },
  ],
};

const mockEmployees = {
  items: [
    {
      employeeId: 'emp-001', employeeNumber: 'EMP-2026-001', fullName: '王大明',
      companyEmail: 'wang.ming@company.com', mobilePhone: '0912-345-678',
      departmentId: 'dept-004', departmentName: '前端組',
      jobTitle: '資深前端工程師', employmentStatus: 'ACTIVE',
      hireDate: '2024-03-01', createdAt: '2024-03-01', updatedAt: '2026-03-01',
    },
    {
      employeeId: 'emp-002', employeeNumber: 'EMP-2026-002', fullName: '李小美',
      companyEmail: 'li.mei@company.com', mobilePhone: '0923-456-789',
      departmentId: 'dept-002', departmentName: '人力資源部',
      jobTitle: 'HR 主管', employmentStatus: 'ACTIVE',
      hireDate: '2023-06-15', createdAt: '2023-06-15', updatedAt: '2026-02-28',
    },
    {
      employeeId: 'emp-003', employeeNumber: 'EMP-2026-003', fullName: '陳志強',
      companyEmail: 'chen.qiang@company.com', mobilePhone: '0934-567-890',
      departmentId: 'dept-001', departmentName: '研發部',
      jobTitle: '研發部經理', employmentStatus: 'ACTIVE',
      hireDate: '2022-01-10', createdAt: '2022-01-10', updatedAt: '2026-03-01',
    },
    {
      employeeId: 'emp-004', employeeNumber: 'EMP-2026-004', fullName: '林雅婷',
      companyEmail: 'lin.yating@company.com', mobilePhone: '0945-678-901',
      departmentId: 'dept-005', departmentName: '後端組',
      jobTitle: '專案經理', employmentStatus: 'ACTIVE',
      hireDate: '2024-08-01', createdAt: '2024-08-01', updatedAt: '2026-03-01',
    },
    {
      employeeId: 'emp-005', employeeNumber: 'EMP-2026-005', fullName: '張家豪',
      companyEmail: 'zhang.jh@company.com', mobilePhone: '0956-789-012',
      departmentId: 'dept-004', departmentName: '前端組',
      jobTitle: '前端工程師', employmentStatus: 'PROBATION',
      hireDate: '2026-02-01', createdAt: '2026-02-01', updatedAt: '2026-03-01',
    },
  ],
  totalElements: 52, page: 1, size: 20,
};

// ========== HR03 Attendance ==========

const now = new Date();
const todayStr = now.toISOString().split('T')[0];

const mockTodayAttendance = {
  records: [
    {
      recordId: 'att-001', employeeId: 'emp-001', employeeName: '王大明',
      checkInTime: `${todayStr}T08:55:00`, checkOutTime: null,
      lateMinutes: 0, earlyLeaveMinutes: 0,
    },
  ],
  hasCheckedIn: true,
  hasCheckedOut: false,
  totalWorkHours: 4.5,
};

const mockAttendanceHistory = {
  items: [
    { recordId: 'att-101', employeeId: 'emp-001', employeeName: '王大明', checkInTime: '2026-03-17T09:02:00', checkOutTime: '2026-03-17T18:15:00', lateMinutes: 2, earlyLeaveMinutes: 0 },
    { recordId: 'att-102', employeeId: 'emp-001', employeeName: '王大明', checkInTime: '2026-03-14T08:50:00', checkOutTime: '2026-03-14T18:05:00', lateMinutes: 0, earlyLeaveMinutes: 0 },
    { recordId: 'att-103', employeeId: 'emp-001', employeeName: '王大明', checkInTime: '2026-03-13T08:58:00', checkOutTime: '2026-03-13T18:30:00', lateMinutes: 0, earlyLeaveMinutes: 0 },
  ],
  totalElements: 3, page: 1, size: 20,
};

const mockLeaveApplications = {
  items: [
    { applicationId: 'lv-001', employeeId: 'emp-001', employeeName: '王大明', leaveTypeId: 'lt-01', leaveTypeName: '特休假', startDate: '2026-03-20', endDate: '2026-03-21', days: 2, reason: '家庭旅遊', status: 'PENDING', createdAt: '2026-03-15' },
    { applicationId: 'lv-002', employeeId: 'emp-003', employeeName: '陳志強', leaveTypeId: 'lt-02', leaveTypeName: '事假', startDate: '2026-03-25', endDate: '2026-03-25', days: 1, reason: '個人事務', status: 'APPROVED', createdAt: '2026-03-10' },
    { applicationId: 'lv-003', employeeId: 'emp-004', employeeName: '林雅婷', leaveTypeId: 'lt-01', leaveTypeName: '特休假', startDate: '2026-04-01', endDate: '2026-04-03', days: 3, reason: '出國休假', status: 'PENDING', createdAt: '2026-03-16' },
  ],
  content: [],
  totalElements: 3, page: 1, size: 20,
};

const mockLeaveBalances = {
  balances: [
    { leaveTypeId: 'lt-01', leaveTypeName: '特休假', totalDays: 10, usedDays: 3, remainingDays: 7, year: 2026 },
    { leaveTypeId: 'lt-02', leaveTypeName: '事假', totalDays: 14, usedDays: 1, remainingDays: 13, year: 2026 },
    { leaveTypeId: 'lt-03', leaveTypeName: '病假', totalDays: 30, usedDays: 0, remainingDays: 30, year: 2026 },
    { leaveTypeId: 'lt-04', leaveTypeName: '婚假', totalDays: 8, usedDays: 0, remainingDays: 8, year: 2026 },
  ],
};

const mockShifts = [
  { shiftId: 'sh-001', shiftCode: 'DAY', shiftName: '日班', shiftType: 'STANDARD', workStartTime: '09:00', workEndTime: '18:00', startTime: '09:00', endTime: '18:00', breakStartTime: '12:00', breakEndTime: '13:00', isActive: true, workHours: 8 },
  { shiftId: 'sh-002', shiftCode: 'FLEX', shiftName: '彈性班', shiftType: 'FLEXIBLE', workStartTime: '08:00', workEndTime: '17:00', startTime: '08:00', endTime: '17:00', breakStartTime: '12:00', breakEndTime: '13:00', isActive: true, workHours: 8 },
  { shiftId: 'sh-003', shiftCode: 'NIGHT', shiftName: '晚班', shiftType: 'ROTATING', workStartTime: '14:00', workEndTime: '23:00', startTime: '14:00', endTime: '23:00', breakStartTime: '18:00', breakEndTime: '19:00', isActive: true, workHours: 8 },
];

const mockLeaveTypes = [
  { leaveTypeId: 'lt-01', leaveTypeCode: 'ANNUAL', leaveTypeName: '特休假', isPaid: true, annualQuotaDays: 30, maxDays: 30, allowCarryOver: true, isActive: true },
  { leaveTypeId: 'lt-02', leaveTypeCode: 'PERSONAL', leaveTypeName: '事假', isPaid: false, annualQuotaDays: 14, maxDays: 14, allowCarryOver: false, isActive: true },
  { leaveTypeId: 'lt-03', leaveTypeCode: 'SICK', leaveTypeName: '病假', isPaid: true, annualQuotaDays: 30, maxDays: 30, allowCarryOver: false, isActive: true },
  { leaveTypeId: 'lt-04', leaveTypeCode: 'MARRIAGE', leaveTypeName: '婚假', isPaid: true, annualQuotaDays: 8, maxDays: 8, allowCarryOver: false, isActive: true },
  { leaveTypeId: 'lt-05', leaveTypeCode: 'MATERNITY', leaveTypeName: '產假', isPaid: true, annualQuotaDays: 56, maxDays: 56, allowCarryOver: false, isActive: true },
];

// ========== HR04 Payroll ==========

const mockPayrollRuns = {
  items: [
    { runId: 'pr-001', name: '2026年01月薪資', organizationId: 'org-001', status: 'PAID', start: '2026-01-01', end: '2026-01-31', payDate: '2026-02-05', totalEmployees: 50, processedEmployees: 50, successCount: 50, failureCount: 0, totalGrossPay: 3250000, totalNetPay: 2680000, totalDeductions: 570000 },
    { runId: 'pr-002', name: '2026年02月薪資', organizationId: 'org-001', status: 'APPROVED', start: '2026-02-01', end: '2026-02-28', payDate: '2026-03-05', totalEmployees: 52, processedEmployees: 52, successCount: 52, failureCount: 0, totalGrossPay: 3380000, totalNetPay: 2790000, totalDeductions: 590000 },
    { runId: 'pr-003', name: '2026年03月薪資', organizationId: 'org-001', status: 'CALCULATING', start: '2026-03-01', end: '2026-03-31', payDate: '2026-04-05', totalEmployees: 52, processedEmployees: 30, successCount: 30, failureCount: 0, totalGrossPay: 0, totalNetPay: 0, totalDeductions: 0 },
  ],
  totalElements: 3,
};

const mockPayslips = {
  items: [
    { id: 'ps-001', employeeId: 'emp-001', employeeName: '王大明', employeeNumber: 'EMP-2026-001', departmentName: '前端組', periodStartDate: '2026-01-01', periodEndDate: '2026-01-31', payDate: '2026-02-05', status: 'PAID', baseSalary: 65000, grossWage: 72000, totalDeductions: 11500, netWage: 60500 },
    { id: 'ps-002', employeeId: 'emp-002', employeeName: '李小美', employeeNumber: 'EMP-2026-002', departmentName: '人力資源部', periodStartDate: '2026-01-01', periodEndDate: '2026-01-31', payDate: '2026-02-05', status: 'PAID', baseSalary: 58000, grossWage: 62000, totalDeductions: 9800, netWage: 52200 },
    { id: 'ps-003', employeeId: 'emp-003', employeeName: '陳志強', employeeNumber: 'EMP-2026-003', departmentName: '研發部', periodStartDate: '2026-01-01', periodEndDate: '2026-01-31', payDate: '2026-02-05', status: 'PAID', baseSalary: 85000, grossWage: 95000, totalDeductions: 16200, netWage: 78800 },
  ],
  totalElements: 52, page: 1, size: 20,
};

const mockSalaryStructures = {
  items: [
    { id: 'ss-001', structureName: '一般員工薪資結構', grade: 'G1', baseSalary: 42000, mealAllowance: 2400, transportAllowance: 1500, status: 'ACTIVE' },
    { id: 'ss-002', structureName: '資深工程師薪資結構', grade: 'G3', baseSalary: 65000, mealAllowance: 2400, transportAllowance: 2000, status: 'ACTIVE' },
    { id: 'ss-003', structureName: '部門主管薪資結構', grade: 'G5', baseSalary: 85000, mealAllowance: 2400, transportAllowance: 3000, status: 'ACTIVE' },
  ],
  total: 3,
};

const mockPayrollItemDefinitions = [
  { id: 'pid-001', itemCode: 'BASE_SALARY', itemName: '底薪', itemType: 'EARNING', calculationType: 'FIXED', isActive: true },
  { id: 'pid-002', itemCode: 'MEAL_ALLOWANCE', itemName: '伙食津貼', itemType: 'EARNING', calculationType: 'FIXED', defaultAmount: 2400, isActive: true },
  { id: 'pid-003', itemCode: 'TRANSPORT', itemName: '交通津貼', itemType: 'EARNING', calculationType: 'FIXED', defaultAmount: 1500, isActive: true },
  { id: 'pid-004', itemCode: 'OVERTIME_PAY', itemName: '加班費', itemType: 'EARNING', calculationType: 'FORMULA', isActive: true },
  { id: 'pid-005', itemCode: 'LABOR_INS', itemName: '勞保費', itemType: 'DEDUCTION', calculationType: 'RATE', rate: 0.115, isActive: true },
];

// ========== HR05 Insurance ==========

const mockEnrollments = {
  items: [
    { enrollmentId: 'enr-001', employeeId: 'emp-001', employeeName: '王大明', insuranceUnitId: 'iu-001', insuranceUnitName: '台灣科技公司', insuranceType: 'LABOR', enrollDate: '2024-03-01', monthlySalary: 65000, levelNumber: 23, status: 'ACTIVE', isReported: true, createdAt: '2024-03-01' },
    { enrollmentId: 'enr-002', employeeId: 'emp-002', employeeName: '李小美', insuranceUnitId: 'iu-001', insuranceUnitName: '台灣科技公司', insuranceType: 'LABOR', enrollDate: '2023-06-15', monthlySalary: 58000, levelNumber: 21, status: 'ACTIVE', isReported: true, createdAt: '2023-06-15' },
    { enrollmentId: 'enr-003', employeeId: 'emp-005', employeeName: '張家豪', insuranceUnitId: 'iu-001', insuranceUnitName: '台灣科技公司', insuranceType: 'LABOR', enrollDate: '2026-02-01', monthlySalary: 42000, levelNumber: 15, status: 'ACTIVE', isReported: false, createdAt: '2026-02-01' },
  ],
  totalElements: 3, page: 1, size: 20,
};

const mockMyInsurance = {
  employeeName: '王大明',
  unitName: '台灣科技公司',
  enrollments: [
    { enrollmentId: 'enr-001', employeeId: 'emp-001', employeeName: '王大明', insuranceType: 'LABOR', enrollDate: '2024-03-01', monthlySalary: 65000, levelNumber: 23, status: 'ACTIVE' },
    { enrollmentId: 'enr-004', employeeId: 'emp-001', employeeName: '王大明', insuranceType: 'HEALTH', enrollDate: '2024-03-01', monthlySalary: 65000, levelNumber: 23, status: 'ACTIVE' },
  ],
  fees: {
    laborEmployeeFee: 1493, laborEmployerFee: 4550, healthEmployeeFee: 987,
    healthEmployerFee: 3098, pensionEmployerFee: 3900, totalEmployeeFee: 2480, totalEmployerFee: 11548,
  },
  history: [
    { historyId: 'h-001', changeDate: '2024-03-01', changeType: 'ENROLL', insuranceType: 'LABOR', monthlySalary: 55000, levelNumber: 20, reason: '新進員工加保' },
    { historyId: 'h-002', changeDate: '2025-07-01', changeType: 'ADJUST', insuranceType: 'LABOR', monthlySalary: 65000, levelNumber: 23, reason: '年度調薪' },
  ],
};

const mockInsuranceLevels = [
  { id: 'lv-01', insuranceType: 'LABOR', levelNumber: 1, monthlySalary: 27470, active: true, effectiveDate: '2026-01-01' },
  { id: 'lv-15', insuranceType: 'LABOR', levelNumber: 15, monthlySalary: 42000, active: true, effectiveDate: '2026-01-01' },
  { id: 'lv-23', insuranceType: 'LABOR', levelNumber: 23, monthlySalary: 65000, active: true, effectiveDate: '2026-01-01' },
];

// ========== HR06 Project ==========

const mockProjects = {
  items: [
    { projectId: 'prj-001', projectCode: 'PRJ-2026-001', projectName: '電商平台重構專案', customerId: 'cust-001', status: 'IN_PROGRESS', startDate: '2026-01-15', endDate: '2026-06-30', totalBudget: 2500000, budgetHours: 3000, actualCost: 980000, actualHours: 1200, progress: 40, ownerId: 'emp-004', createdAt: '2026-01-10' },
    { projectId: 'prj-002', projectCode: 'PRJ-2026-002', projectName: 'HR系統導入', customerId: 'cust-002', status: 'PLANNING', startDate: '2026-04-01', endDate: '2026-09-30', totalBudget: 1800000, budgetHours: 2000, actualCost: 0, actualHours: 0, progress: 0, ownerId: 'emp-004', createdAt: '2026-03-01' },
    { projectId: 'prj-003', projectCode: 'PRJ-2025-008', projectName: '行動APP開發', customerId: 'cust-003', status: 'COMPLETED', startDate: '2025-06-01', endDate: '2025-12-31', totalBudget: 1200000, budgetHours: 1500, actualCost: 1150000, actualHours: 1480, progress: 100, ownerId: 'emp-004', createdAt: '2025-05-20' },
  ],
  total: 3, totalElements: 3, page: 0, size: 20,
};

const mockCustomers = {
  items: [
    { customerId: 'cust-001', customerCode: 'CUST-001', customerName: '遠東百貨股份有限公司', taxId: '12345679', industry: '零售業', email: 'contact@feds.com', phoneNumber: '02-7711-1234', status: 'ACTIVE', createdAt: '2025-06-01' },
    { customerId: 'cust-002', customerCode: 'CUST-002', customerName: '台灣中油股份有限公司', taxId: '03711102', industry: '能源業', email: 'info@cpc.com.tw', phoneNumber: '02-8789-5678', status: 'ACTIVE', createdAt: '2025-08-15' },
    { customerId: 'cust-003', customerCode: 'CUST-003', customerName: '國泰人壽保險股份有限公司', taxId: '03459901', industry: '金融保險', email: 'service@cathaylife.com', phoneNumber: '02-2326-9000', status: 'ACTIVE', createdAt: '2025-03-10' },
  ],
  total: 3, totalElements: 3,
};

// ========== HR07 Timesheet ==========

const mockWeeklyTimesheet = {
  items: [
    {
      timesheetId: 'ts-001', employeeId: 'emp-001', employeeName: '王大明',
      periodStartDate: '2026-03-16', periodEndDate: '2026-03-20',
      totalHours: 38, status: 'DRAFT',
      entries: [
        { entryId: 'te-001', projectId: 'prj-001', projectName: '電商平台重構專案', taskCode: 'WBS-001', taskName: '前端頁面開發', workDate: '2026-03-16', hours: 8, description: '商品列表頁切版' },
        { entryId: 'te-002', projectId: 'prj-001', projectName: '電商平台重構專案', taskCode: 'WBS-001', taskName: '前端頁面開發', workDate: '2026-03-17', hours: 7.5, description: '購物車功能實作' },
        { entryId: 'te-003', projectId: 'prj-001', projectName: '電商平台重構專案', taskCode: 'WBS-002', taskName: 'API 串接', workDate: '2026-03-18', hours: 8, description: '訂單 API 串接' },
        { entryId: 'te-004', projectId: 'prj-001', projectName: '電商平台重構專案', taskCode: 'WBS-002', taskName: 'API 串接', workDate: '2026-03-19', hours: 7, description: '付款流程整合' },
        { entryId: 'te-005', projectId: 'prj-001', projectName: '電商平台重構專案', taskCode: 'WBS-003', taskName: '測試', workDate: '2026-03-20', hours: 7.5, description: 'E2E 測試撰寫' },
      ],
    },
  ],
};

const mockPendingApprovals = {
  items: [
    { timesheetId: 'ts-010', employeeId: 'emp-001', employeeName: '王大明', periodStartDate: '2026-03-09', periodEndDate: '2026-03-13', totalHours: 40, status: 'PENDING', submittedAt: '2026-03-14T09:00:00', entries: [] },
    { timesheetId: 'ts-011', employeeId: 'emp-005', employeeName: '張家豪', periodStartDate: '2026-03-09', periodEndDate: '2026-03-13', totalHours: 36, status: 'PENDING', submittedAt: '2026-03-14T10:30:00', entries: [] },
    { timesheetId: 'ts-012', employeeId: 'emp-003', employeeName: '陳志強', periodStartDate: '2026-03-09', periodEndDate: '2026-03-13', totalHours: 42, status: 'PENDING', submittedAt: '2026-03-14T11:00:00', entries: [] },
  ],
  total: 3,
};

const mockTimesheetSummary = {
  totalHours: 1520,
  projects: [
    { projectName: '電商平台重構專案', totalHours: 680 },
    { projectName: 'HR系統導入', totalHours: 240 },
    { projectName: '行動APP開發', totalHours: 600 },
  ],
  departmentHours: [
    { departmentName: '研發部', totalHours: 960 },
    { departmentName: '人力資源部', totalHours: 320 },
    { departmentName: '財務部', totalHours: 240 },
  ],
  unreportedEmployees: [
    { employeeId: 'emp-010', employeeName: '周美玲' },
  ],
};

// ========== HR08 Performance ==========

const mockPerformanceCycles = {
  items: [
    { cycleId: 'cyc-001', cycleName: '2026年度考核', cycleType: 'ANNUAL', startDate: '2026-01-01', endDate: '2026-12-31', selfEvalDeadline: '2026-12-15', managerEvalDeadline: '2026-12-25', status: 'ACTIVE', createdAt: '2025-12-01' },
    { cycleId: 'cyc-002', cycleName: '2026上半年考核', cycleType: 'SEMI_ANNUAL', startDate: '2026-01-01', endDate: '2026-06-30', selfEvalDeadline: '2026-06-20', managerEvalDeadline: '2026-06-28', status: 'IN_PROGRESS', createdAt: '2026-01-05' },
  ],
  totalElements: 2,
};

const mockTeamReviews = {
  items: [
    { employeeId: 'emp-001', employeeName: '王大明', employeeCode: 'EMP-2026-001', departmentName: '前端組', positionName: '資深前端工程師', status: 'PENDING_SELF', overallScore: null, overallRating: null, submittedAt: null },
    { employeeId: 'emp-005', employeeName: '張家豪', employeeCode: 'EMP-2026-005', departmentName: '前端組', positionName: '前端工程師', status: 'PENDING_MANAGER', overallScore: 4.2, overallRating: 'B+', submittedAt: '2026-03-10T14:00:00' },
    { employeeId: 'emp-004', employeeName: '林雅婷', employeeCode: 'EMP-2026-004', departmentName: '後端組', positionName: '專案經理', status: 'FINALIZED', overallScore: 4.8, overallRating: 'A', submittedAt: '2026-03-05T10:00:00' },
  ],
  totalElements: 3, page: 1, size: 20,
};

const mockPerformanceDistribution = {
  distribution: {
    A: { rating: 'A', count: 5, percentage: 10 },
    'B+': { rating: 'B+', count: 12, percentage: 24 },
    B: { rating: 'B', count: 20, percentage: 40 },
    C: { rating: 'C', count: 10, percentage: 20 },
    D: { rating: 'D', count: 3, percentage: 6 },
  },
  totalEmployees: 50,
  averageScore: 3.8,
};

const mockMyPerformance = {
  items: [
    {
      reviewId: 'rv-001', cycleId: 'cyc-002', cycleName: '2026上半年考核',
      employeeId: 'emp-001', employeeName: '王大明', reviewerId: 'emp-001', reviewerName: '王大明',
      reviewType: 'SELF', status: 'PENDING_SELF',
      evaluationItems: [
        { itemId: 'ei-001', itemName: '工作品質', weight: 30, score: null, maxScore: 5 },
        { itemId: 'ei-002', itemName: '工作效率', weight: 25, score: null, maxScore: 5 },
        { itemId: 'ei-003', itemName: '團隊合作', weight: 20, score: null, maxScore: 5 },
        { itemId: 'ei-004', itemName: '專業知識', weight: 25, score: null, maxScore: 5 },
      ],
      overallScore: null, comments: '', createdAt: '2026-03-01',
    },
  ],
};

// ========== HR09 Recruitment ==========

const mockJobOpenings = {
  content: [
    { id: 'job-001', title: '資深 React 前端工程師', departmentId: 'dept-004', departmentName: '前端組', numberOfPositions: 2, minSalary: 60000, maxSalary: 90000, status: 'OPEN', openDate: '2026-03-01', createdBy: 'emp-002', createdAt: '2026-03-01' },
    { id: 'job-002', title: 'Java 後端工程師', departmentId: 'dept-005', departmentName: '後端組', numberOfPositions: 3, minSalary: 55000, maxSalary: 85000, status: 'OPEN', openDate: '2026-02-15', createdBy: 'emp-002', createdAt: '2026-02-15' },
    { id: 'job-003', title: 'HR 專員', departmentId: 'dept-002', departmentName: '人力資源部', numberOfPositions: 1, minSalary: 40000, maxSalary: 55000, status: 'FILLED', openDate: '2026-01-10', closeDate: '2026-02-28', createdBy: 'emp-002', createdAt: '2026-01-10' },
  ],
  totalElements: 3, number: 0, size: 20,
};

const mockCandidates = {
  content: [
    { candidateId: 'can-001', openingId: 'job-001', jobTitle: '資深 React 前端工程師', fullName: '趙雲翔', email: 'zhao.yx@gmail.com', phoneNumber: '0978-111-222', source: 'JOB_BANK', applicationDate: '2026-03-10', status: 'INTERVIEWING', createdAt: '2026-03-10' },
    { candidateId: 'can-002', openingId: 'job-001', jobTitle: '資深 React 前端工程師', fullName: '黃詩涵', email: 'huang.sh@gmail.com', phoneNumber: '0965-333-444', source: 'LINKEDIN', applicationDate: '2026-03-08', status: 'SCREENING', createdAt: '2026-03-08' },
    { candidateId: 'can-003', openingId: 'job-002', jobTitle: 'Java 後端工程師', fullName: '劉建國', email: 'liu.jg@yahoo.com', phoneNumber: '0933-555-666', source: 'REFERRAL', referrerName: '陳志強', applicationDate: '2026-03-12', status: 'NEW', createdAt: '2026-03-12' },
    { candidateId: 'can-004', openingId: 'job-003', jobTitle: 'HR 專員', fullName: '吳佩珊', email: 'wu.ps@gmail.com', phoneNumber: '0911-777-888', source: 'WEBSITE', applicationDate: '2026-01-20', status: 'HIRED', createdAt: '2026-01-20' },
  ],
  totalElements: 4, number: 0, size: 20,
};

const mockRecruitmentDashboard = {
  kpis: { openJobsCount: 2, totalApplications: 15, interviewsScheduled: 5, hiredCount: 1 },
  sourceAnalytics: [
    { source: 'JOB_BANK', count: 6, percentage: 40 },
    { source: 'LINKEDIN', count: 4, percentage: 27 },
    { source: 'REFERRAL', count: 3, percentage: 20 },
    { source: 'WEBSITE', count: 2, percentage: 13 },
  ],
  conversionFunnel: { rates: { interviewRate: 0.6, offerRate: 0.4, acceptRate: 0.8 } },
};

// ========== HR10 Training ==========

const mockCourses = {
  content: [
    { courseId: 'crs-001', courseCode: 'TRN-2026-001', courseName: 'React 18 進階開發', courseType: 'INTERNAL', deliveryMode: 'ONLINE', category: '技術類', instructor: '陳志強', durationHours: 16, maxParticipants: 30, currentEnrollments: 22, startDate: '2026-04-01', endDate: '2026-04-02', status: 'PLANNED', isMandatory: false },
    { courseId: 'crs-002', courseCode: 'TRN-2026-002', courseName: '資訊安全基礎認知', courseType: 'MANDATORY', deliveryMode: 'ONLINE', category: '合規類', instructor: '外部講師', durationHours: 4, maxParticipants: 100, currentEnrollments: 85, startDate: '2026-03-15', endDate: '2026-03-15', status: 'IN_PROGRESS', isMandatory: true },
    { courseId: 'crs-003', courseCode: 'TRN-2026-003', courseName: '主管領導力培訓', courseType: 'EXTERNAL', deliveryMode: 'OFFLINE', category: '管理類', instructor: '台灣管理顧問公司', durationHours: 24, maxParticipants: 15, currentEnrollments: 12, startDate: '2026-05-10', endDate: '2026-05-12', location: '台北國際會議中心', cost: 15000, status: 'PLANNED', isMandatory: false },
  ],
  totalElements: 3,
};

const mockTrainingStatistics = {
  totalCourses: 12,
  totalEnrollments: 156,
  totalTrainingHours: 480,
  completionRate: 78.5,
  coursesByCategory: { '技術類': 5, '合規類': 3, '管理類': 2, '語言類': 2 },
  hoursByDepartment: { '研發部': 240, '人力資源部': 80, '財務部': 60, '其他': 100 },
};

// ========== HR11 Workflow ==========

const mockWorkflowDefinitions = {
  content: [
    {
      definitionId: 'wf-001', flowName: '請假簽核流程', flowType: 'LEAVE',
      nodesJson: JSON.stringify([
        { nodeId: 'n1', nodeType: 'START', name: '申請人提交' },
        { nodeId: 'n2', nodeType: 'APPROVAL', name: '直屬主管審核', assigneeType: 'DIRECT_MANAGER' },
        { nodeId: 'n3', nodeType: 'APPROVAL', name: 'HR 確認', assigneeType: 'ROLE', assigneeIds: ['HR_ADMIN'] },
        { nodeId: 'n4', nodeType: 'END', name: '完成' },
      ]),
      edgesJson: JSON.stringify([
        { edgeId: 'e1', from: 'n1', to: 'n2' },
        { edgeId: 'e2', from: 'n2', to: 'n3' },
        { edgeId: 'e3', from: 'n3', to: 'n4' },
      ]),
      active: true, version: 1, createdAt: '2026-01-01',
    },
    {
      definitionId: 'wf-002', flowName: '加班簽核流程', flowType: 'OVERTIME',
      nodesJson: JSON.stringify([
        { nodeId: 'n1', nodeType: 'START', name: '員工申請' },
        { nodeId: 'n2', nodeType: 'APPROVAL', name: '部門主管審核' },
        { nodeId: 'n3', nodeType: 'END', name: '完成' },
      ]),
      edgesJson: JSON.stringify([
        { edgeId: 'e1', from: 'n1', to: 'n2' },
        { edgeId: 'e2', from: 'n2', to: 'n3' },
      ]),
      active: true, version: 1, createdAt: '2026-01-01',
    },
    {
      definitionId: 'wf-003', flowName: '薪資調整簽核', flowType: 'SALARY',
      nodesJson: JSON.stringify([
        { nodeId: 'n1', nodeType: 'START', name: 'HR 提案' },
        { nodeId: 'n2', nodeType: 'APPROVAL', name: '部門主管' },
        { nodeId: 'n3', nodeType: 'APPROVAL', name: '總經理' },
        { nodeId: 'n4', nodeType: 'END', name: '完成' },
      ]),
      edgesJson: JSON.stringify([
        { edgeId: 'e1', from: 'n1', to: 'n2' },
        { edgeId: 'e2', from: 'n2', to: 'n3' },
        { edgeId: 'e3', from: 'n3', to: 'n4' },
      ]),
      active: false, version: 2, createdAt: '2026-02-01',
    },
  ],
  totalElements: 3, number: 0, size: 20,
};

const mockPendingTasks = {
  content: [
    { taskId: 'task-001', instanceId: 'inst-001', flowName: '請假簽核流程', businessType: 'LEAVE', businessId: 'lv-001', summary: '王大明 申請特休假 2 天 (03/20-03/21)', nodeName: '直屬主管審核', applicantName: '王大明', assigneeName: '陳志強', status: 'PENDING', dueDate: '2026-03-19', createdAt: '2026-03-15' },
    { taskId: 'task-002', instanceId: 'inst-002', flowName: '加班簽核流程', businessType: 'OVERTIME', businessId: 'ot-001', summary: '張家豪 申請加班 3 小時 (03/15)', nodeName: '部門主管審核', applicantName: '張家豪', assigneeName: '陳志強', status: 'PENDING', dueDate: '2026-03-17', createdAt: '2026-03-15' },
    { taskId: 'task-003', instanceId: 'inst-003', flowName: '請假簽核流程', businessType: 'LEAVE', businessId: 'lv-003', summary: '林雅婷 申請特休假 3 天 (04/01-04/03)', nodeName: 'HR 確認', applicantName: '林雅婷', assigneeName: '李小美', status: 'PENDING', dueDate: '2026-03-20', createdAt: '2026-03-16' },
  ],
  totalElements: 3, number: 0, size: 20,
};

const mockDelegations = {
  content: [
    { delegationId: 'del-001', delegatorId: 'emp-003', delegatorName: '陳志強', delegateId: 'emp-004', delegateeName: '林雅婷', startDate: '2026-03-20', endDate: '2026-03-25', status: 'ACTIVE', createdAt: '2026-03-18' },
  ],
};

const mockMyApplications = {
  content: [
    { instanceId: 'inst-001', definitionId: 'wf-001', flowName: '請假簽核流程', businessType: 'LEAVE', businessId: 'lv-001', applicantName: '王大明', currentNodeName: '直屬主管審核', status: 'RUNNING', startedAt: '2026-03-15' },
    { instanceId: 'inst-005', definitionId: 'wf-001', flowName: '請假簽核流程', businessType: 'LEAVE', businessId: 'lv-old', applicantName: '王大明', currentNodeName: '完成', status: 'COMPLETED', startedAt: '2026-02-10', completedAt: '2026-02-12' },
  ],
  totalElements: 2, number: 0, size: 20,
};

// ========== HR12 Notification ==========

const mockNotifications = {
  items: [
    { notificationId: 'noti-001', recipientId: 'emp-001', title: '請假申請已核准', content: '您的特休假申請 (02/10-02/12) 已獲核准。', notificationType: 'APPROVAL', channel: 'IN_APP', priority: 'NORMAL', status: 'READ', sentAt: '2026-03-14T09:00:00', readAt: '2026-03-14T10:30:00', createdAt: '2026-03-14' },
    { notificationId: 'noti-002', recipientId: 'emp-001', title: '薪資單已發放', content: '2026年02月薪資單已產生，請至薪資單頁面查看。', notificationType: 'PAYROLL', channel: 'IN_APP', priority: 'HIGH', status: 'SENT', sentAt: '2026-03-05T08:00:00', createdAt: '2026-03-05' },
    { notificationId: 'noti-003', recipientId: 'emp-001', title: '工時提交提醒', content: '本週工時尚未提交，請於週五前完成。', notificationType: 'REMINDER', channel: 'IN_APP', priority: 'NORMAL', status: 'SENT', sentAt: '2026-03-17T09:00:00', createdAt: '2026-03-17' },
  ],
  pagination: { totalItems: 3, currentPage: 1, pageSize: 10 },
  summary: { unreadCount: 2 },
};

const mockNotificationTemplates = {
  items: [
    { templateId: 'tmpl-001', templateCode: 'LEAVE_APPROVED', name: '請假核准通知', subjectTemplate: '您的{{leaveType}}已核准', contentTemplate: '您申請的{{leaveType}} ({{startDate}}-{{endDate}}) 已獲核准。', defaultChannels: ['IN_APP', 'EMAIL'], status: 'ACTIVE', createdAt: '2026-01-01' },
    { templateId: 'tmpl-002', templateCode: 'PAYSLIP_READY', name: '薪資單通知', subjectTemplate: '{{month}}月薪資單已產生', contentTemplate: '{{month}}月薪資單已產生，請至系統查看。', defaultChannels: ['IN_APP'], status: 'ACTIVE', createdAt: '2026-01-01' },
    { templateId: 'tmpl-003', templateCode: 'TIMESHEET_REMINDER', name: '工時提交提醒', subjectTemplate: '工時提交提醒', contentTemplate: '本週工時尚未提交，請於{{deadline}}前完成。', defaultChannels: ['IN_APP', 'EMAIL'], status: 'ACTIVE', createdAt: '2026-01-01' },
  ],
  pagination: { totalItems: 3, currentPage: 1, pageSize: 10 },
};

const mockAnnouncements = {
  items: [
    { announcementId: 'ann-001', title: '2026年清明連假公告', content: '4月3日至4月6日為清明連假，共計4天。請同仁提前安排工作。', priority: 'HIGH', targetRoles: [], publishedAt: '2026-03-15T10:00:00', expireAt: '2026-04-07T00:00:00', status: 'PUBLISHED', publishedBy: { fullName: '李小美' }, createdAt: '2026-03-15' },
    { announcementId: 'ann-002', title: '員工健康檢查通知', content: '2026年度員工健康檢查將於4月15日至4月30日進行，請至人資部預約時段。', priority: 'NORMAL', targetRoles: [], publishedAt: '2026-03-10T09:00:00', expireAt: '2026-04-30T00:00:00', status: 'PUBLISHED', publishedBy: { fullName: '李小美' }, createdAt: '2026-03-10' },
  ],
  pagination: { totalItems: 2, currentPage: 1, pageSize: 10 },
};

const mockUnreadCount = { unreadCount: 3 };

const mockNotificationPreference = {
  preferenceId: 'pref-001', employeeId: 'emp-001',
  channels: { emailEnabled: true, pushEnabled: false, inAppEnabled: true },
  quietHours: { startTime: '22:00', endTime: '08:00' },
  updatedAt: '2026-03-01',
};

// ========== HR13 Document ==========

const mockDocuments = {
  content: [
    { documentId: 'doc-001', documentType: 'UPLOADED', fileName: '2026年度預算報告.pdf', fileSize: 2457600, mimeType: 'application/pdf', ownerId: 'emp-002', ownerName: '李小美', visibility: 'DEPARTMENT', version: 1, uploadedAt: '2026-03-10T14:00:00', createdAt: '2026-03-10' },
    { documentId: 'doc-002', documentType: 'UPLOADED', fileName: '員工手冊_v3.docx', fileSize: 1843200, mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', ownerId: 'emp-002', ownerName: '李小美', visibility: 'COMPANY', version: 3, uploadedAt: '2026-02-28T10:00:00', createdAt: '2025-06-01' },
    { documentId: 'doc-003', documentType: 'GENERATED', fileName: '王大明_在職證明.pdf', fileSize: 102400, mimeType: 'application/pdf', ownerId: 'emp-001', ownerName: '王大明', visibility: 'PRIVATE', version: 1, uploadedAt: '2026-03-15T16:00:00', createdAt: '2026-03-15' },
  ],
  totalElements: 3, number: 0, size: 20,
};

const mockDocumentTemplates = {
  content: [
    { templateId: 'dtpl-001', templateCode: 'EMPLOYMENT_CERT', name: '在職證明', category: 'HR', content: '茲證明 {{employeeName}} 自 {{hireDate}} 起任職本公司...', variables: ['employeeName', 'hireDate', 'position'], status: 'ACTIVE', createdAt: '2026-01-01' },
    { templateId: 'dtpl-002', templateCode: 'SALARY_CERT', name: '薪資證明', category: 'HR', content: '茲證明 {{employeeName}} 月薪為 {{salary}} 元...', variables: ['employeeName', 'salary', 'period'], status: 'ACTIVE', createdAt: '2026-01-01' },
    { templateId: 'dtpl-003', templateCode: 'RESIGNATION', name: '離職證明', category: 'HR', content: '茲證明 {{employeeName}} 已於 {{lastDate}} 離職...', variables: ['employeeName', 'hireDate', 'lastDate'], status: 'ACTIVE', createdAt: '2026-01-01' },
  ],
  totalElements: 3, number: 0, size: 20,
};

// ========== HR14 Reporting ==========

const mockDashboardKpis = {
  content: [
    { employeeId: 'emp-001', employeeName: '王大明', departmentName: '前端組', departmentId: 'dept-004', status: 'ACTIVE' },
    { employeeId: 'emp-002', employeeName: '李小美', departmentName: '人力資源部', departmentId: 'dept-002', status: 'ACTIVE' },
    { employeeId: 'emp-003', employeeName: '陳志強', departmentName: '研發部', departmentId: 'dept-001', status: 'ACTIVE' },
    { employeeId: 'emp-004', employeeName: '林雅婷', departmentName: '後端組', departmentId: 'dept-005', status: 'ACTIVE' },
    { employeeId: 'emp-005', employeeName: '張家豪', departmentName: '前端組', departmentId: 'dept-004', status: 'ACTIVE' },
  ],
};

const mockAttendanceStats = {
  content: [
    { employeeName: '王大明', actualDays: 20, absentCount: 0, lateCount: 1, attendanceRate: 0.95, overtimeHours: 6 },
    { employeeName: '李小美', actualDays: 20, absentCount: 0, lateCount: 0, attendanceRate: 1.0, overtimeHours: 2 },
    { employeeName: '陳志強', actualDays: 19, absentCount: 1, lateCount: 0, attendanceRate: 0.90, overtimeHours: 10 },
  ],
};

const mockPayrollSummary = {
  content: [],
};

// ========== Auth 相關路由 ==========

const mockCurrentUser = {
  userId: 'u001', username: 'admin', email: 'admin@company.com',
  displayName: 'System Admin', status: 'ACTIVE', roles: ['SYSTEM_ADMIN'],
  roleIds: ['r001'], createdAt: '2026-01-01',
};

// ========== 月結狀態 ==========

const mockMonthlyClose = {
  items: [
    { id: 'mc-001', month: '2026-01', status: 'CLOSED', closedAt: '2026-02-05T10:00:00', closedBy: '李小美' },
    { id: 'mc-002', month: '2026-02', status: 'CLOSED', closedAt: '2026-03-05T09:30:00', closedBy: '李小美' },
    { id: 'mc-003', month: '2026-03', status: 'OPEN', closedAt: null, closedBy: null },
  ],
  totalElements: 3,
};

// ========== URL 路由表 ==========

/**
 * 根據 API URL 回傳對應的 mock 資料
 * URL 格式為 http://localhost:5173/api/v1/{path}
 */
export function getMockResponse(url: string): object {
  // 擷取 /api/v1/ 之後的路徑
  const apiMatch = url.match(/\/api\/v1\/(.+?)(\?|$)/);
  if (!apiMatch) {
    return { data: [], items: [], content: [], total: 0, totalElements: 0, success: true };
  }
  const apiPath = apiMatch[1];

  // ---- HR01 Auth / IAM ----
  if (apiPath === 'auth/me' || apiPath === 'auth/login') return mockCurrentUser;
  if (apiPath === 'users') return mockUsers;
  if (apiPath === 'roles') return mockRoles;
  if (apiPath === 'permissions') return mockPermissions;

  // ---- HR02 Organization ----
  if (apiPath === 'organizations') return mockOrganizations;
  if (apiPath.match(/^organizations\/[^/]+\/tree$/)) return mockOrgTree;
  if (apiPath.match(/^organizations\/[^/]+$/)) return mockOrgTree; // 單一組織詳情
  if (apiPath === 'employees') return mockEmployees;
  if (apiPath.match(/^employees\/[^/]+$/)) return mockEmployees.items[0];
  if (apiPath === 'departments') return { items: mockOrgTree.departments };

  // ---- HR03 Attendance ----
  if (apiPath === 'attendance/today') return mockTodayAttendance;
  if (apiPath === 'attendance/records') return mockAttendanceHistory;
  if (apiPath === 'attendance/corrections') return { items: [], totalElements: 0 };
  if (apiPath === 'shifts') return mockShifts;
  if (apiPath === 'leave/applications') return mockLeaveApplications;
  if (apiPath.match(/^leave\/balances\//)) return mockLeaveBalances;
  if (apiPath === 'leave/types') return mockLeaveTypes;
  // 加班
  if (apiPath.match(/^attendance\/overtime/)) return { items: [
    { overtimeId: 'ot-001', employeeId: 'emp-001', employeeName: '王大明', overtimeDate: '2026-03-15', startTime: '18:00', endTime: '21:00', hours: 3, overtimeType: 'WORKDAY', reason: '專案趕工', status: 'PENDING', createdAt: '2026-03-15' },
    { overtimeId: 'ot-002', employeeId: 'emp-005', employeeName: '張家豪', overtimeDate: '2026-03-16', startTime: '09:00', endTime: '17:00', hours: 8, overtimeType: 'REST_DAY', reason: '系統上線支援', status: 'APPROVED', createdAt: '2026-03-14' },
  ], totalElements: 2 };
  // 月結
  if (apiPath.match(/^attendance\/monthly/)) return mockMonthlyClose;

  // ---- HR04 Payroll ----
  if (apiPath === 'payroll-runs') return mockPayrollRuns;
  if (apiPath.match(/^payroll-runs\/[^/]+$/)) return mockPayrollRuns.items[0];
  if (apiPath === 'payslips') return mockPayslips;
  if (apiPath.match(/^payslips\/[^/]+$/)) return mockPayslips.items[0];
  if (apiPath === 'salary-structures') return mockSalaryStructures;
  if (apiPath.match(/^salary-structures\/employee\//)) return mockSalaryStructures.items[0];
  if (apiPath === 'payroll-item-definitions') return mockPayrollItemDefinitions;

  // ---- HR05 Insurance ----
  if (apiPath === 'insurance/my') return mockMyInsurance;
  if (apiPath === 'insurance/enrollments/active') return mockEnrollments.items.filter(e => e.status === 'ACTIVE');
  if (apiPath === 'insurance/enrollments') return mockEnrollments;
  if (apiPath === 'insurance/levels') return mockInsuranceLevels;
  if (apiPath === 'insurance/fees/calculate') return { laborEmployeeFee: 1493, laborEmployerFee: 4550, healthEmployeeFee: 987, healthEmployerFee: 3098, pensionEmployerFee: 3900, totalEmployeeFee: 2480, totalEmployerFee: 11548, levelNumber: 23 };

  // ---- HR06 Project ----
  if (apiPath === 'projects') return mockProjects;
  if (apiPath.match(/^projects\/[^/]+\/wbs$/)) return { rootTasks: [
    { taskId: 't-001', taskCode: 'WBS-001', taskName: '前端頁面開發', level: 1, status: 'IN_PROGRESS', estimatedHours: 200, actualHours: 120, progress: 60, assigneeName: '王大明', children: [
      { taskId: 't-002', taskCode: 'WBS-001-01', taskName: '商品列表頁', level: 2, status: 'COMPLETED', estimatedHours: 40, actualHours: 38, progress: 100, assigneeName: '王大明' },
      { taskId: 't-003', taskCode: 'WBS-001-02', taskName: '購物車頁面', level: 2, status: 'IN_PROGRESS', estimatedHours: 60, actualHours: 40, progress: 65, assigneeName: '張家豪' },
    ] },
    { taskId: 't-004', taskCode: 'WBS-002', taskName: 'API 串接', level: 1, status: 'IN_PROGRESS', estimatedHours: 150, actualHours: 80, progress: 50, assigneeName: '林雅婷' },
    { taskId: 't-005', taskCode: 'WBS-003', taskName: '測試', level: 1, status: 'NOT_STARTED', estimatedHours: 100, actualHours: 0, progress: 0, assigneeName: null },
  ] };
  if (apiPath.match(/^projects\/[^/]+\/members$/)) return [
    { id: 'm-001', employeeId: 'emp-001', employeeName: '王大明', role: 'DEVELOPER', allocatedHours: 500, actualHours: 240, joinDate: '2026-01-15' },
    { id: 'm-002', employeeId: 'emp-005', employeeName: '張家豪', role: 'DEVELOPER', allocatedHours: 400, actualHours: 180, joinDate: '2026-02-01' },
    { id: 'm-003', employeeId: 'emp-004', employeeName: '林雅婷', role: 'PROJECT_MANAGER', allocatedHours: 300, actualHours: 150, joinDate: '2026-01-15' },
  ];
  if (apiPath.match(/^projects\/[^/]+\/cost$/)) return { totalCost: 980000, budget: 2500000, actualCost: 980000, laborCost: 850000, otherCost: 130000 };
  if (apiPath.match(/^projects\/[^/]+$/)) return { ...mockProjects.items[0], members: [] };
  if (apiPath === 'customers') return mockCustomers;
  if (apiPath.match(/^customers\/[^/]+$/)) return mockCustomers.items[0];

  // ---- HR07 Timesheet ----
  if (apiPath === 'timesheets/my') return mockWeeklyTimesheet;
  if (apiPath === 'timesheets/approvals') return mockPendingApprovals;
  if (apiPath === 'timesheets/summary') return mockTimesheetSummary;

  // ---- HR08 Performance ----
  if (apiPath === 'performance/cycles') return mockPerformanceCycles;
  if (apiPath.match(/^performance\/cycles\/[^/]+$/)) return mockPerformanceCycles.items[0];
  if (apiPath === 'performance/reviews/my') return mockMyPerformance;
  if (apiPath === 'performance/reviews/team') return mockTeamReviews;
  if (apiPath.match(/^performance\/reports\/distribution\//)) return mockPerformanceDistribution;

  // ---- HR09 Recruitment ----
  if (apiPath === 'recruitment/jobs') return mockJobOpenings;
  if (apiPath === 'recruitment/candidates') return mockCandidates;
  if (apiPath.match(/^recruitment\/candidates\/[^/]+$/)) return { ...mockCandidates.content[0], interviews: [], evaluations: [] };
  if (apiPath === 'recruitment/dashboard') return mockRecruitmentDashboard;

  // ---- HR10 Training ----
  if (apiPath === 'training/courses') return mockCourses;
  if (apiPath.match(/^training\/courses\/[^/]+$/)) return mockCourses.content[0];
  if (apiPath === 'training/my') return { content: [
    { enrollmentId: 'enr-t-001', courseId: 'crs-002', courseName: '資訊安全基礎認知', employeeId: 'emp-001', status: 'IN_PROGRESS', attendance: true, completedHours: 2 },
    { enrollmentId: 'enr-t-002', courseId: 'crs-001', courseName: 'React 18 進階開發', employeeId: 'emp-001', status: 'ENROLLED', attendance: false },
  ], totalElements: 2 };
  if (apiPath === 'training/my/hours') return { employeeId: 'emp-001', totalHours: 32, yearToDateHours: 18 };
  if (apiPath === 'training/enrollments') return { content: [], totalElements: 0 };
  if (apiPath === 'training/certificates') return { content: [
    { certificateId: 'cert-001', employeeId: 'emp-001', certificateName: 'AWS Solutions Architect', issuingOrganization: 'Amazon Web Services', certificateNumber: 'AWS-SAA-2026-001', issueDate: '2025-09-15', expiryDate: '2028-09-15', category: '雲端', isRequired: false, isVerified: true, status: 'ACTIVE' },
  ], totalElements: 1 };
  if (apiPath === 'training/statistics') return mockTrainingStatistics;

  // ---- HR11 Workflow ----
  if (apiPath === 'workflows/definitions') return mockWorkflowDefinitions;
  if (apiPath === 'workflows/pending-tasks') return mockPendingTasks;
  if (apiPath === 'workflows/my/applications') return mockMyApplications;
  if (apiPath === 'workflows/delegations') return mockDelegations;
  if (apiPath.match(/^workflows\/[^/]+\/history$/)) return {
    instanceId: 'inst-001', definitionId: 'wf-001', flowName: '請假簽核流程',
    businessType: 'LEAVE', businessId: 'lv-001', applicantName: '王大明',
    currentNodeId: 'n2', currentNodeName: '直屬主管審核', status: 'RUNNING',
    startedAt: '2026-03-15',
    timeline: [
      { taskId: 'task-hist-001', nodeName: '申請人提交', applicantName: '王大明', status: 'APPROVED', createdAt: '2026-03-15T09:00:00', approvedAt: '2026-03-15T09:00:00' },
      { taskId: 'task-hist-002', nodeName: '直屬主管審核', applicantName: '王大明', assigneeName: '陳志強', status: 'PENDING', createdAt: '2026-03-15T09:01:00' },
    ],
  };

  // ---- HR12 Notification ----
  if (apiPath === 'notifications/me') return mockNotifications;
  if (apiPath === 'notifications/unread-count') return mockUnreadCount;
  if (apiPath === 'notifications/templates') return mockNotificationTemplates;
  if (apiPath === 'notifications/announcements') return mockAnnouncements;
  if (apiPath === 'notifications/preferences') return mockNotificationPreference;

  // ---- HR13 Document ----
  if (apiPath === 'documents/my') return mockDocuments;
  if (apiPath === 'documents') return mockDocuments;
  if (apiPath === 'documents/templates') return mockDocumentTemplates;
  if (apiPath === 'documents/requests') return { content: [], totalElements: 0 };
  if (apiPath === 'documents/download-logs') return { content: [], totalElements: 0 };

  // ---- HR14 Reporting ----
  if (apiPath === 'reporting/hr/employee-roster') return mockDashboardKpis;
  if (apiPath === 'reporting/hr/attendance-statistics') return mockAttendanceStats;
  if (apiPath === 'reporting/finance/payroll-summary') return mockPayrollSummary;

  // ---- 系統管理 (HR01 SystemAdmin) ----
  if (apiPath === 'system/parameters' || apiPath === 'system/params') return {
    items: [
      { paramId: 'sp-001', paramCode: 'MAX_LOGIN_ATTEMPTS', paramName: '最大登入失敗次數', paramValue: '5', description: '超過此次數帳號自動鎖定', updatedAt: '2026-01-01' },
      { paramId: 'sp-002', paramCode: 'PASSWORD_EXPIRE_DAYS', paramName: '密碼有效天數', paramValue: '90', description: '密碼到期需強制變更', updatedAt: '2026-01-01' },
      { paramId: 'sp-003', paramCode: 'SESSION_TIMEOUT', paramName: '閒置逾時(分)', paramValue: '30', description: '閒置超過此時間自動登出', updatedAt: '2026-01-01' },
    ],
    totalElements: 3,
  };
  if (apiPath === 'system/features' || apiPath === 'system/toggles') return {
    items: [
      { featureId: 'ft-001', featureCode: 'ENABLE_SSO', featureName: 'SSO 單一登入', enabled: true, description: '啟用企業 SSO 整合' },
      { featureId: 'ft-002', featureCode: 'ENABLE_2FA', featureName: '雙因素驗證', enabled: false, description: '啟用 TOTP 雙因素驗證' },
      { featureId: 'ft-003', featureCode: 'ENABLE_LEAVE_AUTO_APPROVE', featureName: '請假自動核准', enabled: false, description: '3天以下請假自動核准' },
    ],
  };
  if (apiPath === 'system/jobs' || apiPath === 'system/schedules') return {
    items: [
      { jobId: 'job-s-001', jobName: '每日出勤統計', cronExpression: '0 0 1 * * ?', lastRunAt: '2026-03-18T01:00:00', nextRunAt: '2026-03-19T01:00:00', status: 'ACTIVE' },
      { jobId: 'job-s-002', jobName: '月結薪資計算', cronExpression: '0 0 2 1 * ?', lastRunAt: '2026-03-01T02:00:00', nextRunAt: '2026-04-01T02:00:00', status: 'ACTIVE' },
    ],
  };

  // ---- Profile 頁面 ----
  if (apiPath === 'profile' || apiPath === 'profile/me') return {
    userId: 'u001', username: 'admin', email: 'admin@company.com',
    displayName: 'System Admin', status: 'ACTIVE',
  };

  // ---- 預設：回傳空資料但帶有各種可能欄位 ----
  return {
    data: [], items: [], content: [],
    total: 0, totalElements: 0, success: true,
    unreadCount: 0, unread_count: 0,
  };
}
