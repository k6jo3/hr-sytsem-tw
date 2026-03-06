import PptxGenJS from 'pptxgenjs';
import fs from 'fs/promises';
import path from 'path';
import { DOC_META } from '../config.js';

/** 投影片版型 */
export type SlideLayout = 'cover' | 'content' | 'section' | 'image' | 'table' | 'twoColumn';

/** 投影片內容 */
export interface SlideContent {
  layout: SlideLayout;
  title: string;
  subtitle?: string;
  body?: string;        // 文字內容（支援換行）
  bullets?: string[];   // 項目符號清單
  imagePath?: string;   // 圖片路徑
  imageBase64?: string; // base64 圖片
  tableData?: string[][]; // 表格資料 [row][col]
  leftContent?: string;
  rightContent?: string;
  notes?: string;
}

/** 顏色配置 */
const COLORS = {
  primary: '1A365D',
  primaryLight: '2C5282',
  accent: '2B6CB0',
  white: 'FFFFFF',
  text: '1A202C',
  textLight: '4A5568',
  bgLight: 'F7FAFC',
  border: 'E2E8F0',
};

/**
 * PPT 生成器
 * 16:9 比例，封面/內容/圖表/表格 4 種版型
 */
export class PptxGenerator {

  /** 建立新的簡報並加入投影片 */
  async generate(slides: SlideContent[], outputPath: string): Promise<string> {
    const pptx = new PptxGenJS();

    // 簡報設定
    pptx.defineLayout({ name: 'HRMS', width: 13.33, height: 7.5 });
    pptx.layout = 'HRMS';
    pptx.author = DOC_META.author;
    pptx.company = DOC_META.company;
    pptx.title = slides[0]?.title || DOC_META.company;

    for (const slideContent of slides) {
      this.addSlide(pptx, slideContent);
    }

    // 確保輸出目錄存在
    await fs.mkdir(path.dirname(outputPath), { recursive: true });

    // 寫入檔案
    const buffer = await pptx.write({ outputType: 'nodebuffer' }) as Buffer;
    await fs.writeFile(outputPath, buffer);

    console.log(`[PPTX] 已生成: ${path.basename(outputPath)}`);
    return outputPath;
  }

  private addSlide(pptx: PptxGenJS, content: SlideContent): void {
    switch (content.layout) {
      case 'cover':
        this.addCoverSlide(pptx, content);
        break;
      case 'section':
        this.addSectionSlide(pptx, content);
        break;
      case 'content':
        this.addContentSlide(pptx, content);
        break;
      case 'image':
        this.addImageSlide(pptx, content);
        break;
      case 'table':
        this.addTableSlide(pptx, content);
        break;
      case 'twoColumn':
        this.addTwoColumnSlide(pptx, content);
        break;
    }
  }

  /** 封面投影片 */
  private addCoverSlide(pptx: PptxGenJS, content: SlideContent): void {
    const slide = pptx.addSlide();
    slide.background = { color: COLORS.primary };

    // HRMS Logo
    slide.addText('HRMS', {
      x: 0.5, y: 0.8, w: 12.33, h: 1,
      fontSize: 48, fontFace: 'Microsoft JhengHei',
      color: COLORS.white, bold: true,
      align: 'center',
    });

    // 標題
    slide.addText(content.title, {
      x: 1, y: 2.5, w: 11.33, h: 1.5,
      fontSize: 36, fontFace: 'Microsoft JhengHei',
      color: COLORS.white, bold: true,
      align: 'center',
    });

    // 副標題
    if (content.subtitle) {
      slide.addText(content.subtitle, {
        x: 1, y: 4, w: 11.33, h: 0.8,
        fontSize: 18, fontFace: 'Microsoft JhengHei',
        color: 'A0AEC0', align: 'center',
      });
    }

    // 底部資訊
    slide.addText(`${DOC_META.author}　|　${DOC_META.date}　|　v${DOC_META.version}`, {
      x: 1, y: 6.2, w: 11.33, h: 0.5,
      fontSize: 12, fontFace: 'Microsoft JhengHei',
      color: '718096', align: 'center',
    });
  }

  /** 章節分隔頁 */
  private addSectionSlide(pptx: PptxGenJS, content: SlideContent): void {
    const slide = pptx.addSlide();
    slide.background = { color: COLORS.primaryLight };

    slide.addText(content.title, {
      x: 1, y: 2.5, w: 11.33, h: 1.5,
      fontSize: 36, fontFace: 'Microsoft JhengHei',
      color: COLORS.white, bold: true,
      align: 'center',
    });

    if (content.subtitle) {
      slide.addText(content.subtitle, {
        x: 1, y: 4.2, w: 11.33, h: 0.8,
        fontSize: 16, fontFace: 'Microsoft JhengHei',
        color: 'A0AEC0', align: 'center',
      });
    }
  }

