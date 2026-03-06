import { ServiceScanResult, PackageDependency } from './java-scanner.js';
import { plantumlRenderer } from './plantuml-renderer.js';
import fs from 'fs/promises';
import path from 'path';

/**
 * UML 圖生成器
 * 將 Java 掃描結果轉為 PlantUML Package Diagram
 */
export class UmlGenerator {

  /** 生成單一服務的 Package Diagram PlantUML 文字 */
  generateServiceUml(scanResult: ServiceScanResult): string {
    const { service, packages, dependencies } = scanResult;
    const lines: string[] = [];

    lines.push('@startuml');
    lines.push(`title ${service.code} ${service.name} — Package Diagram`);
    lines.push('skinparam packageStyle rectangle');
    lines.push('skinparam backgroundColor #FEFEFE');
    lines.push('left to right direction');
    lines.push('');

    // DDD 層級分組
    const layers: Record<string, string[]> = {
      interfaces: [],
      application: [],
      domain: [],
      infrastructure: [],
      config: [],
    };

    for (const pkg of packages) {
      const shortPkg = pkg.replace(`${service.pkg}.`, '');
      if (shortPkg.startsWith('interfaces')) layers.interfaces.push(shortPkg);
      else if (shortPkg.startsWith('application')) layers.application.push(shortPkg);
      else if (shortPkg.startsWith('domain')) layers.domain.push(shortPkg);
      else if (shortPkg.startsWith('infrastructure')) layers.infrastructure.push(shortPkg);
      else if (shortPkg.startsWith('config')) layers.config.push(shortPkg);
    }

    // 輸出分層
    const layerColors: Record<string, string> = {
      interfaces: '#E3F2FD',
      application: '#FFF3E0',
      domain: '#E8F5E9',
      infrastructure: '#F3E5F5',
      config: '#FFF9C4',
    };

    const layerNames: Record<string, string> = {
      interfaces: '介面層 (Interface)',
      application: '應用層 (Application)',
      domain: '領域層 (Domain)',
      infrastructure: '基礎設施層 (Infrastructure)',
      config: '配置 (Config)',
    };

    for (const [layer, pkgs] of Object.entries(layers)) {
      if (pkgs.length === 0) continue;
      lines.push(`package "${layerNames[layer]}" ${layerColors[layer]} {`);
      for (const pkg of pkgs) {
        const alias = pkg.replace(/\./g, '_');
        const shortName = pkg.split('.').pop() || pkg;
        lines.push(`  package "${shortName}" as ${alias}`);
      }
      lines.push('}');
      lines.push('');
    }

    // 依賴關係（只取前 20 個最頻繁的）
    const topDeps = dependencies.slice(0, 20);
    for (const dep of topDeps) {
      const fromAlias = dep.from.replace(/\./g, '_');
      const toAlias = dep.to.replace(/\./g, '_');
      lines.push(`${fromAlias} --> ${toAlias}`);
    }

    lines.push('@enduml');
    return lines.join('\n');
  }

  /** 生成跨服務總覽圖 */
  generateOverviewUml(scanResults: ServiceScanResult[]): string {
    const lines: string[] = [];

    lines.push('@startuml');
    lines.push('title HRMS 微服務架構 — Package 總覽');
    lines.push('skinparam packageStyle rectangle');
    lines.push('skinparam backgroundColor #FEFEFE');
    lines.push('');

    for (const result of scanResults) {
      const { service, layerCounts } = result;
      const total = Object.values(layerCounts).reduce((a, b) => a + b, 0);
      const alias = `svc_${service.code}`;
      lines.push(`package "${service.code} ${service.name}\\n(${total} files)" as ${alias} #E3F2FD {`);

      const layerOrder = ['interface', 'application', 'domain', 'infrastructure'];
      for (const layer of layerOrder) {
        const count = layerCounts[layer] || 0;
        if (count > 0) {
          lines.push(`  [${layer} (${count})] as ${alias}_${layer}`);
        }
      }
      lines.push('}');
      lines.push('');
    }

    // 跨服務依賴（透過共用事件和 common）
    lines.push(`note bottom`);
    lines.push('跨服務通訊透過 Kafka Event-Driven');
    lines.push('共用模組：hrms-common');
    lines.push('end note');

    lines.push('@enduml');
    return lines.join('\n');
  }

  /** 渲染服務 UML 為 PNG 檔案 */
  async renderServiceUml(scanResult: ServiceScanResult, outputDir: string): Promise<string> {
    const puml = this.generateServiceUml(scanResult);
    const buffer = await plantumlRenderer.renderText(puml);
    const outputPath = path.join(outputDir, `${scanResult.service.code}_${scanResult.service.id}_package.png`);
    await fs.mkdir(outputDir, { recursive: true });
    await fs.writeFile(outputPath, buffer);
    console.log(`[UML] 已生成: ${path.basename(outputPath)}`);
    return outputPath;
  }

  /** 渲染總覽圖為 PNG 檔案 */
  async renderOverviewUml(scanResults: ServiceScanResult[], outputDir: string): Promise<string> {
    const puml = this.generateOverviewUml(scanResults);
    const buffer = await plantumlRenderer.renderText(puml);
    const outputPath = path.join(outputDir, 'overview_package.png');
    await fs.mkdir(outputDir, { recursive: true });
    await fs.writeFile(outputPath, buffer);
    console.log(`[UML] 已生成: overview_package.png`);
    return outputPath;
  }
}

export const umlGenerator = new UmlGenerator();
