import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

/**
 * Timesheet State (工時管理狀態)
 * Domain Code: HR07
 */
export interface TimesheetState {
  loading: boolean;
  error: string | null;

}

const initialState: TimesheetState = {
  loading: false,
  error: null,
};

/**
 * Timesheet Slice (工時管理 Redux Slice)
 */
export const timesheetSlice = createSlice({
  name: 'timesheet',
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

export const timesheetActions = timesheetSlice.actions;
export default timesheetSlice.reducer;
