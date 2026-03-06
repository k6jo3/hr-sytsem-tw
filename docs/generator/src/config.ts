import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

/** 專案根目錄 */
export const PROJECT_ROOT = path.resolve(__dirname, '..', '..', '..');

/** knowledge 目錄 */
export const KNOWLEDGE_DIR = path.join(PROJECT_ROOT, 'knowledge');

/** framework 目錄 */
export const FRAMEWORK_DIR = path.join(PROJECT_ROOT, 'framework');

/** 後端目錄 */
export const BACKEND_DIR = path.join(PROJECT_ROOT, 'backend');

/** 前端目錄 */
export const FRONTEND_DIR = path.join(PROJECT_ROOT, 'frontend');

/** 產出目錄 */
export const OUTPUT_DIR = path.join(__dirname, '..', 'output');

/** PlantUML jar 路徑 */
export const PLANTUML_JAR = path.join(KNOWLEDGE_DIR, 'tools', 'plantuml.jar');

/** 已渲染圖表目錄 */
export const DIAGRAMS_DIR = path.join(KNOWLEDGE_DIR, 'diagrams');

/** 子目錄路徑 */
export const PATHS = {
  requirements: path.join(KNOWLEDGE_DIR, '02_Requirements_Analysis'),
  systemDesign: path.join(KNOWLEDGE_DIR, '02_System_Design'),
  apiSpecs: path.join(KNOWLEDGE_DIR, '04_API_Specifications'),
  logicSpecs: path.join(KNOWLEDGE_DIR, '03_Logic_Specifications'),
  uiDesign: path.join(KNOWLEDGE_DIR, '06_UI_Design'),
  reports: path.join(KNOWLEDGE_DIR, '05_Reports'),
  clientReqs: path.join(KNOWLEDGE_DIR, '01_Client_Requirements'),
} as const;

/** 14 個微服務 metadata */
export interface ServiceMeta {
  code: string;       // "01" ~ "14"
  id: string;         // 英文名稱
  name: string;       // 中文名稱
  pkg: string;        // Java package
  port: number;       // Local Profile port
  frontendFeature: string; // 前端 feature 目錄
}

export const SERVICES: ServiceMeta[] = [
  { code: '01', id: 'iam',          name: 'IAM 認證授權',     pkg: 'com.company.hrms.iam',          port: 8081, frontendFeature: 'auth' },
  { code: '02', id: 'organization', name: '組織員工管理',     pkg: 'com.company.hrms.organization', port: 8082, frontendFeature: 'organization' },
  { code: '03', id: 'attendance',   name: '考勤管理',         pkg: 'com.company.hrms.attendance',   port: 8083, frontendFeature: 'attendance' },
  { code: '04', id: 'payroll',      name: '薪資管理',         pkg: 'com.company.hrms.payroll',      port: 8084, frontendFeature: 'payroll' },
  { code: '05', id: 'insurance',    name: '保險管理',         pkg: 'com.company.hrms.insurance',    port: 8085, frontendFeature: 'insurance' },
  { code: '06', id: 'project',      name: '專案管理',         pkg: 'com.company.hrms.project',      port: 8086, frontendFeature: 'project' },
  { code: '07', id: 'timesheet',    name: '工時管理',         pkg: 'com.company.hrms.timesheet',    port: 8087, frontendFeature: 'timesheet' },
  { code: '08', id: 'performance',  name: '績效管理',         pkg: 'com.company.hrms.performance',  port: 8088, frontendFeature: 'performance' },
  { code: '09', id: 'recruitment',  name: '招募管理',         pkg: 'com.company.hrms.recruitment',  port: 8089, frontendFeature: 'recruitment' },
  { code: '10', id: 'training',     name: '訓練管理',         pkg: 'com.company.hrms.training',     port: 8090, frontendFeature: 'training' },
  { code: '11', id: 'workflow',     name: '簽核流程',         pkg: 'com.company.hrms.workflow',      port: 8091, frontendFeature: 'workflow' },
  { code: '12', id: 'notification', name: '通知服務',         pkg: 'com.company.hrms.notification', port: 8092, frontendFeature: 'notification' },
  { code: '13', id: 'document',     name: '文件管理',         pkg: 'com.company.hrms.document',      port: 8093, frontendFeature: 'document' },
  { code: '14', id: 'reporting',    name: '報表分析',         pkg: 'com.company.hrms.reporting',    port: 8094, frontendFeature: 'report' },
];

/** 依 code 取得服務 metadata */
export function getService(code: string): ServiceMeta | undefined {
  return SERVICES.find(s => s.code === code);
}

/** 文件共用 metadata */
export const DOC_META = {
  company: '人力資源暨專案管理系統',
  version: '1.0',
  date: new Date().toISOString().split('T')[0],
  author: 'HRMS 開發團隊',
  fontFamily: "'Microsoft JhengHei', 'Noto Sans TC', 'PingFang TC', sans-serif",
} as const;

/** 各 E 文件輸出路徑 */
export const OUTPUT_PATHS = {
  E1: path.join(OUTPUT_DIR, 'E1_系統分析與架構'),
  E2: path.join(OUTPUT_DIR, 'E2_需求分析書'),
  E3: path.join(OUTPUT_DIR, 'E3_系統設計書'),
  E4: path.join(OUTPUT_DIR, 'E4_API規格書'),
  E5: path.join(OUTPUT_DIR, 'E5_系統操作手冊'),
  E6: path.join(OUTPUT_DIR, 'E6_系統說明'),
  E7: path.join(OUTPUT_DIR, 'E7_程式維護手冊'),
} as const;
