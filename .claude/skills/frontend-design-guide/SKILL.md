# Frontend Design Guide Skill

## 使用時機
開發前端頁面、元件、API 層、Factory、Hook 時，用此 Skill 確認設計規範與慣例。
觸發關鍵字：`/frontend-design-guide`、「前端設計」、「前端規範」

> **本文件會持續更新**，反映最新的前端設計決策與調整。
> 最後更新：2026-03-14

---

## 1. 目錄結構

```
frontend/src/
├── pages/                          # 頁面元件（路由入口）
│   └── HR{DD}{FeatureName}Page.tsx
├── features/{feature}/             # 功能模組（14 個）
│   ├── api/
│   │   ├── {Feature}Api.ts         # API 呼叫 + Adapter
│   │   ├── {Feature}Types.ts       # DTO / Request / Response 型別
│   │   ├── Mock{Feature}Api.ts     # Mock 實作
│   │   └── {Feature}Api.adapter.test.ts
│   ├── components/                 # UI 元件
│   ├── factory/                    # DTO → ViewModel 轉換
│   ├── hooks/                      # Custom Hooks
│   └── model/                      # ViewModel 型別
├── shared/
│   ├── api/apiClient.ts            # Axios 封裝
│   ├── utils/adapterGuard.ts       # guardEnum / guardRequired
│   └── components/                 # 共用元件（ProtectedRoute 等）
├── store/                          # Redux Toolkit
│   ├── index.ts                    # configureStore
│   ├── hooks.ts                    # useAppDispatch / useAppSelector
│   └── {feature}Slice.ts           # 各功能 Slice
└── config/MockConfig.ts            # Mock 開關
```

---

## 2. 命名規範

| 元素 | 格式 | 範例 |
|:---|:---|:---|
| 頁面 | `HR{DD}{FeatureName}Page.tsx` | `HR01LoginPage.tsx` |
| 元件 | `{ComponentName}.tsx` | `UserFormModal.tsx` |
| API 模組 | `{Feature}Api.ts` | `AttendanceApi.ts` |
| Mock API | `Mock{Feature}Api.ts` | `MockAttendanceApi.ts` |
| 型別定義 | `{Feature}Types.ts` | `AttendanceTypes.ts` |
| Factory | `{ViewModel}Factory.ts` | `AttendanceViewModelFactory.ts` |
| Hook | `use{Name}.ts` | `useAttendance.ts` |
| Adapter 測試 | `{Feature}Api.adapter.test.ts` | `AttendanceApi.adapter.test.ts` |
| Factory 測試 | `{Factory}.test.ts` | `AttendanceViewModelFactory.test.ts` |
| Redux Slice | `{feature}Slice.ts` | `authSlice.ts` |

---

## 3. API 層設計

### 3.1 Adapter 模式（強制）

後端 camelCase → 前端 snake_case，**禁止直接將後端回應傳給元件**。

```typescript
function adaptEmployeeItem(raw: any): EmployeeDto {
  return {
    id: raw.employeeId ?? raw.id,
    full_name: raw.fullName ?? raw.full_name ?? '',
    status: guardEnum('employee.status', backendStatus, EMPLOYEE_STATUS_VALUES, 'ACTIVE'),
  };
}
```

#### 欄位 Fallback Chain

後端欄位名可能不一致（`name` vs `organizationName`），用 `??` 鏈式映射：

```typescript
organizationName: raw.name ?? raw.organizationName ?? '',
phoneNumber: raw.phone ?? raw.phoneNumber,
parentOrganizationId: raw.parentId ?? raw.parentOrganizationId,
```

### 3.2 guardEnum（禁止靜默 fallback）

```typescript
import { guardEnum } from '@shared/utils/adapterGuard';

// 未知值 → console.warn + 回傳原始值（不靜默替換）
status: guardEnum('employee.status', raw.status, ['ACTIVE', 'INACTIVE', 'ON_LEAVE'], 'ACTIVE')
```

