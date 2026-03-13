import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

/**
 * Insurance State (保險管理狀態)
 * Domain Code: HR05
 */
export interface InsuranceState {
  loading: boolean;
  error: string | null;

}

const initialState: InsuranceState = {
  loading: false,
  error: null,
};

/**
 * Insurance Slice (保險管理 Redux Slice)
 */
export const insuranceSlice = createSlice({
  name: 'insurance',
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

export const insuranceActions = insuranceSlice.actions;
export default insuranceSlice.reducer;
