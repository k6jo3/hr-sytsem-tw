# 作品集文件待辦工項

> 最後更新：2026-02-24

## 待完成

- [x] **#8** 補齊 Use Case Descriptions — `03_系統使用案例圖與規格.md`（UC01~UC12 全部完成）
- [x] **#9** 建立作品介紹腳本 — `07_作品介紹腳本.md`（2 分鐘版 + 5 分鐘版 + 常見問答）
- [x] **#10** 建立技術決策紀錄 — `08_技術決策紀錄.md`（ADR-01~ADR-08，8 個決策）
- [x] **#11** 補充核心業務循序圖 — `01_核心業務循序圖.md`（新增：請假簽核、薪資 SAGA 補償、員工離職，共 4 個流程）
- [x] **#12** 建立測試架構亮點文件 — `09_測試架構與品質保證.md`

## 已完成

- [x] **#7** Use Case Diagram 改用 PlantUML 標準格式（`03_系統使用案例圖與規格.puml`）
- [x] Mermaid 批次渲染腳本（`generate_diagrams.sh`）
- [x] 架構圖 `SupportDomain` 節點 bug 修正
- [x] PlantUML JAR 下載（`tools/plantuml.jar` v1.2024.8，不需 GraphViz）

## 圖表渲染指令

```bash
# 所有 Mermaid 圖表（在 knowledge/ 目錄執行）
bash generate_diagrams.sh

# Use Case 圖（PlantUML，不需 GraphViz）
java -jar tools/plantuml.jar -smetana -o diagrams 03_系統使用案例圖與規格.puml
```
