import fs from 'fs/promises';
import path from 'path';
import { PATHS, SERVICES, OUTPUT_PATHS, ServiceMeta } from '../config.js';
import { markdownParser } from '../shared/markdown-parser.js';
import { renderFullDocument } from '../shared/html-template.js';
import { pdfGenerator } from '../shared/pdf-generator.js';

/**
 * E4: API 規格書 → 14 PDF
 * 來源：knowledge/04_API_Specifications/
 * 特色：以表格為主，需防止寬表格截斷（CSS 已處理 font-size 9px + word-break）
 */

/** API 規格書檔案對應表 */
const FILE_MAP: Record<string, string> = {
  '01': '01_IAM服務系統設計書_API詳細規格.md',
  '02': '02_組織員工服務系統設計書_API詳細規格.md',
  '03': '03_考勤管理服務系統設計書_API詳細規格.md',
  '04': '04_薪資管理服務系統設計書_API詳細規格.md',
  '05': '05_保險管理服務系統設計書_API詳細規格.md',
  '06': '06_專案管理服務系統設計書_API詳細規格.md',
  '07': '07_工時管理服務系統設計書_API詳細規格.md',
  '08': '08_績效管理服務系統設計書_API詳細規格.md',
  '09': '09_招募管理服務系統設計書_API詳細規格.md',
  '10': '10_訓練管理服務系統設計書_API詳細規格.md',
  '11': '11_簽核流程服務系統設計書_API詳細規格.md',
  '12': '12_通知服務系統設計書_API詳細規格.md',
  '13': '13_文件管理服務系統設計書_API詳細規格.md',
  '14': '14_報表分析服務系統設計書_API詳細規格.md',
};

/** 生成單一服務的 API 規格書 PDF */
async function generateServicePdf(service: ServiceMeta): Promise<string> {
  const filename = FILE_MAP[service.code];
  if (!filename) {
    throw new Error(`找不到服務 ${service.code} 的 API 規格書`);
  }

  const filePath = path.join(PATHS.apiSpecs, filename);
  const markdown = await fs.readFile(filePath, 'utf-8');

  // 解析 Markdown
  console.log(`[E4] 解析 ${service.code} ${service.name}...`);
  const parsed = await markdownParser.parse(markdown);

  // 組合完整 HTML
  const title = `${service.code} ${service.name} — API 規格書`;
  const html = await renderFullDocument({
    title,
    subtitle: `HR${service.code} ${service.id.toUpperCase()} Service — API Specification`,
    bodyHtml: parsed.html,
    headings: parsed.headings,
    showToc: true,
    showCover: true,
    headerText: title,
  });

  // 生成 PDF
  const outputPath = path.join(OUTPUT_PATHS.E4, `${service.code}_${service.name}_API規格書.pdf`);
  await pdfGenerator.generateFromHtml(html, {
    outputPath,
    headerText: title,
    mermaidWaitMs: 2000,
  });

  return outputPath;
}

/** E4 主入口：生成 API 規格書 */
export async function generateE4(serviceFilter?: string): Promise<void> {
  console.log('\n--- E4: API 規格書 ---\n');

  const services = serviceFilter
    ? SERVICES.filter(s => s.code === serviceFilter)
    : SERVICES;

  if (services.length === 0) {
    console.error(`找不到服務代碼: ${serviceFilter}`);
    return;
  }

  for (const service of services) {
    try {
      await generateServicePdf(service);
    } catch (err) {
      console.error(`[E4] ${service.code} ${service.name} 生成失敗:`, err);
    }
  }

  console.log(`\nE4 完成: ${services.length} 份 API 規格書`);
}

// 支援直接執行
const isDirectRun = process.argv[1]?.includes('e4-api-specs');
if (isDirectRun) {
  const serviceArg = process.argv.find(a => a.startsWith('--service'))
    ? process.argv[process.argv.indexOf('--service') + 1]
    : undefined;
  generateE4(serviceArg).then(() => pdfGenerator.close()).catch(console.error);
}
