import fs from 'fs/promises';
import path from 'path';
import { KNOWLEDGE_DIR, DIAGRAMS_DIR, OUTPUT_PATHS, SERVICES, DOC_META } from '../config.js';
import { markdownParser } from '../shared/markdown-parser.js';
import { renderFullDocument } from '../shared/html-template.js';
import { pdfGenerator } from '../shared/pdf-generator.js';
import { pptxGenerator, SlideContent } from '../shared/pptx-generator.js';

/**
 * E1: 系統分析與架構 → PPT + PDF
 * 來源：knowledge/00~04_*.md + 已渲染 PNG（knowledge/diagrams/）
 */

/** E1 來源檔案（按順序） */
const SOURCE_FILES = [
  '00_系統分析與架構總覽.md',
  '01_核心業務循序圖.md',
  '02_跨微服務實體關聯圖.md',
  '03_系統使用案例圖與規格.md',
  '04_核心業務流程圖.md',
];

/** 相關圖表 PNG 檔案 */
const DIAGRAM_FILES = [
  '00_系統架構拓撲圖.png',
  '01_員工入職流程.png',
  '01_員工離職流程.png',
  '01_薪資SAGA補償流程.png',
  '01_請假簽核流程.png',
  '02_跨微服務實體關聯圖.png',
  '03_usecase.png',
  '04_考勤結算至薪資發放流程.png',
];

/** 生成 E1 PDF */
async function generatePdf(): Promise<string> {
  console.log('[E1] 生成系統分析與架構 PDF...');

  let combinedMarkdown = '';

  for (const file of SOURCE_FILES) {
    const filePath = path.join(KNOWLEDGE_DIR, file);
    try {
      const content = await fs.readFile(filePath, 'utf-8');
      combinedMarkdown += content + '\n\n---\n\n';
    } catch {
      console.warn(`[E1] 找不到: ${file}`);
    }
  }

  // 解析合併後的 Markdown
  const parsed = await markdownParser.parse(combinedMarkdown);

  // 在正文前插入已渲染的圖表
  let diagramHtml = '<h2>架構圖表</h2>\n';
  for (const diagramFile of DIAGRAM_FILES) {
    const diagramPath = path.join(DIAGRAMS_DIR, diagramFile);
    try {
      const imgBuffer = await fs.readFile(diagramPath);
      const base64 = imgBuffer.toString('base64');
      const label = diagramFile.replace('.png', '').replace(/_/g, ' ');
      diagramHtml += `
        <div class="diagram">
          <img src="data:image/png;base64,${base64}" alt="${label}" />
          <p class="screenshot-caption">${label}</p>
        </div>\n`;
    } catch {
      // 圖檔不存在，跳過
    }
  }

  const fullBodyHtml = parsed.html + '\n' + diagramHtml;
  const title = '系統分析與架構總覽';

  const html = await renderFullDocument({
    title,
    subtitle: 'HRMS System Analysis & Architecture',
    bodyHtml: fullBodyHtml,
    headings: parsed.headings,
    showToc: true,
    showCover: true,
    headerText: title,
  });

  const outputPath = path.join(OUTPUT_PATHS.E1, '系統分析與架構總覽.pdf');
  await pdfGenerator.generateFromHtml(html, {
    outputPath,
    headerText: title,
    mermaidWaitMs: 5000,
  });

  return outputPath;
}

