import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

/**
 * Document State (文件管理狀態)
 * Domain Code: HR13
 */
export interface DocumentState {
  loading: boolean;
  error: string | null;
  // TODO: Add state properties
}

const initialState: DocumentState = {
  loading: false,
  error: null,
};

/**
 * Document Slice (文件管理 Redux Slice)
 */
export const documentSlice = createSlice({
  name: 'document',
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

export const documentActions = documentSlice.actions;
export default documentSlice.reducer;
