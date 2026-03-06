import fs from 'fs/promises';
import path from 'path';
import {
  KNOWLEDGE_DIR, FRAMEWORK_DIR, BACKEND_DIR, FRONTEND_DIR,
  OUTPUT_PATHS, SERVICES, DOC_META
} from '../config.js';
import { markdownParser } from '../shared/markdown-parser.js';
import { renderFullDocument } from '../shared/html-template.js';
import { pdfGenerator } from '../shared/pdf-generator.js';
import { javaScanner, JavaFileInfo } from '../shared/java-scanner.js';
import { umlGenerator } from '../shared/uml-generator.js';

/**
 * E7: 程式維護手冊 → 1 PDF
 * 包含 Java 掃描結果、Package UML、前後端程式結構
 */

/** 讀取 markdown 檔案（存在才讀） */
async function tryReadMd(filePath: string): Promise<string> {
  try {
    return await fs.readFile(filePath, 'utf-8');
  } catch {
    return '';
  }
}

/** 遞迴列出檔案 */
async function listFiles(dir: string, ext: string, base?: string): Promise<string[]> {
  const results: string[] = [];
  const baseDir = base || dir;
  try {
    const entries = await fs.readdir(dir, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = path.join(dir, entry.name);
      if (entry.isDirectory() && !entry.name.startsWith('.') && entry.name !== 'node_modules' && entry.name !== 'target' && entry.name !== 'dist') {
        results.push(...await listFiles(fullPath, ext, baseDir));
      } else if (entry.isFile() && entry.name.endsWith(ext)) {
        results.push(path.relative(baseDir, fullPath).replace(/\\/g, '/'));
      }
    }
  } catch { /* 目錄不存在 */ }
  return results;
}

/** 生成第 1 章：開發環境建置 */
async function generateChapter1(): Promise<string> {
  const envGuide = await tryReadMd(path.join(KNOWLEDGE_DIR, '05_開發環境快速啟動指南.md'));
  if (envGuide) {
    const parsed = await markdownParser.parse(envGuide);
    return `<h1>1. 開發環境建置</h1>\n${parsed.html}`;
  }
  return `<h1>1. 開發環境建置</h1>
    <p>請參考 knowledge/05_開發環境快速啟動指南.md</p>`;
}

/** 生成第 2 章：系統架構概述 */
async function generateChapter2(): Promise<string> {
  let html = `<h1>2. 系統架構概述</h1>`;

  // 從 framework 讀取架構文件
  const archFiles = [
    { file: '01_核心架構原則.md', name: '核心架構原則' },
    { file: '02_DDD分層設計.md', name: 'DDD 分層設計' },
  ];

  for (const af of archFiles) {
    const content = await tryReadMd(path.join(FRAMEWORK_DIR, 'architecture', af.file));
    if (content) {
      const parsed = await markdownParser.parse(content);
      html += `<h2>2.${archFiles.indexOf(af) + 1} ${af.name}</h2>\n${parsed.html}`;
    }
  }

  return html;
}

