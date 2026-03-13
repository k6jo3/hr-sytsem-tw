// @ts-nocheck
/**
 * OrganizationApi Adapter 測試
 *
 * 三向一致性稽核：後端 DTO ↔ 合約 requiredFields ↔ 前端 adapter 映射
 *
 * 測試涵蓋：
 *   1. adaptEmployeeItem   — 後端 EmployeeListItemResponse → 前端 EmployeeDto
 *   2. adaptEmployeeListResponse — 後端 EmployeeListResponse 分頁包裝
 *   3. adaptOrganizations  — 後端 OrganizationListItemResponse → 前端 OrganizationDto
 *   4. adaptOrganizationTree — 後端 OrganizationTreeResponse → { data, departments }
 *
 * 發現的不一致問題（三向稽核結果）請參閱檔案底部的 MISMATCH REPORT 區段
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';

// ─────────────────────────────────────────────
// 模擬 MockConfig，確保測試強制走真實 adapter 路徑
// vi.mock 會被 hoisting，factory 中不可引用外部變數
// ─────────────────────────────────────────────
vi.mock('../../../config/MockConfig', () => ({
  MockConfig: { isEnabled: () => false },
}));

// ─────────────────────────────────────────────
// 模擬 apiClient：使用 vi.hoisted 確保 mockGet 在 hoisting 後仍可被引用
// ─────────────────────────────────────────────
const { mockGet } = vi.hoisted(() => ({
  mockGet: vi.fn(),
}));

vi.mock('../../../shared/api/apiClient', () => ({
  apiClient: { get: mockGet },
}));

import { OrganizationApi } from './OrganizationApi';

// ─────────────────────────────────────────────────────────────────────────────
// 測試固件：符合後端 EmployeeListItemResponse 格式的原始資料
// 後端欄位（Java camelCase）：
//   employeeId, employeeNumber, fullName, departmentId, departmentName,
//   departmentPath, positionId, email, status, statusDisplay,
//   jobTitle, employmentStatus, employmentStatusDisplayName,
//   hireDate, photoUrl
// ─────────────────────────────────────────────────────────────────────────────
const RAW_EMPLOYEE_ITEM = {
  employeeId: 'e0000001-0001-0001-0001-000000000001',
  employeeNumber: 'EMP001',
  fullName: '王小明',
  departmentId: 'd0000001-0001-0001-0001-000000000001',
  departmentName: '研發部',
  departmentPath: '總公司 / 研發部',
  positionId: 'p001',
  email: 'wang@company.com',       // 後端欄位：email
  status: 'ACTIVE',                // 後端冗餘欄位
  statusDisplay: '在職',
  jobTitle: '軟體工程師',
  employmentStatus: 'ACTIVE',      // 合約 requiredField：employmentStatus
  employmentStatusDisplayName: '在職',
  hireDate: '2024-01-15',
  photoUrl: 'https://cdn.company.com/photos/e001.jpg',
};

// 符合後端 EmployeeListResponse 包裝格式
const RAW_EMPLOYEE_LIST_RESPONSE = {
  items: [RAW_EMPLOYEE_ITEM],
  total: 1,
  page: 1,
  size: 20,
  totalPages: 1,
};

// 符合後端 OrganizationListItemResponse 格式
const RAW_ORGANIZATION_ITEM = {
  organizationId: '11111111-1111-1111-1111-111111111111',
  code: 'WU',
  name: '吳氏科技股份有限公司',
  type: 'PARENT',
  typeDisplay: '母公司',
  status: 'ACTIVE',
  parentId: null,
  parentName: null,
  employeeCount: 50,
  departmentCount: 8,
};

// 符合後端 OrganizationListResponse 包裝格式（後端使用 items，非 content）
const RAW_ORG_LIST_RESPONSE = {
  items: [RAW_ORGANIZATION_ITEM],
  totalCount: 1,
};

// 符合後端 OrganizationTreeResponse 格式
const RAW_ORG_TREE_RESPONSE = {
  organizationId: '11111111-1111-1111-1111-111111111111',
  code: 'WU',
  name: '吳氏科技',
  type: 'PARENT',
  status: 'ACTIVE',
  children: [],
  departments: [
    {
      departmentId: 'd0000001-0001-0001-0001-000000000001',
      code: 'RD',
      name: '研發部',
      level: 1,
      managerId: 'e0000002-0002-0002-0002-000000000002',
      managerName: '李主管',
      children: [],
    },
  ],
};

// ─────────────────────────────────────────────────────────────────────────────
// 1. adaptEmployeeItem 測試群組
// ─────────────────────────────────────────────────────────────────────────────
describe('adaptEmployeeItem — 後端 EmployeeListItemResponse → EmployeeDto', () => {
  beforeEach(() => {
    mockGet.mockReset();
    mockGet.mockResolvedValue(RAW_EMPLOYEE_LIST_RESPONSE);
  });

  it('【正常路徑】應正確映射所有標準欄位', async () => {
    const result = await OrganizationApi.getEmployeeList();
    const emp = result.employees[0];

    // 合約 ORG_QRY_E001 requiredFields: employeeId, employeeNumber, fullName, employmentStatus, hireDate
    expect(emp.id).toBe('e0000001-0001-0001-0001-000000000001');      // employeeId → id
    expect(emp.employee_number).toBe('EMP001');                        // employeeNumber → employee_number
    expect(emp.department_id).toBe('d0000001-0001-0001-0001-000000000001');
    expect(emp.department_name).toBe('研發部');
    expect(emp.position).toBe('軟體工程師');                           // jobTitle → position
    expect(emp.status).toBe('ACTIVE');                                 // employmentStatus → status
    expect(emp.hire_date).toBe('2024-01-15');
  });

  it('【欄位映射驗證】first_name 應映射自 fullName（非 firstName）', async () => {
    // 稽核發現：後端 EmployeeListItemResponse 沒有 firstName 欄位
    // adapter 將 fullName 映射到 first_name，last_name 則為空字串
    // 這是已知不一致：EmployeeDto.first_name 語意應為姓氏，但實際承載 fullName
    const result = await OrganizationApi.getEmployeeList();
    const emp = result.employees[0];

    expect(emp.first_name).toBe('王小明'); // 映射自 fullName（已知缺陷）
    expect(emp.last_name).toBe('');        // 後端列表回應無獨立 lastName，固定為空字串
  });

  it('【email 欄位】應優先讀取 companyEmail，fallback 至 email', async () => {
    // 後端 EmployeeListItemResponse 使用 email（非 companyEmail）
    // adapter 判斷順序：companyEmail ?? email
    const result = await OrganizationApi.getEmployeeList();
    const emp = result.employees[0];

    expect(emp.email).toBe('wang@company.com');
  });

  it('【email fallback】後端回傳 companyEmail 時應優先使用', async () => {
    mockGet.mockResolvedValue({
      ...RAW_EMPLOYEE_LIST_RESPONSE,
      items: [{
        ...RAW_EMPLOYEE_ITEM,
        companyEmail: 'wang.company@company.com',
        email: 'wang.personal@gmail.com',
      }],
    });
    const result = await OrganizationApi.getEmployeeList();
    expect(result.employees[0].email).toBe('wang.company@company.com');
  });

  it('【employmentStatus 優先】應優先使用 employmentStatus 而非 status', async () => {
    mockGet.mockResolvedValue({
      ...RAW_EMPLOYEE_LIST_RESPONSE,
      items: [{
        ...RAW_EMPLOYEE_ITEM,
        employmentStatus: 'PROBATION',
        status: 'ACTIVE', // 兩者不同，應以 employmentStatus 為準
      }],
    });
    const result = await OrganizationApi.getEmployeeList();
    expect(result.employees[0].status).toBe('PROBATION');
  });

  it('【null/undefined 欄位】缺少選擇性欄位時應使用合理預設值', async () => {
    mockGet.mockResolvedValue({
      items: [{
        employeeId: 'e-null-test',
        employeeNumber: 'EMP999',
        fullName: '測試員工',
        employmentStatus: 'ACTIVE',
        hireDate: '2025-01-01',
        // 以下欄位全部缺失
      }],
      total: 1,
      page: 1,
      size: 20,
    });
    const result = await OrganizationApi.getEmployeeList();
    const emp = result.employees[0];

    expect(emp.id).toBe('e-null-test');
    expect(emp.employee_number).toBe('EMP999');
    expect(emp.first_name).toBe('測試員工');
    expect(emp.last_name).toBe('');
    expect(emp.email).toBe('');
    expect(emp.phone).toBeUndefined();
    expect(emp.department_id).toBe('');
    expect(emp.department_name).toBe('');
    expect(emp.position).toBe('');
    expect(emp.termination_date).toBeUndefined();
    expect(emp.created_at).toBe('');
    expect(emp.updated_at).toBe('');
  });

  it('【id fallback】後端無 employeeId 時應 fallback 至 id 欄位', async () => {
    mockGet.mockResolvedValue({
      items: [{
        id: 'fallback-id-001',  // 使用舊欄位名
        employeeNumber: 'EMP999',
        fullName: '備援測試',
        employmentStatus: 'ACTIVE',
        hireDate: '2025-01-01',
      }],
      total: 1, page: 1, size: 20,
    });
    const result = await OrganizationApi.getEmployeeList();
    expect(result.employees[0].id).toBe('fallback-id-001');
  });

  it('【未知 enum 值】employmentStatus 為未知值時應發出警告並保留原始值', async () => {
    const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
    mockGet.mockResolvedValue({
      items: [{
        ...RAW_EMPLOYEE_ITEM,
        employmentStatus: 'SUSPENDED',  // 未知 enum 值
      }],
      total: 1, page: 1, size: 20,
    });

    const result = await OrganizationApi.getEmployeeList();

    // guardEnum 應發出警告
    expect(warnSpy).toHaveBeenCalledWith(
      expect.stringContaining('employee.status')
    );
    // 應保留原始值（guardEnum 的行為：unknown → 回傳原始值）
    expect(result.employees[0].status).toBe('SUSPENDED');

    warnSpy.mockRestore();
  });

  it('【null status】employmentStatus 為 null 時應 fallback 至 ACTIVE', async () => {
    mockGet.mockResolvedValue({
      items: [{
        ...RAW_EMPLOYEE_ITEM,
        employmentStatus: null,
        status: null,
      }],
      total: 1, page: 1, size: 20,
    });
    const result = await OrganizationApi.getEmployeeList();
    expect(result.employees[0].status).toBe('ACTIVE');
  });

  it('【terminationDate 映射】應映射 terminationDate 和 termination_date', async () => {
    mockGet.mockResolvedValue({
      items: [{
        ...RAW_EMPLOYEE_ITEM,
        terminationDate: '2026-03-31',
        employmentStatus: 'TERMINATED',
      }],
      total: 1, page: 1, size: 20,
    });
    const result = await OrganizationApi.getEmployeeList();
    expect(result.employees[0].termination_date).toBe('2026-03-31');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 2. adaptEmployeeListResponse 分頁包裝測試
// ─────────────────────────────────────────────────────────────────────────────
describe('adaptEmployeeListResponse — 分頁包裝映射', () => {
  it('【後端 items + total/page/size】應正確映射分頁欄位', async () => {
    mockGet.mockResolvedValue({
      items: [RAW_EMPLOYEE_ITEM],
      total: 100,
      page: 2,
      size: 20,
      totalPages: 5,
    });
    const result = await OrganizationApi.getEmployeeList();

    expect(result.total).toBe(100);
    expect(result.page).toBe(2);
    expect(result.page_size).toBe(20);   // size → page_size
    expect(result.employees).toHaveLength(1);
  });

  it('【稽核發現：後端欄位名不一致】後端使用 size 而前端型別期望 page_size', async () => {
    // 後端 EmployeeListResponse.size → 前端 GetEmployeeListResponse.page_size
    // adapter 已正確處理：raw.size ?? raw.page_size ?? 20
    mockGet.mockResolvedValue({
      items: [],
      total: 0,
      page: 1,
      size: 50, // 後端欄位
    });
    const result = await OrganizationApi.getEmployeeList();
    expect(result.page_size).toBe(50);
  });

  it('【Spring Data 格式】後端回傳 content/totalElements/number 格式時應正確映射', async () => {
    // Spring Data Page 物件格式
    mockGet.mockResolvedValue({
      content: [RAW_EMPLOYEE_ITEM],
      totalElements: 55,
      number: 3,
      size: 10,
    });
    const result = await OrganizationApi.getEmployeeList();

    expect(result.employees).toHaveLength(1);
    expect(result.total).toBe(55);
    expect(result.page).toBe(3);
    expect(result.page_size).toBe(10);
  });

  it('【空列表】items 為空陣列時應回傳空 employees', async () => {
    mockGet.mockResolvedValue({ items: [], total: 0, page: 1, size: 20 });
    const result = await OrganizationApi.getEmployeeList();
    expect(result.employees).toEqual([]);
    expect(result.total).toBe(0);
  });

  it('【缺少包裝】後端直接回傳陣列（employees 欄位）時應正確映射', async () => {
    mockGet.mockResolvedValue({
      employees: [RAW_EMPLOYEE_ITEM],
      total: 1,
      page: 1,
      size: 20,
    });
    const result = await OrganizationApi.getEmployeeList();
    expect(result.employees).toHaveLength(1);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 3. getOrganizations — OrganizationListItemResponse → OrganizationDto
// ─────────────────────────────────────────────────────────────────────────────
describe('getOrganizations — 後端 OrganizationListItemResponse → OrganizationDto', () => {
  beforeEach(() => {
    mockGet.mockReset();
    mockGet.mockResolvedValue(RAW_ORG_LIST_RESPONSE);
  });

  it('【正常路徑】應正確映射合約 ORG_QRY_O001 requiredFields', async () => {
    const result = await OrganizationApi.getOrganizations();
    const org = result.content[0];

    // 合約 requiredFields: organizationId, code→organizationCode, name→organizationName,
    //                      type→organizationType, parentId→parentOrganizationId, status
    expect(org.organizationId).toBe('11111111-1111-1111-1111-111111111111'); // 同名映射
    expect(org.organizationCode).toBe('WU');             // code → organizationCode
    expect(org.organizationName).toBe('吳氏科技股份有限公司'); // name → organizationName
    expect(org.organizationType).toBe('PARENT');          // type → organizationType
    // 稽核發現 M9：後端 parentId 為 null 時，??運算子會繼續求值右邊的 o.parentOrganizationId
    // 兩者皆 null/undefined → 結果為 undefined（非 null）
    // 合約 ORG_QRY_O001 requiredFields parentId notNull:false，允許缺失
    expect(org.parentOrganizationId).toBeUndefined();    // parentId=null ?? parentOrganizationId=undefined → undefined
    expect(org.status).toBe('ACTIVE');
  });

  it('【稽核發現：後端使用 items 非 content】adapter 應讀取 items 欄位', async () => {
    // 後端 OrganizationListResponse 使用 items（非 content）
    // 但 getOrganizations adapter 第 112 行：raw.content ?? raw.items
    // 合約 ORG_QRY_O001 dataPath 也是 "items"
    // 若後端只回傳 items，adapter 仍可正常工作
    mockGet.mockResolvedValue({ items: [RAW_ORGANIZATION_ITEM], totalCount: 1 });
    const result = await OrganizationApi.getOrganizations();
    expect(result.content).toHaveLength(1);
  });

  it('【organizationType fallback】非 PARENT 的 type 值應映射為 SUBSIDIARY', async () => {
    mockGet.mockResolvedValue({
      items: [{ ...RAW_ORGANIZATION_ITEM, type: 'SUBSIDIARY' }],
    });
    const result = await OrganizationApi.getOrganizations();
    expect(result.content[0].organizationType).toBe('SUBSIDIARY');
  });

  it('【未知 organizationType】type 為未知值時應 fallback 為 SUBSIDIARY', async () => {
    mockGet.mockResolvedValue({
      items: [{ ...RAW_ORGANIZATION_ITEM, type: 'BRANCH' }],
    });
    const result = await OrganizationApi.getOrganizations();
    // adapter 邏輯：o.type === 'PARENT' ? 'PARENT' : 'SUBSIDIARY'
    // 任何非 PARENT 的值都變成 SUBSIDIARY（包括未知值）
    // 稽核發現：此實作遺失未知值警告，與 guardEnum 行為不一致
    expect(result.content[0].organizationType).toBe('SUBSIDIARY');
  });

  it('【未知 status】status 為未知值時應發出警告', async () => {
    const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
    mockGet.mockResolvedValue({
      items: [{ ...RAW_ORGANIZATION_ITEM, status: 'SUSPENDED' }],
    });

    await OrganizationApi.getOrganizations();

    expect(warnSpy).toHaveBeenCalledWith(
      expect.stringContaining('organization.status')
    );
    warnSpy.mockRestore();
  });

  it('【null status】status 為 null 時應 fallback 為 ACTIVE', async () => {
    mockGet.mockResolvedValue({
      items: [{ ...RAW_ORGANIZATION_ITEM, status: null }],
    });
    const result = await OrganizationApi.getOrganizations();
    expect(result.content[0].status).toBe('ACTIVE');
  });

  it('【稽核發現：OrganizationDto 缺少後端欄位】typeDisplay/parentName/departmentCount 未映射', async () => {
    // 後端 OrganizationListItemResponse 有 typeDisplay, parentName, departmentCount
    // 但前端 OrganizationDto 介面沒有這些欄位，adapter 也未映射
    // 這是已知缺口，若前端需要顯示組織類型中文名稱，需新增此欄位
    const result = await OrganizationApi.getOrganizations();
    const org = result.content[0] as any;

    // 確認未映射的欄位不存在於輸出
    expect(org.typeDisplay).toBeUndefined();
    expect(org.parentName).toBeUndefined();
    expect(org.departmentCount).toBeUndefined();
  });

  it('【employeeCount 映射】應正確映射 employeeCount', async () => {
    const result = await OrganizationApi.getOrganizations();
    expect(result.content[0].employeeCount).toBe(50);
  });

  it('【稽核發現：createdAt 缺失】OrganizationListItemResponse 無 createdAt 欄位', async () => {
    // 後端 OrganizationListItemResponse 沒有 createdAt 欄位
    // 但 adapter 要求 OrganizationDto.createdAt：raw.createdAt ?? ''
    // 列表查詢時 createdAt 永遠為空字串
    const result = await OrganizationApi.getOrganizations();
    expect(result.content[0].createdAt).toBe('');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 4. getOrganizationTree — OrganizationTreeResponse → { data, departments }
// ─────────────────────────────────────────────────────────────────────────────
describe('getOrganizationTree — 後端 OrganizationTreeResponse → { data, departments }', () => {
  beforeEach(() => {
    mockGet.mockReset();
    mockGet.mockResolvedValue(RAW_ORG_TREE_RESPONSE);
  });

  it('【正常路徑】應正確重組為 { data: OrganizationDto, departments: DepartmentDto[] }', async () => {
    const result = await OrganizationApi.getOrganizationTree('11111111-1111-1111-1111-111111111111');

    expect(result.data).toBeDefined();
    expect(result.departments).toBeDefined();
    expect(Array.isArray(result.departments)).toBe(true);
  });

  it('【合約 ORG_QRY_O002 requiredFields】organizationId/code/name/type/status 應正確映射', async () => {
    const result = await OrganizationApi.getOrganizationTree('11111111-1111-1111-1111-111111111111');
    const org = result.data;

    expect(org.organizationId).toBe('11111111-1111-1111-1111-111111111111');
    expect(org.organizationCode).toBe('WU');     // code → organizationCode
    expect(org.organizationName).toBe('吳氏科技'); // name → organizationName
    expect(org.organizationType).toBe('PARENT'); // type → organizationType
    expect(org.status).toBe('ACTIVE');
  });

  it('【departments 映射】部門清單應正確映射 DepartmentTreeNode → DepartmentDto', async () => {
    const result = await OrganizationApi.getOrganizationTree('11111111-1111-1111-1111-111111111111');
    const dept = result.departments[0];

    expect(dept.departmentId).toBe('d0000001-0001-0001-0001-000000000001');
    expect(dept.code).toBe('RD');
    expect(dept.name).toBe('研發部');
    expect(dept.level).toBe(1);
    expect(dept.managerId).toBe('e0000002-0002-0002-0002-000000000002');
    expect(dept.managerName).toBe('李主管');
  });

  it('【稽核發現：DepartmentTreeNode 無 sortOrder 欄位】adapter fallback 應為 0', async () => {
    // 後端 OrganizationTreeResponse.DepartmentTreeNode 無 sortOrder/displayOrder
    // adapter 使用：d.sortOrder ?? d.displayOrder ?? 0
    const result = await OrganizationApi.getOrganizationTree('11111111-1111-1111-1111-111111111111');
    expect(result.departments[0].sortOrder).toBe(0);
  });

  it('【稽核發現：DepartmentTreeNode 無 organizationId 欄位】應 fallback 至 id 參數', async () => {
    // OrganizationTreeResponse.DepartmentTreeNode 沒有 organizationId 欄位
    // adapter 使用：d.organizationId ?? id（id 為函數參數）
    const result = await OrganizationApi.getOrganizationTree('11111111-1111-1111-1111-111111111111');
    expect(result.departments[0].organizationId).toBe('11111111-1111-1111-1111-111111111111');
  });

  it('【DepartmentTreeNode 使用 children 而非 subDepartments】應正確映射子部門', async () => {
    mockGet.mockResolvedValue({
      ...RAW_ORG_TREE_RESPONSE,
      departments: [{
        ...RAW_ORG_TREE_RESPONSE.departments[0],
        children: [
          { departmentId: 'd-sub-001', code: 'RD-FE', name: '前端組', level: 2 },
        ],
      }],
    });
    const result = await OrganizationApi.getOrganizationTree('11111111-1111-1111-1111-111111111111');
    // adapter 映射：d.subDepartments ?? d.children
    // 後端 DepartmentTreeNode 使用 children，應被正確讀取
    expect(result.departments[0].subDepartments).toHaveLength(1);
  });

  it('【空 departments】後端 departments 為 null 時應回傳空陣列', async () => {
    mockGet.mockResolvedValue({
      ...RAW_ORG_TREE_RESPONSE,
      departments: null,
    });
    const result = await OrganizationApi.getOrganizationTree('test-id');
    expect(result.departments).toEqual([]);
  });

  it('【organizationId fallback】後端使用舊欄位名 id 時應正確映射', async () => {
    mockGet.mockResolvedValue({
      id: 'legacy-id-001',  // 舊欄位名
      code: 'LEGACY',
      name: 'Legacy 組織',
      type: 'PARENT',
      status: 'ACTIVE',
      departments: [],
    });
    const result = await OrganizationApi.getOrganizationTree('legacy-id-001');
    expect(result.data.organizationId).toBe('legacy-id-001');
  });

  it('【未知 type】organizationType 為未知值時應 fallback 為 SUBSIDIARY', async () => {
    mockGet.mockResolvedValue({
      ...RAW_ORG_TREE_RESPONSE,
      type: 'HOLDING',
    });
    const result = await OrganizationApi.getOrganizationTree('test-id');
    // adapter：raw.type === 'PARENT' ? 'PARENT' : 'SUBSIDIARY'
    expect(result.data.organizationType).toBe('SUBSIDIARY');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 5. getOrganization（單一組織詳情）— 直接 pass-through 測試
// ─────────────────────────────────────────────────────────────────────────────
describe('getOrganization — 直接 pass-through（無 adapter）', () => {
  it('【稽核發現：無 adapter】getOrganization 直接回傳 apiClient 原始回應，未做欄位映射', async () => {
    // 後端 OrganizationDetailResponse 使用 code/name/type/phone（非語意化欄位名）
    // 但前端 OrganizationDto 期望 organizationCode/organizationName/organizationType/phoneNumber
    // getOrganization 目前沒有 adapter，直接 return apiClient.get<OrganizationDto>()
    // 這表示直接查詢單一組織時，欄位映射不會發生
    const rawDetailResponse = {
      organizationId: '11111111-1111-1111-1111-111111111111',
      code: 'WU',          // 後端欄位：OrganizationDetailResponse
      name: '吳氏科技',
      type: 'PARENT',
      phone: '02-12345678',  // 後端：phone；前端 OrganizationDto 期望：phoneNumber
      status: 'ACTIVE',
      taxId: '12345678',
      address: '台北市',
      establishedDate: '2020-01-01',
    };
    mockGet.mockResolvedValue(rawDetailResponse);

    const result = await OrganizationApi.getOrganization('11111111-1111-1111-1111-111111111111') as any;

    // pass-through：code 不會被映射為 organizationCode
    // 這是已知的不一致問題（見下方 MISMATCH REPORT #M5）
    expect(result.code).toBe('WU');
    expect(result.organizationCode).toBeUndefined();
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 6. getDepartments — 直接 pass-through 測試
// ─────────────────────────────────────────────────────────────────────────────
describe('getDepartments — 直接 pass-through（無 adapter）', () => {
  it('【稽核發現：無 adapter】後端 DepartmentListItemResponse 與前端 DepartmentDto 欄位名稱相符', async () => {
    // 後端 DepartmentListItemResponse 欄位名稱與前端 DepartmentDto 完全相符：
    //   departmentId, code, name, level, sortOrder, organizationId,
    //   parentId, managerId, managerName, status, statusDisplay, employeeCount
    // getDepartments 直接 pass-through 是安全的，因為欄位名稱完全一致
    const rawDeptResponse = {
      items: [{
        departmentId: 'd-001',
        code: 'RD',
        name: '研發部',
        level: 1,
        sortOrder: 1,
        organizationId: 'org-001',
        parentId: null,
        managerId: 'e-001',
        managerName: '李主管',
        status: 'ACTIVE',
        statusDisplay: '啟用',
        employeeCount: 10,
      }],
    };
    mockGet.mockResolvedValue(rawDeptResponse);

    const result = await OrganizationApi.getDepartments();
    // getDepartments 直接 pass-through，欄位名稱與 DepartmentDto 完全一致
    expect(result).toEqual(rawDeptResponse);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 7. 合約 requiredFields 完整覆蓋測試
// ─────────────────────────────────────────────────────────────────────────────
describe('合約 requiredFields 完整覆蓋', () => {
  it('【ORG_QRY_E001】所有 requiredFields 應存在於 EmployeeDto', async () => {
    mockGet.mockResolvedValue(RAW_EMPLOYEE_LIST_RESPONSE);
    const result = await OrganizationApi.getEmployeeList();
    const emp = result.employees[0];

    // requiredFields: employeeId, employeeNumber, fullName, departmentName, jobTitle, employmentStatus, hireDate
    expect(emp.id).toBeTruthy();          // employeeId → id
    expect(emp.employee_number).toBeTruthy(); // employeeNumber
    expect(emp.first_name).toBeTruthy();  // fullName → first_name（已知映射問題）
    expect(emp.department_name).toBeTruthy(); // departmentName
    expect(emp.position).toBeTruthy();   // jobTitle → position
    expect(emp.status).toBe('ACTIVE');   // employmentStatus → status
    expect(emp.hire_date).toBeTruthy();  // hireDate
  });

  it('【ORG_QRY_O001】所有 requiredFields 應存在於 OrganizationDto', async () => {
    mockGet.mockResolvedValue(RAW_ORG_LIST_RESPONSE);
    const result = await OrganizationApi.getOrganizations();
    const org = result.content[0];

    // requiredFields: organizationId, code, name, type, parentId, status
    expect(org.organizationId).toBeTruthy();
    expect(org.organizationCode).toBeTruthy();      // code → organizationCode
    expect(org.organizationName).toBeTruthy();      // name → organizationName
    expect(org.organizationType).toBeTruthy();      // type → organizationType
    // parentId → parentOrganizationId（允許 null）
    expect(org.status).toBeTruthy();
  });

  it('【ORG_QRY_O002】組織樹 requiredFields 應完整', async () => {
    mockGet.mockResolvedValue(RAW_ORG_TREE_RESPONSE);
    const result = await OrganizationApi.getOrganizationTree('11111111-1111-1111-1111-111111111111');

    // requiredFields: organizationId, code, name, type, status, departments
    expect(result.data.organizationId).toBeTruthy();
    expect(result.data.organizationCode).toBeTruthy();
    expect(result.data.organizationName).toBeTruthy();
    expect(result.data.organizationType).toBeTruthy();
    expect(result.data.status).toBeTruthy();
    expect(Array.isArray(result.departments)).toBe(true);
  });
});

/*
 * ─────────────────────────────────────────────────────────────────────────────
 * MISMATCH REPORT — 三向稽核發現的不一致問題
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * 稽核方法：後端 DTO 欄位 ↔ 合約 requiredFields ↔ 前端 adapter 映射
 *
 * ── M1【高】EmployeeDto.first_name 承載 fullName（語意錯誤）──
 *   後端 EmployeeListItemResponse：無 firstName 欄位，只有 fullName
 *   Adapter (第 28 行)：first_name: raw.fullName ?? raw.first_name ?? ''
 *   合約 requiredFields：fullName（notNull: true）
 *   問題：前端 EmployeeDto 用 first_name + last_name 模型，但列表 API 只回 fullName
 *         first_name 承載完整姓名，last_name 永遠為空字串
 *   建議：列表 DTO 新增 firstName/lastName，或前端改用 fullName 欄位
 *
 * ── M2【中】getOrganization 無 adapter（單一組織詳情）──
 *   後端 OrganizationDetailResponse：使用 code/name/type/phone
 *   前端 OrganizationDto 介面：期望 organizationCode/organizationName/organizationType/phoneNumber
 *   Adapter (第 131 行)：直接 return apiClient.get<OrganizationDto>()，無映射
 *   問題：欄位名稱不匹配，前端元件若讀取 organizationCode 會拿到 undefined
 *   建議：getOrganization 需加 adapter 函式，與 getOrganizations 一致
 *
 * ── M3【中】OrganizationDto 缺少後端提供的顯示欄位 ──
 *   後端 OrganizationListItemResponse 提供：typeDisplay, parentName, departmentCount
 *   前端 OrganizationDto 介面：無上述三個欄位
 *   Adapter：未映射這三個欄位
 *   問題：前端無法顯示組織類型中文名稱（typeDisplay），需額外 API 呼叫或硬編碼翻譯
 *   建議：OrganizationDto 新增 typeDisplay?: string 等選擇性欄位
 *
 * ── M4【中】OrganizationListItemResponse 無 createdAt 欄位 ──
 *   後端 OrganizationListItemResponse：無 createdAt 欄位（只有 OrganizationDetailResponse 才有）
 *   前端 OrganizationDto：createdAt: string（必要欄位）
 *   Adapter (第 121 行)：createdAt: o.createdAt ?? ''
 *   問題：列表查詢時 createdAt 永遠為空字串
 *   建議：OrganizationDto.createdAt 改為 createdAt?: string（選擇性）
 *
 * ── M5【中】DepartmentTreeNode 缺少多個 DepartmentDto 欄位 ──
 *   後端 OrganizationTreeResponse.DepartmentTreeNode 欄位：
 *     departmentId, code, name, level, managerId, managerName, children
 *   前端 DepartmentDto 必要欄位：sortOrder, organizationId, status, employeeCount
 *   問題：樹狀節點 DTO 欠缺 sortOrder/organizationId/status/employeeCount
 *         Adapter 用 fallback 值（0/id參數/'ACTIVE'/0），但這些不是真實資料
 *   建議：OrganizationTreeResponse.DepartmentTreeNode 補充缺失欄位
 *         或前端 DepartmentDto 將這些欄位改為選擇性
 *
 * ── M6【低】organizationType 使用三元判斷而非 guardEnum ──
 *   Adapter (第 117 行)：organizationType: (o.type ?? o.organizationType) === 'PARENT' ? 'PARENT' : 'SUBSIDIARY'
 *   問題：未知 type 值（如 'BRANCH'）靜默 fallback 為 'SUBSIDIARY'，不發出警告
 *         與 status 使用 guardEnum 的行為不一致
 *   建議：改用 guardEnum('organization.type', o.type, ['PARENT', 'SUBSIDIARY'], 'SUBSIDIARY')
 *
 * ── M7【低】合約 ORG_QRY_D001 requiredFields 與後端 DepartmentListItemResponse 欄位名不一致 ──
 *   合約 requiredFields：{ name: "code" }, { name: "name" }
 *   後端 DepartmentListItemResponse：code, name（相符）
 *   前端 DepartmentDto：code, name（相符）
 *   結論：此處三方一致，無問題
 *
 * ── M8【低】EmployeeListResponse.size 對應前端 GetEmployeeListResponse.page_size ──
 *   後端欄位名：size（非 pageSize）
 *   前端型別：page_size
 *   Adapter：raw.size ?? raw.page_size ?? 20（正確處理）
 *   合約：未明確規範分頁回應欄位名稱
 *   結論：Adapter 已正確處理，但命名不一致值得記錄
 * ─────────────────────────────────────────────────────────────────────────────
 */
