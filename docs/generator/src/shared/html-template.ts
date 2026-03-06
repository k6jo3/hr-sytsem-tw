import fs from 'fs/promises';
import path from 'path';
import { DOC_META } from '../config.js';
import { fileURLToPath } from 'url';
import { Heading } from './markdown-parser.js';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const STYLES_PATH = path.join(__dirname, '..', 'templates', 'styles.css');

/** 讀取 CSS 樣式 */
async function loadStyles(): Promise<string> {
  try {
    return await fs.readFile(STYLES_PATH, 'utf-8');
  } catch {
    console.warn('[HTML Template] styles.css 不存在，使用內建樣式');
    return '';
  }
}

/** 封面頁 HTML */
export function renderCoverPage(opts: {
  title: string;
  subtitle?: string;
  version?: string;
  date?: string;
  author?: string;
}): string {
  return `
    <div class="cover-page">
      <div class="cover-logo">HRMS</div>
      <h1 class="cover-title">${opts.title}</h1>
      ${opts.subtitle ? `<h2 class="cover-subtitle">${opts.subtitle}</h2>` : ''}
      <div class="cover-meta">
        <div class="cover-meta-item">
          <span class="label">系統名稱</span>
          <span class="value">${DOC_META.company}</span>
        </div>
        <div class="cover-meta-item">
          <span class="label">版本</span>
          <span class="value">${opts.version || DOC_META.version}</span>
        </div>
        <div class="cover-meta-item">
          <span class="label">日期</span>
          <span class="value">${opts.date || DOC_META.date}</span>
        </div>
        <div class="cover-meta-item">
          <span class="label">作者</span>
          <span class="value">${opts.author || DOC_META.author}</span>
        </div>
      </div>
    </div>`;
}

/** 目錄頁 HTML */
export function renderTocPage(headings: Heading[]): string {
  // 只取 H1, H2, H3
  const tocItems = headings
    .filter(h => h.level <= 3)
    .map(h => {
      const indent = (h.level - 1) * 20;
      const cls = `toc-level-${h.level}`;
      return `<div class="toc-item ${cls}" style="padding-left: ${indent}px">
        <a href="#${h.id}">${h.text}</a>
        <span class="toc-dots"></span>
      </div>`;
    })
    .join('\n');

  return `
    <div class="toc-page">
      <h1 class="toc-title">目錄</h1>
      <div class="toc-content">
        ${tocItems}
      </div>
    </div>`;
}

/** 完整 PDF HTML 文件 */
export async function renderFullDocument(opts: {
  title: string;
  subtitle?: string;
  bodyHtml: string;
  headings?: Heading[];
  showToc?: boolean;
  showCover?: boolean;
  headerText?: string;
}): Promise<string> {
  const styles = await loadStyles();
  const cover = opts.showCover !== false
    ? renderCoverPage({ title: opts.title, subtitle: opts.subtitle })
    : '';
  const toc = opts.showToc !== false && opts.headings
    ? renderTocPage(opts.headings)
    : '';
  const headerText = opts.headerText || `${DOC_META.company} — ${opts.title}`;

  return `<!DOCTYPE html>
<html lang="zh-TW">
<head>
  <meta charset="UTF-8" />
  <title>${opts.title}</title>
  <style>${styles}</style>
  <script src="https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.min.js"></script>
  <script>
    document.addEventListener('DOMContentLoaded', () => {
      if (typeof mermaid !== 'undefined') {
        mermaid.initialize({
          startOnLoad: true,
          theme: 'default',
          securityLevel: 'loose',
          flowchart: { useMaxWidth: true },
          sequence: { useMaxWidth: true },
        });
      }
    });
  </script>
</head>
<body>
  ${cover}
  ${toc}
  <div class="document-body">
    ${opts.bodyHtml}
  </div>
  <div class="header-text" style="display:none">${headerText}</div>
</body>
</html>`;
}