/** 生成第 3 章：後端程式結構 + Package Diagram */
async function generateChapter3(): Promise<string> {
  let html = `<h1>3. 後端程式結構</h1>`;

  html += `<h2>3.1 微服務 Package 總覽</h2>`;
  html += `<table>
    <thead><tr><th>服務</th><th>Package</th><th>Port</th><th>說明</th></tr></thead>
    <tbody>`;

  for (const svc of SERVICES) {
    html += `<tr><td>HR${svc.code}</td><td><code>${svc.pkg}</code></td><td>${svc.port}</td><td>${svc.name}</td></tr>`;
  }
  html += `</tbody></table>`;

  // 掃描 Java 檔案並生成 UML
  console.log('[E7] 掃描 Java 原始碼...');
  const scanResults = await javaScanner.scanAll();
  const umlOutputDir = path.join(OUTPUT_PATHS.E7, 'uml');

  // 生成總覽 UML
  try {
    const overviewPath = await umlGenerator.renderOverviewUml(scanResults, umlOutputDir);
    const imgBuffer = await fs.readFile(overviewPath);
    const base64 = imgBuffer.toString('base64');
    html += `<h2>3.2 跨服務 Package 總覽圖</h2>
      <div class="diagram">
        <img src="data:image/png;base64,${base64}" alt="Package Overview" />
      </div>`;
  } catch (err) {
    console.warn(`[E7] 總覽 UML 生成失敗: ${err}`);
    html += `<h2>3.2 跨服務 Package 總覽圖</h2>
      <div class="diagram-error">UML 圖生成失敗（需要 Java 環境執行 PlantUML）</div>`;
  }

  // 各服務 Package Diagram
  html += `<h2>3.3 各服務 Package Diagram</h2>`;

  for (const result of scanResults) {
    html += `<h3>HR${result.service.code} ${result.service.name}</h3>`;
    html += `<p>檔案數: ${result.files.length} | Packages: ${result.packages.length}</p>`;

    // DDD 層級統計
    html += `<table>
      <thead><tr><th>DDD 層級</th><th>檔案數</th></tr></thead>
      <tbody>`;
    for (const [layer, count] of Object.entries(result.layerCounts)) {
      html += `<tr><td>${layer}</td><td>${count}</td></tr>`;
    }
    html += `</tbody></table>`;

    // Package Diagram 圖
    try {
      const umlPath = await umlGenerator.renderServiceUml(result, umlOutputDir);
      const imgBuffer = await fs.readFile(umlPath);
      const base64 = imgBuffer.toString('base64');
      html += `<div class="diagram">
        <img src="data:image/png;base64,${base64}" alt="${result.service.name} Package Diagram" />
      </div>`;
    } catch {
      html += `<div class="diagram-error">Package Diagram 生成失敗</div>`;
    }
  }

  return html;
}

/** 生成第 4 章：前端程式結構 */
async function generateChapter4(): Promise<string> {
  let html = `<h1>4. 前端程式結構</h1>`;

  // 讀取前端指南
  const feGuide = await tryReadMd(path.join(FRAMEWORK_DIR, 'development', '04_Frontend_Guide.md'));
  if (feGuide) {
    const parsed = await markdownParser.parse(feGuide);
    html += parsed.html;
  }

  // Feature 模組清單
  html += `<h2>4.1 Feature 模組對應</h2>`;
  html += `<table>
    <thead><tr><th>服務</th><th>Feature 目錄</th><th>路由前綴</th></tr></thead>
    <tbody>`;
  for (const svc of SERVICES) {
    html += `<tr><td>HR${svc.code} ${svc.name}</td><td><code>features/${svc.frontendFeature}</code></td><td>—</td></tr>`;
  }
  html += `</tbody></table>`;

  return html;
}

/** 生成第 5 章：命名規範 */
async function generateChapter5(): Promise<string> {
  const naming = await tryReadMd(path.join(FRAMEWORK_DIR, 'development', '02_命名規範.md'));
  if (naming) {
    const parsed = await markdownParser.parse(naming);
    return `<h1>5. 命名規範</h1>\n${parsed.html}`;
  }
  return `<h1>5. 命名規範</h1><p>請參考 framework/development/02_命名規範.md</p>`;
}

/** 生成第 6 章：開發流程 */
async function generateChapter6(): Promise<string> {
  const devFlow = await tryReadMd(path.join(FRAMEWORK_DIR, 'development', '01_開發流程.md'));
  if (devFlow) {
    const parsed = await markdownParser.parse(devFlow);
    return `<h1>6. 開發流程（TDD）</h1>\n${parsed.html}`;
  }
  return `<h1>6. 開發流程（TDD）</h1><p>請參考 framework/development/01_開發流程.md</p>`;
}

