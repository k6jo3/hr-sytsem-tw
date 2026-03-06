import puppeteer, { Browser, Page } from 'puppeteer';
import fs from 'fs/promises';
import path from 'path';
import { DOC_META } from '../config.js';

/** PDF 生成選項 */
export interface PdfOptions {
  /** 輸出檔案路徑 */
  outputPath: string;
  /** 頁首文字（左側） */
  headerText?: string;
  /** 是否顯示頁碼（預設 true） */
  showPageNumber?: boolean;
  /** 封面頁數量（這些頁不顯示頁碼，預設 1） */
  coverPages?: number;
  /** A4 橫向（預設 false，即直向） */
  landscape?: boolean;
  /** 等待 mermaid 渲染的時間（毫秒） */
  mermaidWaitMs?: number;
}

/**
 * PDF 生成器
 * HTML → PDF（含頁首/頁尾/頁碼）
 */
export class PdfGenerator {
  private browser: Browser | null = null;

  /** 啟動瀏覽器（可重複使用） */
  async launch(): Promise<void> {
    if (!this.browser) {
      this.browser = await puppeteer.launch({
        headless: true,
        args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-gpu'],
      });
    }
  }

  /** 關閉瀏覽器 */
  async close(): Promise<void> {
    if (this.browser) {
      await this.browser.close();
      this.browser = null;
    }
  }

  /** 從 HTML 字串生成 PDF */
  async generateFromHtml(html: string, options: PdfOptions): Promise<string> {
    await this.launch();
    const page = await this.browser!.newPage();

    try {
      // 設定內容
      await page.setContent(html, {
        waitUntil: 'networkidle0',
        timeout: 120000,
      });

      // 等待 mermaid 渲染完成
      if (options.mermaidWaitMs) {
        await new Promise(r => setTimeout(r, options.mermaidWaitMs));
      }

      // 等待 mermaid SVG 渲染（如果有 mermaid 區塊）
      // 使用字串 evaluate 避免 tsx/esbuild 注入 __name helper
      await page.evaluate(`
        new Promise(function(resolve) {
          var divs = document.querySelectorAll('.mermaid');
          if (divs.length === 0) { resolve(); return; }
          var attempts = 0;
          function check() {
            var all = true;
            divs.forEach(function(d) { if (!d.querySelector('svg')) all = false; });
            if (all || attempts > 50) { resolve(); }
            else { attempts++; setTimeout(check, 200); }
          }
          setTimeout(check, 1000);
        })
      `);

      // 確保輸出目錄存在
      await fs.mkdir(path.dirname(options.outputPath), { recursive: true });

      const headerText = options.headerText || DOC_META.company;

      // 生成 PDF
      await page.pdf({
        path: options.outputPath,
        format: 'A4',
        landscape: options.landscape || false,
        printBackground: true,
        margin: {
          top: '80px',
          bottom: '60px',
          left: '50px',
          right: '50px',
        },
        displayHeaderFooter: true,
        headerTemplate: `
          <div style="font-family: 'Microsoft JhengHei', sans-serif; font-size: 8px; color: #999; width: 100%; padding: 0 50px; display: flex; justify-content: space-between;">
            <span>${headerText}</span>
            <span>${DOC_META.date}</span>
          </div>`,
        footerTemplate: `
          <div style="font-family: 'Microsoft JhengHei', sans-serif; font-size: 8px; color: #999; width: 100%; text-align: center; padding: 0 50px;">
            <span class="pageNumber"></span> / <span class="totalPages"></span>
          </div>`,
      });

      console.log(`[PDF] 已生成: ${path.basename(options.outputPath)}`);
      return options.outputPath;
    } finally {
      await page.close();
    }
  }

  /** 從 HTML 檔案生成 PDF */
  async generateFromFile(htmlPath: string, options: PdfOptions): Promise<string> {
    const html = await fs.readFile(htmlPath, 'utf-8');
    return this.generateFromHtml(html, options);
  }
}

/** 全域共用實例 */
export const pdfGenerator = new PdfGenerator();
