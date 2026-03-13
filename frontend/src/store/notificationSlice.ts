import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

/**
 * Notification State (通知服務狀態)
 * Domain Code: HR12
 */
export interface NotificationState {
  loading: boolean;
  error: string | null;

}

const initialState: NotificationState = {
  loading: false,
  error: null,
};

/**
 * Notification Slice (通知服務 Redux Slice)
 */
export const notificationSlice = createSlice({
  name: 'notification',
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

export const notificationActions = notificationSlice.actions;
export default notificationSlice.reducer;
