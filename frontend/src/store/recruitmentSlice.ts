import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

/**
 * Recruitment State (招募管理狀態)
 * Domain Code: HR09
 */
export interface RecruitmentState {
  loading: boolean;
  error: string | null;

}

const initialState: RecruitmentState = {
  loading: false,
  error: null,
};

/**
 * Recruitment Slice (招募管理 Redux Slice)
 */
export const recruitmentSlice = createSlice({
  name: 'recruitment',
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

export const recruitmentActions = recruitmentSlice.actions;
export default recruitmentSlice.reducer;
