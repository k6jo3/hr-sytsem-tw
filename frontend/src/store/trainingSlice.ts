import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

/**
 * Training State (訓練管理狀態)
 * Domain Code: HR10
 */
export interface TrainingState {
  loading: boolean;
  error: string | null;
  // TODO: Add state properties
}

const initialState: TrainingState = {
  loading: false,
  error: null,
};

/**
 * Training Slice (訓練管理 Redux Slice)
 */
export const trainingSlice = createSlice({
  name: 'training',
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

export const trainingActions = trainingSlice.actions;
export default trainingSlice.reducer;
