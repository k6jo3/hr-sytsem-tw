import { Command } from 'commander';
import { generateE1 } from './generators/e1-system-analysis.js';
import { generateE2 } from './generators/e2-requirements.js';
import { generateE3 } from './generators/e3-system-design.js';
import { generateE4 } from './generators/e4-api-specs.js';
import { generateE5 } from './generators/e5-operation-manual.js';
import { generateE6 } from './generators/e6-system-overview.js';
import { generateE7 } from './generators/e7-maintenance-manual.js';
import { pdfGenerator } from './shared/pdf-generator.js';

const program = new Command();

program
  .name('hrms-doc-generator')
  .description('HRMS 文件生成系統 — Markdown → PPT/PDF')
  .version('1.0.0');

program
  .option('--all', '生成全部文件 (E1-E7)')
  .option('--e1', 'E1: 系統分析與架構 (PPT + PDF)')
  .option('--e2', 'E2: 需求分析書 (14 PDF)')
  .option('--e3', 'E3: 系統設計書 (14 PDF)')
  .option('--e4', 'E4: API 規格書 (14 PDF)')
  .option('--e5', 'E5: 系統操作手冊 (PDF)')
  .option('--e6', 'E6: 系統說明簡報 (PPT)')
  .option('--e7', 'E7: 程式維護手冊 (PDF)')
  .option('--service <code>', '指定服務代碼 (01-14)，用於 E2/E3/E4')
  .action(async (opts) => {
    console.log('========================================');
    console.log('  HRMS 文件生成系統 v1.0');
    console.log('========================================\n');

    const startTime = Date.now();
    const serviceFilter = opts.service || undefined;

    try {
      if (opts.all || (!opts.e1 && !opts.e2 && !opts.e3 && !opts.e4 && !opts.e5 && !opts.e6 && !opts.e7)) {
        if (opts.all) {
          console.log('模式: 生成全部文件 (E1-E7)\n');
          await generateE2(serviceFilter);
          await generateE3(serviceFilter);
          await generateE4(serviceFilter);
          await generateE1();
          await generateE5();
          await generateE7();
          await generateE6();
        } else {
          program.help();
          return;
        }
      } else {
        if (opts.e1) await generateE1();
        if (opts.e2) await generateE2(serviceFilter);
        if (opts.e3) await generateE3(serviceFilter);
        if (opts.e4) await generateE4(serviceFilter);
        if (opts.e5) await generateE5();
        if (opts.e6) await generateE6();
        if (opts.e7) await generateE7();
      }

      const elapsed = ((Date.now() - startTime) / 1000).toFixed(1);
      console.log(`\n========================================`);
      console.log(`  完成！耗時 ${elapsed} 秒`);
      console.log(`========================================`);
    } catch (err) {
      console.error('\n生成失敗:', err);
      process.exit(1);
    } finally {
      await pdfGenerator.close();
    }
  });

program.parse();
