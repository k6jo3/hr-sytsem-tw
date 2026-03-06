import fs from 'fs/promises';
import path from 'path';
import { BACKEND_DIR, SERVICES, ServiceMeta } from '../config.js';

/** Java 檔案資訊 */
export interface JavaFileInfo {
  /** 檔案路徑（相對於後端目錄） */
  relativePath: string;
  /** Package 名稱 */
  packageName: string;
  /** 類別名稱 */
  className: string;
  /** import 的 package 清單 */
  imports: string[];
  /** DDD 層級 */
  layer: 'interface' | 'application' | 'domain' | 'infrastructure' | 'config' | 'unknown';
}

/** Package 依賴關係 */
export interface PackageDependency {
  from: string;  // 來源 package
  to: string;    // 目標 package
  count: number; // 引用次數
}

/** 服務掃描結果 */
export interface ServiceScanResult {
  service: ServiceMeta;
  files: JavaFileInfo[];
  packages: string[];
  dependencies: PackageDependency[];
  layerCounts: Record<string, number>;
}

/**
 * Java 原始碼掃描器
 * 掃描 package/import 語句，建立依賴關係
 */
export class JavaScanner {

  /** 掃描單一服務的所有 Java 檔案 */
  async scanService(service: ServiceMeta): Promise<ServiceScanResult> {
    const serviceDir = path.join(BACKEND_DIR, `hrms-${service.id}`);
    const javaFiles = await this.findJavaFiles(serviceDir);
    const files: JavaFileInfo[] = [];

    for (const filePath of javaFiles) {
      try {
        const info = await this.parseJavaFile(filePath, serviceDir);
        files.push(info);
      } catch (err) {
        console.warn(`[JavaScanner] 解析失敗: ${filePath}`);
      }
    }

    const packages = [...new Set(files.map(f => f.packageName))].sort();
    const dependencies = this.buildDependencies(files, service.pkg);
    const layerCounts = this.countLayers(files);

    return { service, files, packages, dependencies, layerCounts };
  }

  /** 掃描全部 14 個服務 */
  async scanAll(): Promise<ServiceScanResult[]> {
    const results: ServiceScanResult[] = [];
    for (const service of SERVICES) {
      try {
        const result = await this.scanService(service);
        results.push(result);
        console.log(`[JavaScanner] ${service.code} ${service.name}: ${result.files.length} 檔案, ${result.packages.length} packages`);
      } catch (err) {
        console.warn(`[JavaScanner] 服務掃描失敗 ${service.id}: ${err}`);
      }
    }
    return results;
  }

  /** 遞迴找出所有 .java 檔案 */
  private async findJavaFiles(dir: string): Promise<string[]> {
    const results: string[] = [];
    try {
      const entries = await fs.readdir(dir, { withFileTypes: true });
      for (const entry of entries) {
        const fullPath = path.join(dir, entry.name);
        if (entry.isDirectory() && !entry.name.startsWith('.') && entry.name !== 'target' && entry.name !== 'node_modules') {
          results.push(...await this.findJavaFiles(fullPath));
        } else if (entry.isFile() && entry.name.endsWith('.java')) {
          results.push(fullPath);
        }
      }
    } catch { /* 目錄不存在 */ }
    return results;
  }

  /** 解析單一 Java 檔案 */
  private async parseJavaFile(filePath: string, baseDir: string): Promise<JavaFileInfo> {
    const content = await fs.readFile(filePath, 'utf-8');
    const relativePath = path.relative(baseDir, filePath).replace(/\\/g, '/');
    const className = path.basename(filePath, '.java');

    // 解析 package
    const packageMatch = content.match(/^package\s+([\w.]+)\s*;/m);
    const packageName = packageMatch ? packageMatch[1] : '';

    // 解析 import
    const imports: string[] = [];
    const importRegex = /^import\s+([\w.]+)\s*;/gm;
    let match;
    while ((match = importRegex.exec(content)) !== null) {
      imports.push(match[1]);
    }

    // 判斷 DDD 層級
    const layer = this.detectLayer(relativePath, packageName);

    return { relativePath, packageName, className, imports, layer };
  }

  /** 偵測 DDD 層級 */
  private detectLayer(relativePath: string, packageName: string): JavaFileInfo['layer'] {
    const lowerPath = relativePath.toLowerCase();
    const lowerPkg = packageName.toLowerCase();

    if (lowerPath.includes('/interfaces/') || lowerPath.includes('/controller/') || lowerPkg.includes('.interfaces.')) return 'interface';
    if (lowerPath.includes('/application/') || lowerPkg.includes('.application.')) return 'application';
    if (lowerPath.includes('/domain/') || lowerPkg.includes('.domain.')) return 'domain';
    if (lowerPath.includes('/infrastructure/') || lowerPath.includes('/dao/') || lowerPkg.includes('.infrastructure.')) return 'infrastructure';
    if (lowerPath.includes('/config/') || lowerPkg.includes('.config')) return 'config';
    return 'unknown';
  }

  /** 建立 package 之間的依賴關係 */
  private buildDependencies(files: JavaFileInfo[], basePackage: string): PackageDependency[] {
    const depMap = new Map<string, number>();

    for (const file of files) {
      const fromPkg = this.shortenPackage(file.packageName, basePackage);
      for (const imp of file.imports) {
        if (imp.startsWith(basePackage)) {
          const toPkg = this.shortenPackage(this.getPackageFromImport(imp), basePackage);
          if (fromPkg !== toPkg) {
            const key = `${fromPkg}->${toPkg}`;
            depMap.set(key, (depMap.get(key) || 0) + 1);
          }
        }
      }
    }

    return Array.from(depMap.entries()).map(([key, count]) => {
      const [from, to] = key.split('->');
      return { from, to, count };
    }).sort((a, b) => b.count - a.count);
  }

  /** 縮短 package 名稱（移除 base package 前綴） */
  private shortenPackage(pkg: string, base: string): string {
    if (pkg.startsWith(base + '.')) {
      return pkg.substring(base.length + 1);
    }
    return pkg;
  }

  /** 從 import 語句取得 package（移除類別名） */
  private getPackageFromImport(importStr: string): string {
    const lastDot = importStr.lastIndexOf('.');
    return lastDot > 0 ? importStr.substring(0, lastDot) : importStr;
  }

  /** 統計各層檔案數 */
  private countLayers(files: JavaFileInfo[]): Record<string, number> {
    const counts: Record<string, number> = {};
    for (const file of files) {
      counts[file.layer] = (counts[file.layer] || 0) + 1;
    }
    return counts;
  }
}

export const javaScanner = new JavaScanner();