  /** 內容投影片（文字 + 項目符號） */
  private addContentSlide(pptx: PptxGenJS, content: SlideContent): void {
    const slide = pptx.addSlide();
    this.addSlideHeader(slide, content.title);

    let yPos = 1.5;

    // 文字內容
    if (content.body) {
      slide.addText(content.body, {
        x: 0.8, y: yPos, w: 11.73, h: 5,
        fontSize: 14, fontFace: 'Microsoft JhengHei',
        color: COLORS.text, valign: 'top',
        lineSpacingMultiple: 1.5,
      });
    }

    // 項目符號
    if (content.bullets && content.bullets.length > 0) {
      const textItems = content.bullets.map(text => ({
        text,
        options: {
          fontSize: 14,
          fontFace: 'Microsoft JhengHei',
          color: COLORS.text,
          bullet: { type: 'bullet' as const },
          lineSpacingMultiple: 1.5,
        },
      }));

      slide.addText(textItems, {
        x: 0.8, y: yPos, w: 11.73, h: 5,
        valign: 'top',
      });
    }

    this.addSlideFooter(slide);
  }

  /** 圖片投影片 */
  private addImageSlide(pptx: PptxGenJS, content: SlideContent): void {
    const slide = pptx.addSlide();
    this.addSlideHeader(slide, content.title);

    if (content.imagePath) {
      slide.addImage({
        path: content.imagePath,
        x: 0.5, y: 1.5, w: 12.33, h: 5.5,
        sizing: { type: 'contain', w: 12.33, h: 5.5 },
      });
    } else if (content.imageBase64) {
      slide.addImage({
        data: content.imageBase64,
        x: 0.5, y: 1.5, w: 12.33, h: 5.5,
        sizing: { type: 'contain', w: 12.33, h: 5.5 },
      });
    }

    this.addSlideFooter(slide);
  }

  /** 表格投影片 */
  private addTableSlide(pptx: PptxGenJS, content: SlideContent): void {
    const slide = pptx.addSlide();
    this.addSlideHeader(slide, content.title);

    if (content.tableData && content.tableData.length > 0) {
      const rows: PptxGenJS.TableRow[] = content.tableData.map((row, idx) => {
        return row.map(cell => ({
          text: cell,
          options: {
            fontSize: idx === 0 ? 11 : 10,
            fontFace: 'Microsoft JhengHei',
            color: idx === 0 ? COLORS.white : COLORS.text,
            fill: { color: idx === 0 ? COLORS.primary : (idx % 2 === 0 ? COLORS.bgLight : COLORS.white) },
            border: { type: 'solid' as const, pt: 0.5, color: COLORS.border },
            valign: 'middle' as const,
            bold: idx === 0,
          },
        }));
      });

      slide.addTable(rows, {
        x: 0.5, y: 1.5, w: 12.33,
        colW: Array(content.tableData[0].length).fill(12.33 / content.tableData[0].length),
        border: { type: 'solid', pt: 0.5, color: COLORS.border },
        autoPage: true,
        autoPageRepeatHeader: true,
      });
    }

    this.addSlideFooter(slide);
  }

  /** 雙欄投影片 */
  private addTwoColumnSlide(pptx: PptxGenJS, content: SlideContent): void {
    const slide = pptx.addSlide();
    this.addSlideHeader(slide, content.title);

    if (content.leftContent) {
      slide.addText(content.leftContent, {
        x: 0.5, y: 1.5, w: 5.9, h: 5.5,
        fontSize: 12, fontFace: 'Microsoft JhengHei',
        color: COLORS.text, valign: 'top',
        lineSpacingMultiple: 1.4,
      });
    }

    if (content.rightContent) {
      slide.addText(content.rightContent, {
        x: 6.9, y: 1.5, w: 5.9, h: 5.5,
        fontSize: 12, fontFace: 'Microsoft JhengHei',
        color: COLORS.text, valign: 'top',
        lineSpacingMultiple: 1.4,
      });
    }

    this.addSlideFooter(slide);
  }

  /** 投影片標題列 */
  private addSlideHeader(slide: PptxGenJS.Slide, title: string): void {
    // 底部分隔線
    slide.addShape('rect' as unknown as PptxGenJS.ShapeType, {
      x: 0, y: 0, w: 13.33, h: 1.2,
      fill: { color: COLORS.primary },
    });

    slide.addText(title, {
      x: 0.5, y: 0.15, w: 12.33, h: 0.9,
      fontSize: 24, fontFace: 'Microsoft JhengHei',
      color: COLORS.white, bold: true,
      valign: 'middle',
    });
  }

  /** 投影片頁尾 */
  private addSlideFooter(slide: PptxGenJS.Slide): void {
    slide.addText(DOC_META.company, {
      x: 0.5, y: 7, w: 6, h: 0.4,
      fontSize: 8, fontFace: 'Microsoft JhengHei',
      color: '999999',
    });
  }
}

export const pptxGenerator = new PptxGenerator();