**規則**：
- `null / undefined` → 回傳 fallback（合理預設）
- 未知值（如 `'PROBATION'`）→ `console.warn` + 回傳原始值（不丟棄資訊）
- **禁止** `dto.status || 'ACTIVE'` 這類靜默轉換

### 3.3 Enum 映射（後端舊值相容）

```typescript
const OVERTIME_TYPE_MAP: Record<string, OvertimeType> = {
  WEEKDAY: 'WORKDAY',    // 舊值 → 新值
  WEEKEND: 'REST_DAY',
};

export function adaptOvertimeType(raw: string): OvertimeType {
  const mapped = OVERTIME_TYPE_MAP[raw];
  if (mapped) return mapped;
  return guardEnum('overtime.type', raw, ['WORKDAY', 'REST_DAY', 'HOLIDAY'], 'WORKDAY');
}
```

### 3.4 Mock 開關

```typescript
export const OrganizationApi = {
  getEmployeeList: async (params?) => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.getEmployeeList(params);
    const raw = await apiClient.get('/employees', { params });
    return adaptEmployeeListResponse(raw);
  },
};
```

### 3.5 API 送出時的欄位轉換

前端 → 後端也需要映射（不能直接送前端命名）：

```typescript
createOrganization: (data: OrganizationRequest) => {
  return apiClient.post('/organizations', {
    code: data.organizationCode,
    name: data.organizationName,
    type: data.organizationType,
    parentId: data.parentOrganizationId,
    phone: data.phoneNumber,
  });
},
```

---

## 4. Factory 模式（強制）

**禁止元件直接使用 API DTO**，必須經過 Factory 轉換為 ViewModel。

```typescript
export class AttendanceViewModelFactory {
  static createFromDTO(dto: AttendanceRecordDto): AttendanceRecordViewModel {
    return {
      id: dto.id,
      checkTypeLabel: this.mapCheckTypeLabel(dto.checkType),  // CHECK_IN → "上班打卡"
      checkTypeColor: this.mapCheckTypeColor(dto.checkType),  // CHECK_IN → "blue"
      statusLabel: this.mapStatusLabel(dto.status),           // LATE → "遲到"
      statusColor: this.mapStatusColor(dto.status),           // LATE → "warning"
      checkTimeDisplay: this.formatTimeDisplay(dto.checkTime), // ISO → "09:00"
      isNormal: dto.status === 'NORMAL',
    };
  }

  static createListFromDTOs(dtos: AttendanceRecordDto[]): AttendanceRecordViewModel[] {
    return dtos.map(dto => this.createFromDTO(dto));
  }
}
```

**Factory 職責**：
- 中文標籤映射（enum → 顯示文字）
- 顏色映射（status → Ant Design color）
- 日期/時間格式化
- 衍生欄位計算（canCheckIn、canCheckOut）

---

## 5. 元件設計模式

### 5.1 表單 Modal（建立/編輯雙模式）

```typescript
interface Props {
  open: boolean;
  loading: boolean;
  editData: SomeDto | null;  // null = 建立模式, 有值 = 編輯模式
  onSubmit: (values: any) => void;
  onCancel: () => void;
}

const SomeFormModal: React.FC<Props> = ({ open, loading, editData, onSubmit, onCancel }) => {
  const [form] = Form.useForm();
  const isEdit = !!editData;

  useEffect(() => {
    if (open) {
      if (editData) {
        form.setFieldsValue({ ...editData });  // 編輯：填入現有資料
      } else {
        form.resetFields();                     // 建立：重置表單
      }
    }
  }, [open, editData]);

  return (
    <Modal
      title={isEdit ? '編輯XXX' : '建立XXX'}
      open={open}
      onOk={() => form.submit()}
      confirmLoading={loading}
      okText={isEdit ? '儲存' : '建立'}
      onCancel={onCancel}
    >
      <Form form={form} layout="vertical" onFinish={onSubmit}>
        <Form.Item name="code" label="代碼">
          <Input disabled={isEdit} />  {/* 建立後不可改的欄位 */}
        </Form.Item>
        <Form.Item name="name" label="名稱">
          <Input />
        </Form.Item>
      </Form>
    </Modal>
  );
};
```

