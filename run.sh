#!/bin/bash
# =============================================================================
# run.sh — Compile and Run any Java class in this project
# =============================================================================
#
# USAGE:
#   ./run.sh <package.ClassName>
#
# EXAMPLES:
#   ./run.sh basics.HelloWorld
#   ./run.sh oops.Inheritance
#   ./run.sh java8features.StreamAPI
#   ./run.sh advanced.Generics
#
# HOW IT WORKS:
#   1. Converts "package.ClassName" to "src/package/ClassName.java"
#   2. Compiles that file (plus all files in the same package) into out/
#   3. Runs the compiled class with the correct classpath
#
# REQUIREMENTS:
#   - Java JDK 17+ installed
#   - Run this script from the project root directory (JavaUnderstanding/)
# =============================================================================

set -e  # Exit immediately if any command fails

# ── Colours for output ──────────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'   # No Colour

# ── Check argument ───────────────────────────────────────────────────────────
if [ -z "$1" ]; then
    echo -e "${RED}❌  Error: No class specified.${NC}"
    echo ""
    echo -e "  ${YELLOW}Usage:${NC}  ./run.sh <package.ClassName>"
    echo ""
    echo -e "  ${CYAN}Examples:${NC}"
    echo "    ./run.sh basics.HelloWorld"
    echo "    ./run.sh oops.Polymorphism"
    echo "    ./run.sh java8features.StreamAPI"
    echo ""
    exit 1
fi

FULL_CLASS="$1"   # e.g. "basics.HelloWorld"

# ── Derive package and class name ────────────────────────────────────────────
# Split on the last dot
PACKAGE=$(echo "$FULL_CLASS" | rev | cut -d'.' -f2- | rev)   # e.g. "basics"
CLASS=$(echo "$FULL_CLASS" | rev | cut -d'.' -f1 | rev)       # e.g. "HelloWorld"

if [ -z "$PACKAGE" ] || [ -z "$CLASS" ]; then
    echo -e "${RED}❌  Error: Please use the format 'package.ClassName'${NC}"
    echo -e "    e.g. ./run.sh ${CYAN}basics.HelloWorld${NC}"
    exit 1
fi

# Convert package name to path (handles nested packages like java8features)
PACKAGE_PATH=$(echo "$PACKAGE" | tr '.' '/')   # e.g. "java8features"
SOURCE_FILE="src/${PACKAGE_PATH}/${CLASS}.java"
OUT_DIR="out"

# ── Verify source file exists ────────────────────────────────────────────────
if [ ! -f "$SOURCE_FILE" ]; then
    echo -e "${RED}❌  Error: Source file not found: ${SOURCE_FILE}${NC}"
    echo ""
    echo "  Available packages:"
    for pkg in src/*/; do
        echo "    - $(basename "$pkg")"
    done
    exit 1
fi

# ── Check Java is available ──────────────────────────────────────────────────
if ! command -v javac &> /dev/null; then
    echo -e "${RED}❌  Error: 'javac' not found. Java JDK is not installed or not in PATH.${NC}"
    echo ""
    echo -e "  ${YELLOW}Fix (Apple Silicon Mac):${NC}"
    echo "    brew install openjdk@17"
    echo "    echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17' >> ~/.zshrc"
    echo "    echo 'export PATH=\"\$JAVA_HOME/bin:\$PATH\"' >> ~/.zshrc"
    echo "    source ~/.zshrc"
    exit 1
fi

# ── Create output directory ──────────────────────────────────────────────────
mkdir -p "$OUT_DIR"

# ── Compile ──────────────────────────────────────────────────────────────────
echo -e "${BLUE}🔨 Compiling ${CYAN}${SOURCE_FILE}${NC} ..."

# Compile all .java files in the same package directory to handle dependencies
if javac -encoding UTF-8 -d "$OUT_DIR" "src/${PACKAGE_PATH}/"*.java 2>&1; then
    echo -e "${GREEN}✅  Compilation successful!${NC}"
else
    echo -e "${RED}❌  Compilation failed. Check the error messages above.${NC}"
    exit 1
fi

echo ""

# ── Run ──────────────────────────────────────────────────────────────────────
echo -e "${BLUE}🚀 Running ${CYAN}${FULL_CLASS}${NC} ..."
echo -e "${YELLOW}─────────────────────────────────────────────${NC}"
echo ""

java -cp "$OUT_DIR" "$FULL_CLASS"

echo ""
echo -e "${YELLOW}─────────────────────────────────────────────${NC}"
echo -e "${GREEN}✅  Done!${NC}"
