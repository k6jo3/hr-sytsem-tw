import { configureStore } from '@reduxjs/toolkit';
import { authSlice } from './authSlice';
import { organizationSlice } from './organizationSlice';

/**
 * Redux Store 配置
 * 整合所有 Feature Slices
 */
export const store = configureStore({
  reducer: {
    auth: authSlice.reducer,
    organization: organizationSlice.reducer,
    // 未來新增其他 Feature Slices:
    // attendance: attendanceSlice.reducer,
    // payroll: payrollSlice.reducer,
    // insurance: insuranceSlice.reducer,
    // project: projectSlice.reducer,
    // timesheet: timesheetSlice.reducer,
    // performance: performanceSlice.reducer,
    // recruitment: recruitmentSlice.reducer,
    // training: trainingSlice.reducer,
    // workflow: workflowSlice.reducer,
    // notification: notificationSlice.reducer,
    // document: documentSlice.reducer,
    // report: reportSlice.reducer,
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
