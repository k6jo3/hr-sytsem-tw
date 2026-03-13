import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

/**
 * Project State (專案管理狀態)
 * Domain Code: HR06
 */
export interface ProjectState {
  loading: boolean;
  error: string | null;

}

const initialState: ProjectState = {
  loading: false,
  error: null,
};

/**
 * Project Slice (專案管理 Redux Slice)
 */
export const projectSlice = createSlice({
  name: 'project',
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

export const projectActions = projectSlice.actions;
export default projectSlice.reducer;
