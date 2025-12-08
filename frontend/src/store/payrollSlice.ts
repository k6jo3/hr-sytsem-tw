import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

/**
 * Payroll State (薪資管理狀態)
 * Domain Code: HR04
 */
export interface PayrollState {
  loading: boolean;
  error: string | null;
  // TODO: Add state properties
}

const initialState: PayrollState = {
  loading: false,
  error: null,
};

/**
 * Payroll Slice (薪資管理 Redux Slice)
 */
export const payrollSlice = createSlice({
  name: 'payroll',
  initialState,
  reducers: {
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
    },
    reset: () => initialState,
  },
});

export const payrollActions = payrollSlice.actions;
export default payrollSlice.reducer;
