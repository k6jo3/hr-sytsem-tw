import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

/**
 * Attendance State (考勤管理狀態)
 * Domain Code: HR03
 */
export interface AttendanceState {
  loading: boolean;
  error: string | null;
  // TODO: Add state properties
}

const initialState: AttendanceState = {
  loading: false,
  error: null,
};

/**
 * Attendance Slice (考勤管理 Redux Slice)
 */
export const attendanceSlice = createSlice({
  name: 'attendance',
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

export const attendanceActions = attendanceSlice.actions;
export default attendanceSlice.reducer;
