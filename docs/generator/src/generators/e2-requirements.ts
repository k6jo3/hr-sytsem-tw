import fs from 'fs/promises';
import path from 'path';
import { PATHS, SERVICES, OUTPUT_PATHS, ServiceMeta } from '../config.js';
import { markdownParser } from '../shared/markdown-parser.js';
import { renderFullDocument } from '../shared/html-template.js';
import { pdfGenerator } from '../shared/pdf-generator.js';

/**
 * E2: 需求分析書 → 14 PDF
 * 來源：knowledge/02_Requirements_Analysis/
 *
 * 檔案對應：
 * - 01~07, 11~14：獨立檔案，直接對應
 * - 08-10：合併檔（績效/招募/訓練），依 heading 拆分
 * - 07-14：PM 審查補充，依 heading 併入對應服務
 */

/** 需求分析書檔案對應表 */
const FILE_MAP: Record<string, string[]> = {
  '01': ['01_IAM服務需求分析書.md'],
  '02': ['02_組織員工服務需求分析書.md'],
  '03': ['03_考勤管理服務需求分析書.md'],
  '04': ['04_薪資管理服務需求分析書.md'],
  '05': ['05_保險管理服務需求分析書.md'],
  '06': ['06_專案管理服務需求分析書.md'],
  '07': ['07_工時管理服務需求分析書.md'],
  '08': ['08-10_績效招募訓練服務需求分析書.md'],
  '09': ['08-10_績效招募訓練服務需求分析書.md'],
  '10': ['08-10_績效招募訓練服務需求分析書.md'],
  '11': ['11_簽核流程服務需求分析書.md'],
  '12': ['12_通知服務需求分析書.md'],
  '13': ['13_文件管理服務需求分析書.md'],
  '14': ['14_報表分析服務需求分析書.md'],
};

/** 從合併檔案中擷取指定服務的內容 */
function extractServiceContent(markdown: string, service: ServiceMeta): string {
  // 08-10 合併檔中，各服務以 H1 或 H2 分隔
  const serviceKeywords: Record<string, string[]> = {
    '08': ['績效', 'Performance'],
    '09': ['招募', 'Recruitment'],
    '10': ['訓練', 'Training'],
  };

  const keywords = serviceKeywords[service.code];
  if (!keywords) return markdown;

  const lines = markdown.split('\n');
  const sections: string[] = [];
  let capturing = false;
  let depth = 0;

  for (const line of lines) {
    const headingMatch = line.match(/^(#{1,2})\s+(.+)$/);
    if (headingMatch) {
      const level = headingMatch[1].length;
      const text = headingMatch[2];
      const isRelevant = keywords.some(kw => text.includes(kw));

      if (level <= 2) {
        if (isRelevant) {
          capturing = true;
          depth = level;
        } else if (capturing) {
          capturing = false;
        }
      }
    }

    if (capturing) {
      sections.push(line);
    }
  }

  return sections.length > 0 ? sections.join('\n') : markdown;
}

/** 讀取 PM 審查補充內容 */
async function loadPmSupplement(serviceCode: string): Promise<string> {
  const supplementFile = path.join(PATHS.requirements, '07-14_剩餘服務_PM審查補充.md');
  try {
    const content = await fs.readFile(supplementFile, 'utf-8');
    const codeNum = parseInt(serviceCode, 10);
    if (codeNum >= 7 && codeNum <= 14) {
      const service = SERVICES.find(s => s.code === serviceCode);
      if (service) {
        const extracted = extractServiceContent(content, service);
        if (extracted && extracted.trim().length > 0) {
          return `\n\n---\n\n## PM 審查補充\n\n${extracted}`;
        }
      }
    }
  } catch { /* 檔案不存在 */ }
  return '';
}

/** 生成單一服務的需求分析書 PDF */
async function generateServicePdf(service: ServiceMeta): Promise<string> {
  const files = FILE_MAP[service.code];
  if (!files || files.length === 0) {
    throw new Error(`找不到服務 ${service.code} 的需求分析書`);
  }

  // 讀取主檔案
  let markdown = '';
  for (const file of files) {
    const filePath = path.join(PATHS.requirements, file);
    const content = await fs.readFile(filePath, 'utf-8');

    // 如果是合併檔，擷取對應服務的內容
    if (file.includes('-')) {
      markdown += extractServiceContent(content, service);
    } else {
      markdown += content;
    }
  }

  // 附加 PM 審查補充
  const supplement = await loadPmSupplement(service.code);
  markdown += supplement;

  // 解析 Markdown
  const parsed = await markdownParser.parse(markdown);

  // 組合完整 HTML
  const title = `${service.code} ${service.name} — 需求分析書`;
  const html = await renderFullDocument({
    title,
    subtitle: `HR${service.code} ${service.id.toUpperCase()} Service`,
    bodyHtml: parsed.html,
    headings: parsed.headings,
    showToc: true,
    showCover: true,
    headerText: title,
  });

  // 生成 PDF
  const outputPath = path.join(OUTPUT_PATHS.E2, `${service.code}_${service.name}_需求分析書.pdf`);
  await pdfGenerator.generateFromHtml(html, {
    outputPath,
    headerText: title,
    mermaidWaitMs: 3000,
  });

  return outputPath;
}

/** E2 主入口：生成需求分析書 */
export async function generateE2(serviceFilter?: string): Promise<void> {
  console.log('\n--- E2: 需求分析書 ---\n');

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
      console.error(`[E2] ${service.code} ${service.name} 生成失敗:`, err);
    }
  }

  console.log(`\nE2 完成: ${services.length} 份需求分析書`);
}

// 支援直接執行
const isDirectRun = process.argv[1]?.includes('e2-requirements');
if (isDirectRun) {
  const serviceArg = process.argv.find(a => a.startsWith('--service'))
    ? process.argv[process.argv.indexOf('--service') + 1]
    : undefined;
  generateE2(serviceArg).then(() => pdfGenerator.close()).catch(console.error);
}