/** 生成第 7 章：測試規範（分段引用最新文件） */
async function generateChapter7(): Promise<string> {
  let html = `<h1>7. 測試規範</h1>`;

  // 7.1 測試架構總覽
  const overview = await tryReadMd(path.join(FRAMEWORK_DIR, 'testing', '01_測試架構總覽.md'));
  if (overview) {
    const parsed = await markdownParser.parse(overview);
    html += `<h2>7.1 測試架構總覽</h2>\n${parsed.html}`;
  }

  // 7.2 三階測試法
  const threePhase = await tryReadMd(path.join(FRAMEWORK_DIR, 'testing', '02_三階測試法.md'));
  if (threePhase) {
    const parsed = await markdownParser.parse(threePhase);
    html += `<h2>7.2 三階測試法</h2>\n${parsed.html}`;
  }

  // 7.3 合約驅動測試（使用專項文件，非舊版總覽）
  const contractTest = await tryReadMd(path.join(FRAMEWORK_DIR, 'testing', '04_合約驅動測試.md'));
  if (contractTest) {
    const parsed = await markdownParser.parse(contractTest);
    html += `<h2>7.3 合約驅動測試</h2>\n${parsed.html}`;
  }

  // 7.4 測試基類設計
  const baseClass = await tryReadMd(path.join(FRAMEWORK_DIR, 'testing', '05_測試基類設計.md'));
  if (baseClass) {
    const parsed = await markdownParser.parse(baseClass);
    html += `<h2>7.4 測試基類設計</h2>\n${parsed.html}`;
  }

  if (html === `<h1>7. 測試規範</h1>`) {
    html += `<p>請參考 framework/testing/ 目錄下的測試規範文件</p>`;
  }

  return html;
}

/** 生成第 8 章：CI/CD 與部署 */
async function generateChapter8(): Promise<string> {
  const cicd = await tryReadMd(path.join(KNOWLEDGE_DIR, '06_CICD與系統部署指南.md'));
  if (cicd) {
    const parsed = await markdownParser.parse(cicd);
    return `<h1>8. CI/CD 與部署</h1>\n${parsed.html}`;
  }
  return `<h1>8. CI/CD 與部署</h1><p>請參考 knowledge/06_CICD與系統部署指南.md</p>`;
}