/** 生成 E1 PPT */
async function generatePpt(): Promise<string> {
  console.log('[E1] 生成系統分析與架構 PPT...');

  const slides: SlideContent[] = [];

  // 封面
  slides.push({
    layout: 'cover',
    title: '系統分析與架構總覽',
    subtitle: DOC_META.company,
  });

  // 目錄
  slides.push({
    layout: 'content',
    title: '文件目錄',
    bullets: [
      '1. 系統架構總覽',
      '2. 核心業務循序圖',
      '3. 跨微服務實體關聯圖',
      '4. 系統使用案例圖與規格',
      '5. 核心業務流程圖',
    ],
  });

  // 系統架構
  slides.push({
    layout: 'section',
    title: '1. 系統架構總覽',
    subtitle: 'DDD + CQRS + Event-Driven Architecture',
  });

  slides.push({
    layout: 'content',
    title: '架構設計原則',
    bullets: [
      'Domain-Driven Design (DDD) 四層架構',
      'CQRS 讀寫分離模式',
      'Event-Driven 非同步通訊 (Kafka)',
      '14 個微服務獨立部署',
      'Spring Boot 3.1 + Spring Cloud 2023',
    ],
  });

  // 微服務總覽表格
  const serviceTable = [
    ['代碼', '服務名稱', 'Package', 'Port'],
    ...SERVICES.map(s => [s.code, s.name, s.pkg, String(s.port)]),
  ];

  slides.push({
    layout: 'table',
    title: '14 微服務一覽',
    tableData: serviceTable,
  });

  // 插入架構圖
  const archDiagram = path.join(DIAGRAMS_DIR, '00_系統架構拓撲圖.png');
  try {
    await fs.access(archDiagram);
    slides.push({
      layout: 'image',
      title: '系統架構拓撲圖',
      imagePath: archDiagram,
    });
  } catch { /* 圖不存在 */ }

  // 核心業務循序圖
  slides.push({
    layout: 'section',
    title: '2. 核心業務循序圖',
    subtitle: '員工入職/離職/請假/薪資 四大核心流程',
  });

  const sequenceDiagrams = [
    { file: '01_員工入職流程.png', title: '員工入職流程' },
    { file: '01_員工離職流程.png', title: '員工離職流程' },
    { file: '01_請假簽核流程.png', title: '請假簽核流程' },
    { file: '01_薪資SAGA補償流程.png', title: '薪資 SAGA 補償流程' },
  ];

  for (const diagram of sequenceDiagrams) {
    const filePath = path.join(DIAGRAMS_DIR, diagram.file);
    try {
      await fs.access(filePath);
      slides.push({
        layout: 'image',
        title: diagram.title,
        imagePath: filePath,
      });
    } catch { /* 跳過 */ }
  }

  // ER 圖
  slides.push({
    layout: 'section',
    title: '3. 跨微服務實體關聯圖',
  });

  const erDiagram = path.join(DIAGRAMS_DIR, '02_跨微服務實體關聯圖.png');
  try {
    await fs.access(erDiagram);
    slides.push({
      layout: 'image',
      title: '跨微服務實體關聯圖',
      imagePath: erDiagram,
    });
  } catch { /* 跳過 */ }

  // 使用案例
  slides.push({
    layout: 'section',
    title: '4. 系統使用案例圖與規格',
    subtitle: '12 大使用案例分析',
  });

  const usecaseDiagram = path.join(DIAGRAMS_DIR, '03_usecase.png');
  try {
    await fs.access(usecaseDiagram);
    slides.push({
      layout: 'image',
      title: '系統使用案例圖',
      imagePath: usecaseDiagram,
    });
  } catch { /* 跳過 */ }

  // 業務流程（7 個核心流程）
  slides.push({
    layout: 'section',
    title: '5. 核心業務流程圖',
    subtitle: '7 大核心業務流程',
  });

  slides.push({
    layout: 'content',
    title: '核心業務流程一覽',
    bullets: [
      '5.1 考勤結算至薪資發放流程',
      '5.2 員工入職流程',
      '5.3 請假簽核流程',
      '5.4 績效考核流程',
      '5.5 曠職判定流程',
      '5.6 薪資預借審核流程',
      '5.7 離職退保連動流程',
    ],
  });

  // 已有 PNG 的流程圖
  const flowDiagram = path.join(DIAGRAMS_DIR, '04_考勤結算至薪資發放流程.png');
  try {
    await fs.access(flowDiagram);
    slides.push({
      layout: 'image',
      title: '5.1 考勤結算至薪資發放流程',
      imagePath: flowDiagram,
    });
  } catch { /* 跳過 */ }

  // 其餘 6 個流程以文字摘要呈現（mermaid 圖在 PDF 中渲染）
  const flowSummaries: { title: string; bullets: string[] }[] = [
    {
      title: '5.2 員工入職流程',
      bullets: [
        'HR 建立員工基本資料 → Organization 服務',
        '觸發 EmployeeCreatedEvent → IAM 自動建立帳號',
        '→ Insurance 自動加保（勞保/健保）',
        '→ Notification 發送歡迎通知',
      ],
    },
    {
      title: '5.3 請假簽核流程',
      bullets: [
        '員工提交請假申請 → 驗證假別餘額',
        '→ Workflow 引擎啟動多層簽核',
        '→ 主管/HR 逐層審核',
        '→ 核准後扣除假別餘額 → 通知申請人',
      ],
    },
    {
      title: '5.4 績效考核流程',
      bullets: [
        'HR 建立考核週期 → 設定考核模板',
        '→ 員工自評 → 主管評分',
        '→ HR 校準 → 最終確認',
        '→ 績效結果影響薪資調整',
      ],
    },
    {
      title: '5.5 曠職判定流程',
      bullets: [
        '月結排程掃描出勤紀錄',
        '→ 無打卡且無請假 → 標記異常',
        '→ 超過寬限期未補正 → 判定曠職',
        '→ 通知主管與 HR',
      ],
    },
    {
      title: '5.6 薪資預借審核流程',
      bullets: [
        '員工申請薪資預借 → 驗證資格與額度',
        '→ 主管審核 → HR/財務審核',
        '→ 核准後建立扣款排程',
        '→ 後續薪資自動扣回',
      ],
    },
    {
      title: '5.7 離職退保連動流程',
      bullets: [
        'HR 執行離職作業 → Organization 更新狀態',
        '→ Insurance 自動退保（勞保/健保）',
        '→ IAM 停用帳號 → Payroll 計算離職結算',
        '→ 產生離職證明文件',
      ],
    },
  ];

  for (const flow of flowSummaries) {
    slides.push({
      layout: 'content',
      title: flow.title,
      bullets: flow.bullets,
    });
  }

  // 結尾
  slides.push({
    layout: 'section',
    title: 'Q & A',
    subtitle: '謝謝',
  });

  const outputPath = path.join(OUTPUT_PATHS.E1, '系統分析與架構總覽.pptx');
  await pptxGenerator.generate(slides, outputPath);
  return outputPath;
}

/** E1 主入口 */
export async function generateE1(): Promise<void> {
  console.log('\n--- E1: 系統分析與架構 ---\n');
  await generatePdf();
  await generatePpt();
  console.log('\nE1 完成: PDF + PPTX');
}

// 支援直接執行
const isDirectRun = process.argv[1]?.includes('e1-system-analysis');
if (isDirectRun) {
  generateE1().then(() => pdfGenerator.close()).catch(console.error);
}
