import fs from 'fs/promises';
import path from 'path';
import { KNOWLEDGE_DIR, DIAGRAMS_DIR, OUTPUT_PATHS, SERVICES, DOC_META } from '../config.js';
import { pptxGenerator, SlideContent } from '../shared/pptx-generator.js';

/**
 * E6: 系統說明簡報 → 1 PPT（~25 張投影片）
 * 結構：封面 → 系統背景 → 功能模組總覽 → 架構圖 → 技術棧
 *       → 核心業務流程 → DDD/CQRS/Event-Driven → 測試架構
 *       → CI/CD → 畫面展示（E5 截圖）→ 系統規模 → Q&A
 */

/** 嘗試讀取圖檔路徑（存在才回傳） */
async function tryImagePath(filePath: string): Promise<string | undefined> {
  try {
    await fs.access(filePath);
    return filePath;
  } catch {
    return undefined;
  }
}

/** E6 主入口：生成系統說明簡報 */
export async function generateE6(): Promise<void> {
  console.log('\n--- E6: 系統說明簡報 ---\n');

  const slides: SlideContent[] = [];

  // 1. 封面
  slides.push({
    layout: 'cover',
    title: '人力資源暨專案管理系統',
    subtitle: '系統說明簡報 — System Overview',
  });

  // 2. 目錄
  slides.push({
    layout: 'content',
    title: '簡報大綱',
    bullets: [
      '1. 系統背景與問題定義',
      '2. 功能模組總覽',
      '3. 系統架構設計',
      '4. 技術棧',
      '5. 核心業務流程',
      '6. DDD / CQRS / Event-Driven',
      '7. 測試架構',
      '8. CI/CD 與部署',
      '9. 畫面展示',
      '10. 系統規模統計',
    ],
  });

  // 3. 系統背景
  slides.push({
    layout: 'section',
    title: '1. 系統背景與問題定義',
  });

  slides.push({
    layout: 'content',
    title: '問題：企業人資系統碎片化',
    bullets: [
      '考勤、薪資、招募、專案追蹤各自獨立',
      '員工資料需重複維護，容易不一致',
      '跨系統流程（如入職→建帳號→加保）需人工串接',
      '缺乏 Single Source of Truth',
    ],
  });

  slides.push({
    layout: 'content',
    title: '解決方案：整合式人資平台',
    bullets: [
      '14 個微服務統一管理 500 人規模企業',
      '員工從入職到離職的完整生命週期',
      'Event-Driven 跨服務自動串接',
      '三種角色（員工 ESS、主管審核、HR 管理）',
    ],
  });

  // 4. 功能模組總覽
  slides.push({
    layout: 'section',
    title: '2. 功能模組總覽',
    subtitle: '14 個微服務 × 4 個 DDD 層',
  });

  slides.push({
    layout: 'table',
    title: '14 微服務一覽',
    tableData: [
      ['代碼', '服務', '核心功能'],
      ['01', 'IAM', '認證授權、RBAC、SSO、多租戶'],
      ['02', 'Organization', '員工生命週期、組織結構'],
      ['03', 'Attendance', '打卡、請假、加班、排班'],
      ['04', 'Payroll', '薪資計算（Saga）、稅務'],
      ['05', 'Insurance', '勞健保、退休金'],
      ['06', 'Project', '客戶、WBS、成本追蹤'],
      ['07', 'Timesheet', '週報、PM 審核'],
      ['08', 'Performance', '考核週期、彈性表單'],
      ['09', 'Recruitment', '職缺、Kanban、面試'],
      ['10', 'Training', '課程管理、證照'],
      ['11', 'Workflow', '視覺化流程、多層簽核'],
      ['12', 'Notification', 'Email/Push/Teams/LINE'],
      ['13', 'Document', '版控、範本、加密'],
      ['14', 'Reporting', 'CQRS ReadModel、儀表板'],
    ],
  });

  // 5. 系統架構
  slides.push({
    layout: 'section',
    title: '3. 系統架構設計',
    subtitle: 'DDD + CQRS + Event-Driven',
  });

  const archImage = await tryImagePath(path.join(DIAGRAMS_DIR, '00_系統架構拓撲圖.png'));
  if (archImage) {
    slides.push({
      layout: 'image',
      title: '系統架構拓撲圖',
      imagePath: archImage,
    });
  }

  slides.push({
    layout: 'content',
    title: 'DDD 四層架構',
    bullets: [
      'Interface 層：REST Controllers、DTO',
      'Application 層：Use Case 編排、Saga 協調',
      'Domain 層：Aggregate、Entity、Value Object、Domain Service',
      'Infrastructure 層：Repository、DAO、Mapper',
    ],
  });

  slides.push({
    layout: 'content',
    title: 'CQRS + Event-Driven',
    bullets: [
      'Command (POST/PUT/DELETE) 與 Query (GET) 分離',
      'Service Factory 模式：method name → bean name 自動對應',
      'Kafka 非同步事件通訊',
      '事件命名：{Aggregate}{PastVerb}Event',
    ],
  });

  // 6. 技術棧
  slides.push({
    layout: 'section',
    title: '4. 技術棧',
  });

  slides.push({
    layout: 'twoColumn',
    title: '技術棧總覽',
    leftContent: '後端\n━━━━━━━━━━━━━━\nSpring Boot 3.1\nSpring Cloud 2023\nJava 21\nPostgreSQL 15+\nQuerydsl + JPA\nRedis\nKafka\nEureka',
    rightContent: '前端\n━━━━━━━━━━━━━━\nReact 18\nTypeScript 5\nVite 5\nRedux Toolkit\nAnt Design 5\nECharts\nAxios\nReact Router 6',
  });

  // 7. 核心業務流程
  slides.push({
    layout: 'section',
    title: '5. 核心業務流程',
    subtitle: '四大核心流程',
  });

  const flowDiagrams = [
    { file: '01_員工入職流程.png', title: '員工入職流程' },
    { file: '01_薪資SAGA補償流程.png', title: '薪資 SAGA 補償流程' },
  ];

  for (const d of flowDiagrams) {
    const imgPath = await tryImagePath(path.join(DIAGRAMS_DIR, d.file));
    if (imgPath) {
      slides.push({ layout: 'image', title: d.title, imagePath: imgPath });
    }
  }

  // 8. 測試架構
  slides.push({
    layout: 'section',
    title: '6. 測試架構',
    subtitle: '合約驅動 + TDD',
  });

  slides.push({
    layout: 'content',
    title: '測試策略',
    bullets: [
      '合約驅動測試：SA 定義規格 → 工程師依合約實作 → 自動驗證',
      '三層驗證：輸入 + 輸出 + 副作用',
      'Domain 邏輯 100% 覆蓋率',
      'API 端點整合測試',
      '前端 Factory/Component/Hook 單元測試',
      '1,843 測試 × 0 失敗',
    ],
  });

  // 9. CI/CD
  slides.push({
    layout: 'section',
    title: '7. CI/CD 與部署',
  });

  const cicdImage = await tryImagePath(path.join(DIAGRAMS_DIR, '06_CICD_Pipeline.png'));
  if (cicdImage) {
    slides.push({ layout: 'image', title: 'CI/CD Pipeline', imagePath: cicdImage });
  }

  slides.push({
    layout: 'content',
    title: '部署架構',
    bullets: [
      'GCP Cloud Run 容器化部署',
      'Docker Compose 本機開發',
      'GitHub Actions CI/CD',
      '14 服務獨立部署、獨立擴縮',
    ],
  });

  // 10. 系統規模
  slides.push({
    layout: 'section',
    title: '8. 系統規模統計',
  });

  slides.push({
    layout: 'table',
    title: '系統規模',
    tableData: [
      ['指標', '數量'],
      ['微服務數', '14'],
      ['後端 Java 檔案', '~1,842'],
      ['前端 TypeScript 檔案', '~500+'],
      ['自動化測試', '1,843'],
      ['API 端點', '200+'],
      ['前端路由', '65'],
      ['合約測試規格', '43'],
      ['知識文件', '100+'],
    ],
  });

  // Q&A
  slides.push({
    layout: 'section',
    title: 'Q & A',
    subtitle: '謝謝聆聽',
  });

  // 生成 PPT
  const outputPath = path.join(OUTPUT_PATHS.E6, '系統說明簡報.pptx');
  await pptxGenerator.generate(slides, outputPath);

  console.log('\nE6 完成: 系統說明簡報 PPTX');
}

// 支援直接執行
const isDirectRun = process.argv[1]?.includes('e6-system-overview');
if (isDirectRun) {
  generateE6().catch(console.error);
}