/** 從 Java 檔案中擷取功能說明（Javadoc 第一行或 getName() 回傳值） */
async function extractDescription(filePath: string): Promise<string> {
  try {
    const content = await fs.readFile(filePath, 'utf-8');

    // 優先取 Javadoc 第一行描述
    const javadocMatch = content.match(/\/\*\*\s*\n\s*\*\s*(.+?)(?:\n|\*\/)/);
    if (javadocMatch) {
      return javadocMatch[1].trim().replace(/\s*\*\s*$/, '');
    }

    // 其次取 getName() 回傳值
    const getNameMatch = content.match(/getName\(\)\s*\{[\s\S]*?return\s*"(.+?)"/);
    if (getNameMatch) {
      return getNameMatch[1];
    }

    // 從類別名推導（駝峰轉中文動詞提示）
    const classMatch = content.match(/public class (\w+)/);
    if (classMatch) {
      return classMatch[1]
        .replace(/ServiceImpl$/, '')
        .replace(/Task$/, '')
        .replace(/([A-Z])/g, ' $1')
        .trim();
    }
  } catch { /* 讀取失敗 */ }
  return '—';
}

/** 遞迴找出符合條件的 Java 檔案 */
async function findJavaFilesByPattern(dir: string, pattern: RegExp): Promise<{ relativePath: string; fullPath: string }[]> {
  const results: { relativePath: string; fullPath: string }[] = [];
  try {
    const entries = await fs.readdir(dir, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = path.join(dir, entry.name);
      if (entry.isDirectory() && !entry.name.startsWith('.') && entry.name !== 'target' && entry.name !== 'node_modules') {
        results.push(...await findJavaFilesByPattern(fullPath, pattern));
      } else if (entry.isFile() && pattern.test(entry.name)) {
        results.push({ relativePath: entry.name, fullPath });
      }
    }
  } catch { /* 目錄不存在 */ }
  return results;
}

/** 生成附錄 A：後端程式清冊（僅 ServiceImpl + Task） */
async function generateAppendixA(): Promise<string> {
  let html = `<h1>附錄 A：後端程式清冊（Service / Task）</h1>`;
  html += `<p>僅列出 Application 層的 ServiceImpl 與 Pipeline Task，含功能說明。</p>`;

  let totalServices = 0;
  let totalTasks = 0;

  for (const svc of SERVICES) {
    const svcDir = path.join(BACKEND_DIR, `hrms-${svc.id}`);

    // 找 ServiceImpl
    const serviceFiles = await findJavaFilesByPattern(svcDir, /ServiceImpl\.java$/);
    // 找 Task
    const taskFiles = await findJavaFilesByPattern(svcDir, /Task\.java$/);

    const svcCount = serviceFiles.length;
    const taskCount = taskFiles.length;
    totalServices += svcCount;
    totalTasks += taskCount;

    html += `<h2>HR${svc.code} ${svc.name}（${svcCount} Service, ${taskCount} Task）</h2>`;

    if (svcCount > 0) {
      html += `<h3>Application Service</h3>`;
      html += `<table>
        <thead><tr><th>類別名稱</th><th>功能說明</th></tr></thead>
        <tbody>`;
      for (const f of serviceFiles.sort((a, b) => a.relativePath.localeCompare(b.relativePath))) {
        const desc = await extractDescription(f.fullPath);
        const name = f.relativePath.replace('.java', '');
        html += `<tr><td><code>${name}</code></td><td>${desc}</td></tr>`;
      }
      html += `</tbody></table>`;
    }

    if (taskCount > 0) {
      html += `<h3>Pipeline Task</h3>`;
      html += `<table>
        <thead><tr><th>類別名稱</th><th>功能說明</th></tr></thead>
        <tbody>`;
      for (const f of taskFiles.sort((a, b) => a.relativePath.localeCompare(b.relativePath))) {
        const desc = await extractDescription(f.fullPath);
        const name = f.relativePath.replace('.java', '');
        html += `<tr><td><code>${name}</code></td><td>${desc}</td></tr>`;
      }
      html += `</tbody></table>`;
    }

    if (svcCount === 0 && taskCount === 0) {
      html += `<p>（無 ServiceImpl 或 Task 檔案）</p>`;
    }
  }

  html += `<h2>統計</h2>`;
  html += `<p>ServiceImpl 合計：${totalServices} 個 | Task 合計：${totalTasks} 個 | 共 ${totalServices + totalTasks} 個程式</p>`;

  return html;
}

/** 生成附錄 B：前端 TypeScript 檔案清單 */
async function generateAppendixB(): Promise<string> {
  const files = await listFiles(path.join(FRONTEND_DIR, 'src'), '.ts');
  const tsxFiles = await listFiles(path.join(FRONTEND_DIR, 'src'), '.tsx');
  const allFiles = [...files, ...tsxFiles].sort();

  let html = `<h1>附錄 B：前端 TypeScript 檔案清單</h1>`;
  html += `<p>共 ${allFiles.length} 個檔案</p>`;
  html += `<pre class="hljs"><code>`;
  html += allFiles.join('\n');
  html += `</code></pre>`;

  return html;
}

/** E7 主入口：生成程式維護手冊 */
export async function generateE7(): Promise<void> {
  console.log('\n--- E7: 程式維護手冊 ---\n');

  // 依序生成各章節
  const chapters = await Promise.all([
    generateChapter1(),
    generateChapter2(),
  ]);

  // Chapter 3 需要 Java 掃描，單獨處理
  const chapter3 = await generateChapter3();

  const laterChapters = await Promise.all([
    generateChapter4(),
    generateChapter5(),
    generateChapter6(),
    generateChapter7(),
    generateChapter8(),
    generateAppendixA(),
    generateAppendixB(),
  ]);

  const bodyHtml = [...chapters, chapter3, ...laterChapters].join('\n');
  const title = '程式維護手冊';

  const html = await renderFullDocument({
    title,
    subtitle: DOC_META.company,
    bodyHtml,
    showToc: false,  // 章節太多，目錄會很長，省略
    showCover: true,
    headerText: title,
  });

  const outputPath = path.join(OUTPUT_PATHS.E7, '程式維護手冊.pdf');
  await pdfGenerator.generateFromHtml(html, {
    outputPath,
    headerText: title,
    mermaidWaitMs: 3000,
  });

  console.log('\nE7 完成: 程式維護手冊 PDF');
}

// 支援直接執行
const isDirectRun = process.argv[1]?.includes('e7-maintenance-manual');
if (isDirectRun) {
  generateE7().then(() => pdfGenerator.close()).catch(console.error);
}