### 5.2 概況面板（含操作按鈕）

```typescript
<Card
  title="XXX概況"
  bordered={false}
  extra={
    <div style={{ display: 'flex', gap: 8 }}>
      <Button size="small" icon={<EditOutlined />} onClick={handleEdit}>編輯</Button>
      {data.status === 'ACTIVE' && (
        <Button size="small" danger icon={<StopOutlined />} onClick={handleDeactivate}>停用</Button>
      )}
    </div>
  }
>
  <Statistic title="名稱" value={data.name} prefix={<BankOutlined />} />
  <div style={{ marginTop: 16 }}>
    <Statistic
      title="狀態"
      value={data.status === 'ACTIVE' ? '運作中' : '已停用'}
      valueStyle={{ color: data.status === 'ACTIVE' ? '#3f8600' : '#cf1322' }}
    />
  </div>
</Card>
```

### 5.3 停用確認對話框

```typescript
const handleDeactivate = () => {
  Modal.confirm({
    title: `確認停用「${data.name}」？`,
    content: '停用後將無法使用。若仍有在職員工，將無法停用。',
    okType: 'danger',
    okText: '確認停用',
    cancelText: '取消',
    onOk: async () => {
      try {
        await SomeApi.deactivate(data.id);
        message.success('已停用');
        refresh();
      } catch (err: any) {
        message.error(err?.message || '停用失敗');
      }
    },
  });
};
```

### 5.4 條件欄位（Form.useWatch）

```typescript
const [form] = Form.useForm();
const typeValue = Form.useWatch('type', form);

// 當 type 為 SUBSIDIARY 時，才顯示母公司選擇器
{typeValue === 'SUBSIDIARY' && (
  <Form.Item name="parentId" label="所屬母公司" rules={[{ required: true }]}>
    <Select placeholder="選擇母公司">
      {parentOptions.map(p => (
        <Select.Option key={p.id} value={p.id}>{p.name}</Select.Option>
      ))}
    </Select>
  </Form.Item>
)}
```

### 5.5 右鍵選單（樹狀結構節點）

```typescript
const renderNodeTitle = (node: DataNode) => {
  const data = (node as any).data as DeptDto | undefined;
  const isRoot = !data;

  return (
    <Dropdown
      menu={{
        items: isRoot
          ? [{ key: 'add', label: '新增一級部門', icon: <PlusOutlined /> }]
          : [
              { key: 'add-sub', label: '新增子部門', icon: <PlusOutlined /> },
              { key: 'edit', label: '編輯', icon: <EditOutlined /> },
              { key: 'delete', label: '刪除', danger: true, icon: <DeleteOutlined /> },
            ],
      }}
      trigger={['contextMenu']}
    >
      <div style={{ display: 'inline-block', width: '100%' }}>{node.title}</div>
    </Dropdown>
  );
};
```

### 5.6 分組下拉選單（母子公司）

```typescript
const renderOrgSelect = () => {
  const parents = orgList.filter(o => o.type === 'PARENT');
  const subsidiaries = orgList.filter(o => o.type === 'SUBSIDIARY');

  return (
    <Select value={selectedId} onChange={handleChange}>
      {parents.map(parent => {
        const children = subsidiaries.filter(s => s.parentId === parent.id);
        if (children.length === 0) {
          return <Select.Option key={parent.id} value={parent.id}>{parent.name}（母公司）</Select.Option>;
        }
        return (
          <Select.OptGroup key={parent.id} label={`${parent.name}（母公司）`}>
            <Select.Option key={parent.id} value={parent.id}>{parent.name}</Select.Option>
            {children.map(child => (
              <Select.Option key={child.id} value={child.id}>&nbsp;&nbsp;└ {child.name}</Select.Option>
            ))}
          </Select.OptGroup>
        );
      })}
    </Select>
  );
};
```

---

## 6. Hook 設計模式

### 6.1 資料取得 Hook

