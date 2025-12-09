import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

/**
 * Workflow State (簽核流程狀態)
 * Domain Code: HR11
 */
export interface WorkflowState {
  loading: boolean;
  error: string | null;
  // TODO: Add state properties
}

const initialState: WorkflowState = {
  loading: false,
  error: null,
};

/**
 * Workflow Slice (簽核流程 Redux Slice)
 */
export const workflowSlice = createSlice({
  name: 'workflow',
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

export const workflowActions = workflowSlice.actions;
export default workflowSlice.reducer;
