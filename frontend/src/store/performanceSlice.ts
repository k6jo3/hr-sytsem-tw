import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

/**
 * Performance State (績效管理狀態)
 * Domain Code: HR08
 */
export interface PerformanceState {
  loading: boolean;
  error: string | null;

}

const initialState: PerformanceState = {
  loading: false,
  error: null,
};

/**
 * Performance Slice (績效管理 Redux Slice)
 */
export const performanceSlice = createSlice({
  name: 'performance',
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

export const performanceActions = performanceSlice.actions;
export default performanceSlice.reducer;