```typescript
export const useAttendance = () => {
  const [summary, setSummary] = useState<TodaySummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await AttendanceApi.getTodayAttendance();
      const vm = AttendanceViewModelFactory.createTodaySummary(response);
      setSummary(vm);
    } catch (err) {
      setError(err instanceof Error ? err.message : '取得資料失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleAction = useCallback(async (type: string) => {
    try {
      await AttendanceApi.doAction(type);
      await fetchData();  // 操作後重新整理
    } catch (err) { throw err; }
  }, [fetchData]);

  return { summary, loading, error, handleAction, refresh: fetchData };
};
```

### 6.2 Redux 整合 Hook

```typescript
export const useLogin = () => {
  const dispatch = useAppDispatch();
  const { isLoading, error, user } = useAppSelector(state => state.auth);

  const login = async (data: LoginFormData) => {
    dispatch(loginStart());
    try {
      const response = await AuthApi.login(data);
      const vm = UserViewModelFactory.createFromDTO(response.user);
      dispatch(loginSuccess({ user: vm, token: response.access_token }));
      return vm;
    } catch (err) {
      dispatch(loginFailure(err instanceof Error ? err.message : '登入失敗'));
      throw err;
    }
  };

  return { loading: isLoading, error, user, login, logout };
};
```

---

## 7. 錯誤處理

### 7.1 API Client（全域攔截）

```
400 → '請求資料驗證失敗'
401 → 導向 /login（非登入 API 時）
403 → '權限不足'
404 → '找不到資源'
409 → '資料已存在'
500 → '伺服器內部錯誤'
```

### 7.2 元件層

```typescript
try {
  await Api.doSomething(values);
  message.success('操作成功');
} catch (err: any) {
  const msg = err?.message || '操作失敗';
  message.error(msg);
}
```

### 7.3 刪除/停用操作

- **必須** 使用 `Modal.confirm` 二次確認
- 顯示被刪除/停用項目的名稱
- 提示可能影響（如：下屬員工失去歸屬）

---

## 8. 狀態管理

- **Redux**：僅全域狀態（auth、token），persist 到 localStorage
- **元件 state**：Modal 開關、loading、error、表單暫存
- **Hook state**：功能模組的資料取得結果（summary、list）
- **Form state**：Ant Design `Form.useForm()` 管理表單欄位

---

## 9. 路由

- Code splitting：`React.lazy` + `Suspense`
- 權限控制：`<ProtectedRoute requiredRoles={['ADMIN']}>`
- 預設導向 `/login`

---

## 10. 測試規範

### Adapter 測試必測項目
- 正常回傳的欄位映射
- 缺失欄位（`undefined` / `null`）的 fallback
- 未知 enum 值是否觸發 `console.warn`
- 空陣列 / 空物件
- 分頁回應的 total / page 映射

### Factory 測試必測項目
- enum → 中文標籤映射
- enum → 顏色映射
- 日期格式化
- 衍生欄位計算（如 canCheckIn）
- 邊界值（null、undefined、空字串）

---

## 11. UI 框架

- **元件庫**：Ant Design 5（Button、Table、Form、Modal、Card、Statistic、Tree、Select、Alert、Spin）
- **圖表**：ECharts
- **語系**：ConfigProvider + zhTW locale
- **樣式**：inline style 為主（`style={{ marginTop: 16 }}`），不使用 CSS Module

---

## 12. 設計原則

1. **Adapter-First** — 後端回應必經 Adapter 轉換，不直接給元件
2. **Factory-Required** — 元件使用 ViewModel，不使用 DTO
3. **No Silent Fallback** — 禁止 `|| 'DEFAULT'`，用 guardEnum 發出 warn
4. **Dual-Mode Form** — `isEdit = !!editData` 決定建立/編輯模式
5. **Mock-First Dev** — 每個 API 有 MockApi，MockConfig 控制開關
6. **Three-Way Consistency** — 後端 DTO ↔ 合約 requiredFields ↔ 前端 Types 三方一致
7. **Confirm Before Destroy** — 刪除/停用操作必須 Modal.confirm 二次確認
