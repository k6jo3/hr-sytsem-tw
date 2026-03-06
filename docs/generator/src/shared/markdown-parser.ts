import MarkdownIt from 'markdown-it';
import hljs from 'highlight.js';
import { plantumlRenderer } from './plantuml-renderer.js';

/** Markdown 解析結果 */
export interface ParsedMarkdown {
  html: string;
  title: string;
  headings: Heading[];
}

export interface Heading {
  level: number;
  text: string;
  id: string;
}

/**
 * Markdown 解析器
 * 支援 mermaid/plantuml code block 辨識與渲染
 */
export class MarkdownParser {
  private md: MarkdownIt;
  private mermaidBlocks: Map<string, string> = new Map();
  private plantumlBlocks: Map<string, string> = new Map();

  constructor() {
    this.md = new MarkdownIt({
      html: true,
      linkify: true,
      typographer: true,
      highlight: (str: string, lang: string) => {
        // 攔截 mermaid 和 plantuml code blocks
        if (lang === 'mermaid') {
          const id = `mermaid_${this.mermaidBlocks.size}`;
          this.mermaidBlocks.set(id, str);
          return `<div class="mermaid-placeholder" data-id="${id}">${this.escapeHtml(str)}</div>`;
        }
        if (lang === 'plantuml' || lang === 'puml') {
          const id = `plantuml_${this.plantumlBlocks.size}`;
          this.plantumlBlocks.set(id, str);
          return `<div class="plantuml-placeholder" data-id="${id}"></div>`;
        }
        // 一般程式碼高亮
        if (lang && hljs.getLanguage(lang)) {
          try {
            return `<pre class="hljs"><code>${hljs.highlight(str, { language: lang }).value}</code></pre>`;
          } catch { /* fallback */ }
        }
        return `<pre class="hljs"><code>${this.escapeHtml(str)}</code></pre>`;
      },
    });
  }

  /** 解析 Markdown 為 HTML（不渲染圖表） */
  parseSync(markdown: string): ParsedMarkdown {
    this.mermaidBlocks.clear();
    this.plantumlBlocks.clear();

    const html = this.md.render(markdown);
    const headings = this.extractHeadings(markdown);
    const title = headings.length > 0 ? headings[0].text : '未命名文件';

    return { html, title, headings };
  }

  /**
   * 解析 Markdown 並渲染所有圖表為 inline base64 圖片
   * mermaid 區塊保留為文字（PDF 中用 puppeteer 渲染）
   * plantuml 區塊透過 plantuml.jar 渲染為 PNG
   */
  async parse(markdown: string): Promise<ParsedMarkdown> {
    const result = this.parseSync(markdown);
    let html = result.html;

    // 渲染 PlantUML 區塊
    for (const [id, content] of this.plantumlBlocks) {
      try {
        const dataUri = await plantumlRenderer.renderToBase64(content);
        html = html.replace(
          `<div class="plantuml-placeholder" data-id="${id}"></div>`,
          `<div class="diagram plantuml-diagram"><img src="${dataUri}" alt="PlantUML Diagram" /></div>`
        );
      } catch (err) {
        console.warn(`[PlantUML] 渲染失敗 (${id}): ${err}`);
        html = html.replace(
          `<div class="plantuml-placeholder" data-id="${id}"></div>`,
          `<div class="diagram-error">PlantUML 渲染失敗</div>`
        );
      }
    }

    // Mermaid 區塊：轉為 mermaid class div（puppeteer 中用 mermaid.js 渲染）
    for (const [id, content] of this.mermaidBlocks) {
      html = html.replace(
        `<div class="mermaid-placeholder" data-id="${id}">${this.escapeHtml(content)}</div>`,
        `<div class="mermaid">${content}</div>`
      );
    }

    return { ...result, html };
  }

  /** 提取 heading 清單（用於生成目錄） */
  private extractHeadings(markdown: string): Heading[] {
    const headings: Heading[] = [];
    const lines = markdown.split('\n');
    let inCodeBlock = false;

    for (const line of lines) {
      if (line.trim().startsWith('```')) {
        inCodeBlock = !inCodeBlock;
        continue;
      }
      if (inCodeBlock) continue;

      const match = line.match(/^(#{1,6})\s+(.+)$/);
      if (match) {
        const level = match[1].length;
        const text = match[2].trim();
        const id = this.slugify(text);
        headings.push({ level, text, id });
      }
    }
    return headings;
  }

  /** 生成 slug（用於錨點） */
  private slugify(text: string): string {
    return text
      .toLowerCase()
      .replace(/[^\w\u4e00-\u9fff]+/g, '-')
      .replace(/^-|-$/g, '');
  }

  private escapeHtml(str: string): string {
    return str
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }
}

export const markdownParser = new MarkdownParser();
