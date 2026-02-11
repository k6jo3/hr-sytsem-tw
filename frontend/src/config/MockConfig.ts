/**
 * Mock Configuration
 * Controls whether to use mock APIs or real backend APIs.
 */
export const MockConfig = {
  // Master switch: If true, all modules will use mock APIs by default
  USE_MOCK_ALL: true, 

  // Per-module overrides
  // These are checked if USE_MOCK_ALL is false, or can satisfy specific logic
  modules: {
    AUTH: true,
    ORGANIZATION: true,
    ATTENDANCE: true,
    PAYROLL: true,
    INSURANCE: true,
    PROJECT: true,
    TIMESHEET: true,
    PERFORMANCE: true,
    RECRUITMENT: true,
    TRAINING: true,
    WORKFLOW: true,
    NOTIFICATION: true,
    DOCUMENT: true,
    REPORT: true,
  },

  /**
   * Check if a specific module should use mock API
   * @param moduleName The key of the module in the modules object
   * @returns boolean
   */
  isEnabled(moduleName: string): boolean {
    return this.USE_MOCK_ALL || (this.modules as any)[moduleName];
  }
} as const;
