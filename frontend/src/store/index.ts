import { configureStore } from '@reduxjs/toolkit';
import { authSlice } from './authSlice';
import { organizationSlice } from './organizationSlice';
import attendanceReducer from './attendanceSlice';
import payrollReducer from './payrollSlice';
import insuranceReducer from './insuranceSlice';
import projectReducer from './projectSlice';
import timesheetReducer from './timesheetSlice';
import performanceReducer from './performanceSlice';
import recruitmentReducer from './recruitmentSlice';
import trainingReducer from './trainingSlice';
import workflowReducer from './workflowSlice';
import notificationReducer from './notificationSlice';
import documentReducer from './documentSlice';
import reportReducer from './reportSlice';

/**
 * Redux Store 配置
 * 整合所有 Feature Slices
 */
export const store = configureStore({
  reducer: {
    auth: authSlice.reducer,
    organization: organizationSlice.reducer,
    attendance: attendanceReducer,
    payroll: payrollReducer,
    insurance: insuranceReducer,
    project: projectReducer,
    timesheet: timesheetReducer,
    performance: performanceReducer,
    recruitment: recruitmentReducer,
    training: trainingReducer,
    workflow: workflowReducer,
    notification: notificationReducer,
    document: documentReducer,
    report: reportReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        // 忽略特定 action 的序列化檢查
        ignoredActions: ['persist/PERSIST'],
      },
    }),
  devTools: import.meta.env.DEV,
});

// 從 store 推導 RootState 與 AppDispatch 類型
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;
