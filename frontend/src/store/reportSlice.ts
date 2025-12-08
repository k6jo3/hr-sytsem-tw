import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

/**
 * Report State (報表分析狀態)
 * Domain Code: HR14
 */
export interface ReportState {
  loading: boolean;
  error: string | null;
  // TODO: Add state properties
}

const initialState: ReportState = {
  loading: false,
  error: null,
};

/**
 * Report Slice (報表分析 Redux Slice)
 */
export const reportSlice = createSlice({
  name: 'report',
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

export const reportActions = reportSlice.actions;
export default reportSlice.reducer;
