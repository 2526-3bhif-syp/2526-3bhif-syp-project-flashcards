#!/bin/bash
# Script to convert SVG icons to PNG format for use with JavaFX
# Usage: ./convert-icons.sh [source_dir] [output_dir]

set -e

# Default directories
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SOURCE_DIR="${1:-$SCRIPT_DIR/src/main/resources/at/htlleonding/flashcards/icons/svg}"
OUTPUT_DIR="${2:-$SCRIPT_DIR/src/main/resources/at/htlleonding/flashcards/icons/png}"

# Color output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if ImageMagick is installed
if ! command -v convert &> /dev/null; then
    echo -e "${RED}Error: ImageMagick is not installed${NC}"
    echo "Please install ImageMagick (e.g., 'sudo apt-get install imagemagick')"
    exit 1
fi

# Check if source directory exists
if [ ! -d "$SOURCE_DIR" ]; then
    echo -e "${RED}Error: Source directory not found: $SOURCE_DIR${NC}"
    exit 1
fi

# Create output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Convert each SVG to PNG
echo -e "${BLUE}Converting SVG icons to PNG...${NC}"
count=0
for svg_file in "$SOURCE_DIR"/*.svg; do
    if [ -f "$svg_file" ]; then
        filename=$(basename "$svg_file" .svg)
        png_file="$OUTPUT_DIR/${filename}.png"
        
        # Convert SVG to PNG with transparent background
        convert "$svg_file" \
            -background none \
            -density 96 \
            -resize 200x200 \
            "$png_file"
        
        echo -e "${GREEN}✓${NC} Converted: $filename.svg → $filename.png"
        ((count++))
    fi
done

if [ $count -eq 0 ]; then
    echo -e "${RED}No SVG files found in $SOURCE_DIR${NC}"
    exit 1
fi

echo -e "${GREEN}Successfully converted $count SVG files${NC}"
echo -e "${BLUE}Output directory: $OUTPUT_DIR${NC}"
