#!/bin/bash
# ============================================================
# HRMS 系統文件圖表批次生成腳本
# 使用方式: bash generate_diagrams.sh
# 需求:
#   - npm install -g @mermaid-js/mermaid-cli  (Mermaid 圖表)
#   - Java 17+，已下載 tools/plantuml.jar      (PlantUML 圖表)
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
OUTPUT_DIR="$SCRIPT_DIR/diagrams"
PLANTUML_JAR="$SCRIPT_DIR/tools/plantuml.jar"

mkdir -p "$OUTPUT_DIR"

SUCCESS=0
FAIL=0

# ============================================================
# 一、Mermaid 圖表（架構圖、循序圖、ERD、流程圖、CI/CD）
# ============================================================
echo "🔄 [1/2] 渲染 Mermaid 圖表..."
echo ""

MERMAID_FILES=(
  "00_系統分析與架構總覽.md:00_architecture"
  "01_核心業務循序圖.md:01_sequence"
  "02_跨微服務實體關聯圖.md:02_erd"
  "04_核心業務流程圖.md:04_flowchart"
  "06_CICD與系統部署指南.md:06_cicd"
)

for entry in "${MERMAID_FILES[@]}"; do
  src="${entry%%:*}"
  dst="${entry##*:}"
  echo "📄 處理: $src"
  if mmdc -i "$SCRIPT_DIR/$src" -o "$OUTPUT_DIR/${dst}.png" --scale 2 2>&1; then
    # mmdc 會自動在檔名後加 -1, -2...
    for f in "$OUTPUT_DIR"/${dst}-*.png; do
      [ -f "$f" ] || continue
      suffix="${f##*-}"
      suffix="${suffix%.png}"
      mv "$f" "$OUTPUT_DIR/${dst}-page${suffix}.png" 2>/dev/null || true
    done
    echo "   ✅ 成功"
    ((SUCCESS++))
  else
    echo "   ❌ 失敗"
    ((FAIL++))
  fi
  echo ""
done

# ============================================================
# 二、PlantUML 圖表（Use Case Diagram）
# ============================================================
echo "🔄 [2/2] 渲染 PlantUML 圖表..."
echo ""

PLANTUML_FILES=(
  "03_系統使用案例圖與規格.puml:03_usecase.png"
)

if [ ! -f "$PLANTUML_JAR" ]; then
  echo "⚠️  找不到 PlantUML JAR: $PLANTUML_JAR"
  echo "   請執行: curl -L -o tools/plantuml.jar https://github.com/plantuml/plantuml/releases/download/v1.2024.8/plantuml-1.2024.8.jar"
  ((FAIL++))
else
  for entry in "${PLANTUML_FILES[@]}"; do
    src="${entry%%:*}"
    dst="${entry##*:}"
    echo "📄 處理: $src"
    if java -jar "$PLANTUML_JAR" -smetana -o "$OUTPUT_DIR" "$SCRIPT_DIR/$src" 2>&1; then
      # PlantUML 輸出檔名 = 輸入檔名.png，手動改成目標檔名
      generated="${src%.puml}.png"
      if [ -f "$OUTPUT_DIR/$generated" ] && [ "$generated" != "$dst" ]; then
        mv "$OUTPUT_DIR/$generated" "$OUTPUT_DIR/$dst" 2>/dev/null || true
      fi
      echo "   ✅ 成功 -> $dst"
      ((SUCCESS++))
    else
      echo "   ❌ 失敗"
      ((FAIL++))
    fi
    echo ""
  done
fi

echo "============================================"
echo "✅ 成功: $SUCCESS 個 | ❌ 失敗: $FAIL 個"
echo "📁 輸出目錄: $OUTPUT_DIR"
echo "============================================"
