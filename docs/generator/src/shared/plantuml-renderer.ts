import { execFile, spawn } from 'child_process';
import { promisify } from 'util';
import fs from 'fs/promises';
import path from 'path';
import { PLANTUML_JAR } from '../config.js';

const execFileAsync = promisify(execFile);

/**
 * PlantUML 渲染器
 * 將 .puml 檔案或 PlantUML 文字轉為 PNG
 */
export class PlantUmlRenderer {
  private jarPath: string;

  constructor(jarPath: string = PLANTUML_JAR) {
    this.jarPath = jarPath;
  }

  /** 驗證 plantuml.jar 存在 */
  async verify(): Promise<boolean> {
    try {
      await fs.access(this.jarPath);
      return true;
    } catch {
      console.warn(`[PlantUML] jar 不存在: ${this.jarPath}`);
      return false;
    }
  }

  /** 渲染 .puml 檔案為 PNG，回傳 PNG 路徑 */
  async renderFile(pumlPath: string, outputDir?: string): Promise<string> {
    const dir = outputDir || path.dirname(pumlPath);
    const baseName = path.basename(pumlPath, '.puml');
    const outputPath = path.join(dir, `${baseName}.png`);

    await execFileAsync('java', [
      '-jar', this.jarPath,
      '-tpng',
      '-smetana',
      '-charset', 'UTF-8',
      '-o', dir,
      pumlPath,
    ], { timeout: 60000 });

    return outputPath;
  }

  /** 渲染 PlantUML 文字為 PNG Buffer（使用 pipe 模式） */
  async renderText(pumlContent: string): Promise<Buffer> {
    // 確保內容有 @startuml/@enduml
    let content = pumlContent.trim();
    if (!content.startsWith('@start')) {
      content = `@startuml\n${content}\n@enduml`;
    }

    return new Promise((resolve, reject) => {
      const proc = spawn('java', [
        '-jar', this.jarPath,
        '-tpng',
        '-smetana',
        '-charset', 'UTF-8',
        '-pipe',
      ], { timeout: 60000 });

      const chunks: Buffer[] = [];
      const errChunks: Buffer[] = [];

      proc.stdout.on('data', (chunk: Buffer) => chunks.push(chunk));
      proc.stderr.on('data', (chunk: Buffer) => errChunks.push(chunk));

      proc.on('close', (code) => {
        if (code === 0 && chunks.length > 0) {
          resolve(Buffer.concat(chunks));
        } else {
          const stderr = Buffer.concat(errChunks).toString('utf-8');
          reject(new Error(`PlantUML pipe 渲染失敗 (code=${code}): ${stderr}`));
        }
      });

      proc.on('error', (err) => {
        reject(new Error(`PlantUML 執行失敗: ${err.message}`));
      });

      // 將內容寫入 stdin
      proc.stdin.write(content, 'utf-8');
      proc.stdin.end();
    });
  }

  /** 渲染 PlantUML 文字為 base64 data URI */
  async renderToBase64(pumlContent: string): Promise<string> {
    const buffer = await this.renderText(pumlContent);
    return `data:image/png;base64,${buffer.toString('base64')}`;
  }
}

export const plantumlRenderer = new PlantUmlRenderer();
