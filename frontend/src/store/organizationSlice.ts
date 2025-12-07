import { createSlice, PayloadAction } from '@reduxjs/toolkit';

/**
 * 員工摘要介面 (列表用)
 */
export interface EmployeeSummary {
  id: string;
  employeeNo: string;
  fullName: string;
  departmentName: string;
  positionName: string;
  status: string;
}

/**
 * 組織狀態介面
 */
export interface OrganizationState {
  /** 員工列表 */
  employees: EmployeeSummary[];
  /** 選中的員工 ID */
  selectedEmployeeId: string | null;
  /** 載入中狀態 */
  isLoading: boolean;
  /** 錯誤訊息 */
  error: string | null;
}

const initialState: OrganizationState = {
  employees: [],
  selectedEmployeeId: null,
  isLoading: false,
  error: null,
};

/**
 * 組織員工管理 Slice
 */
export const organizationSlice = createSlice({
  name: 'organization',
  initialState,
  reducers: {
    /** 開始載入員工列表 */
    fetchEmployeesStart: (state) => {
      state.isLoading = true;
      state.error = null;
    },
    /** 載入員工列表成功 */
    fetchEmployeesSuccess: (state, action: PayloadAction<EmployeeSummary[]>) => {
      state.isLoading = false;
      state.employees = action.payload;
    },
    /** 載入員工列表失敗 */
    fetchEmployeesFailure: (state, action: PayloadAction<string>) => {
      state.isLoading = false;
      state.error = action.payload;
    },
    /** 選擇員工 */
    selectEmployee: (state, action: PayloadAction<string>) => {
      state.selectedEmployeeId = action.payload;
    },
    /** 清除選擇 */
    clearSelection: (state) => {
      state.selectedEmployeeId = null;
    },
  },
});

export const {
  fetchEmployeesStart,
  fetchEmployeesSuccess,
  fetchEmployeesFailure,
  selectEmployee,
  clearSelection,
} = organizationSlice.actions;

export default organizationSlice.reducer;
