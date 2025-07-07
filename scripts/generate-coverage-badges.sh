#!/bin/bash

# Coverage Badge Generation Script for KMP 2025 Standards
# 
# Features:
# - Generates coverage badges from Kover XML reports
# - Supports different platforms (Android, iOS, Common)
# - Creates badges for different coverage types (line, branch, instruction)
# - Integrates with GitHub Actions and CI/CD pipelines
# - Updates README.md with generated badges
# - Configurable thresholds and styling

set -e

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COVERAGE_DIR="$PROJECT_ROOT/shared/build/reports/kover"
BADGES_DIR="$PROJECT_ROOT/.github/badges"
README_PATH="$PROJECT_ROOT/README.md"

# Coverage thresholds
THRESHOLD_EXCELLENT=90
THRESHOLD_GOOD=80
THRESHOLD_MODERATE=70
THRESHOLD_POOR=60

# Badge colors
COLOR_EXCELLENT="brightgreen"
COLOR_GOOD="green"
COLOR_MODERATE="yellow"
COLOR_POOR="orange"
COLOR_CRITICAL="red"

# Platform configurations
PLATFORMS="android common ios"

# Coverage types
COVERAGE_TYPES="line branch instruction"

# Utility functions
log_info() {
    echo "[INFO] $1"
}

log_warn() {
    echo "[WARN] $1"
}

log_error() {
    echo "[ERROR] $1"
    exit 1
}

# Create directories if they don't exist
create_directories() {
    log_info "Creating necessary directories..."
    mkdir -p "$BADGES_DIR"
    mkdir -p "$COVERAGE_DIR"
}

# Check if required tools are installed
check_dependencies() {
    log_info "Checking dependencies..."
    
    if ! command -v curl &> /dev/null; then
        log_error "curl is required but not installed. Please install curl."
    fi
    
    if ! command -v xmllint &> /dev/null; then
        log_warn "xmllint not found. Will use alternative XML parsing."
    fi
    
    log_info "Dependencies check completed."
}

# Run Kover coverage report generation
generate_coverage_reports() {
    log_info "Generating Kover coverage reports..."
    
    cd "$PROJECT_ROOT"
    
    # Generate coverage for all platforms
    if ./gradlew koverXmlReport; then
        log_info "Coverage reports generated successfully."
    else
        log_error "Failed to generate coverage reports. Please ensure tests pass."
    fi
}

# Parse coverage percentage from XML report
parse_coverage_from_xml() {
    local xml_file="$1"
    local coverage_type="$2"
    
    if [[ ! -f "$xml_file" ]]; then
        echo "0"
        return
    fi
    
    # Extract coverage percentage based on type
    case "$coverage_type" in
        "line")
            if command -v xmllint &> /dev/null; then
                xmllint --xpath "//counter[@type='LINE']/@covered" "$xml_file" 2>/dev/null | sed 's/covered="//g; s/"//g' || echo "0"
            else
                grep -o 'type="LINE"[^>]*covered="[0-9]*"' "$xml_file" | sed 's/.*covered="//; s/".*//' || echo "0"
            fi
            ;;
        "branch")
            if command -v xmllint &> /dev/null; then
                xmllint --xpath "//counter[@type='BRANCH']/@covered" "$xml_file" 2>/dev/null | sed 's/covered="//g; s/"//g' || echo "0"
            else
                grep -o 'type="BRANCH"[^>]*covered="[0-9]*"' "$xml_file" | sed 's/.*covered="//; s/".*//' || echo "0"
            fi
            ;;
        "instruction")
            if command -v xmllint &> /dev/null; then
                xmllint --xpath "//counter[@type='INSTRUCTION']/@covered" "$xml_file" 2>/dev/null | sed 's/covered="//g; s/"//g' || echo "0"
            else
                grep -o 'type="INSTRUCTION"[^>]*covered="[0-9]*"' "$xml_file" | sed 's/.*covered="//; s/".*//' || echo "0"
            fi
            ;;
        *)
            echo "0"
            ;;
    esac
}

# Calculate coverage percentage
calculate_coverage_percentage() {
    local xml_file="$1"
    local coverage_type="$2"
    
    if [[ ! -f "$xml_file" ]]; then
        echo "0"
        return
    fi
    
    local covered missed total percentage
    
    case "$coverage_type" in
        "line")
            if command -v xmllint &> /dev/null; then
                covered=$(xmllint --xpath "//counter[@type='LINE']/@covered" "$xml_file" 2>/dev/null | sed 's/covered="//g; s/"//g' || echo "0")
                missed=$(xmllint --xpath "//counter[@type='LINE']/@missed" "$xml_file" 2>/dev/null | sed 's/missed="//g; s/"//g' || echo "0")
            else
                covered=$(grep -o 'type="LINE"[^>]*covered="[0-9]*"' "$xml_file" | sed 's/.*covered="//; s/".*//' | head -1 || echo "0")
                missed=$(grep -o 'type="LINE"[^>]*missed="[0-9]*"' "$xml_file" | sed 's/.*missed="//; s/".*//' | head -1 || echo "0")
            fi
            ;;
        "branch")
            if command -v xmllint &> /dev/null; then
                covered=$(xmllint --xpath "//counter[@type='BRANCH']/@covered" "$xml_file" 2>/dev/null | sed 's/covered="//g; s/"//g' || echo "0")
                missed=$(xmllint --xpath "//counter[@type='BRANCH']/@missed" "$xml_file" 2>/dev/null | sed 's/missed="//g; s/"//g' || echo "0")
            else
                covered=$(grep -o 'type="BRANCH"[^>]*covered="[0-9]*"' "$xml_file" | sed 's/.*covered="//; s/".*//' | head -1 || echo "0")
                missed=$(grep -o 'type="BRANCH"[^>]*missed="[0-9]*"' "$xml_file" | sed 's/.*missed="//; s/".*//' | head -1 || echo "0")
            fi
            ;;
        "instruction")
            if command -v xmllint &> /dev/null; then
                covered=$(xmllint --xpath "//counter[@type='INSTRUCTION']/@covered" "$xml_file" 2>/dev/null | sed 's/covered="//g; s/"//g' || echo "0")
                missed=$(xmllint --xpath "//counter[@type='INSTRUCTION']/@missed" "$xml_file" 2>/dev/null | sed 's/missed="//g; s/"//g' || echo "0")
            else
                covered=$(grep -o 'type="INSTRUCTION"[^>]*covered="[0-9]*"' "$xml_file" | sed 's/.*covered="//; s/".*//' | head -1 || echo "0")
                missed=$(grep -o 'type="INSTRUCTION"[^>]*missed="[0-9]*"' "$xml_file" | sed 's/.*missed="//; s/".*//' | head -1 || echo "0")
            fi
            ;;
        *)
            echo "0"
            return
            ;;
    esac
    
    # Ensure we have valid numbers
    covered=${covered:-0}
    missed=${missed:-0}
    
    total=$((covered + missed))
    
    if [[ $total -eq 0 ]]; then
        echo "0"
    else
        percentage=$(( (covered * 100) / total ))
        echo "$percentage"
    fi
}

# Get badge color based on coverage percentage
get_badge_color() {
    local percentage="$1"
    
    if [[ $percentage -ge $THRESHOLD_EXCELLENT ]]; then
        echo "$COLOR_EXCELLENT"
    elif [[ $percentage -ge $THRESHOLD_GOOD ]]; then
        echo "$COLOR_GOOD"
    elif [[ $percentage -ge $THRESHOLD_MODERATE ]]; then
        echo "$COLOR_MODERATE"
    elif [[ $percentage -ge $THRESHOLD_POOR ]]; then
        echo "$COLOR_POOR"
    else
        echo "$COLOR_CRITICAL"
    fi
}

# Generate a single badge
generate_badge() {
    local platform="$1"
    local coverage_type="$2"
    local percentage="$3"
    local output_file="$4"
    
    local color
    color=$(get_badge_color "$percentage")
    
    local badge_label="${platform} ${coverage_type}"
    local badge_message="${percentage}%"
    local badge_url="https://img.shields.io/badge/${badge_label}-${badge_message}-${color}?style=flat-square&logo=kotlin"
    
    log_info "Generating badge: $badge_label -> $badge_message ($color)"
    
    if curl -s "$badge_url" > "$output_file"; then
        log_info "Badge saved: $output_file"
    else
        log_warn "Failed to generate badge: $output_file"
    fi
}

# Generate all badges
generate_all_badges() {
    log_info "Generating coverage badges..."
    
    # Overall coverage badge
    local overall_xml="$COVERAGE_DIR/report.xml"
    if [[ -f "$overall_xml" ]]; then
        for coverage_type in $COVERAGE_TYPES; do
            local percentage
            percentage=$(calculate_coverage_percentage "$overall_xml" "$coverage_type")
            
            local badge_file="$BADGES_DIR/coverage-${coverage_type}.svg"
            generate_badge "overall" "$coverage_type" "$percentage" "$badge_file"
        done
    else
        log_warn "Overall coverage report not found: $overall_xml"
    fi
    
    # Platform-specific badges
    for platform in $PLATFORMS; do
        local platform_xml="$COVERAGE_DIR/${platform}/report.xml"
        
        if [[ -f "$platform_xml" ]]; then
            for coverage_type in $COVERAGE_TYPES; do
                local percentage
                percentage=$(calculate_coverage_percentage "$platform_xml" "$coverage_type")
                
                local badge_file="$BADGES_DIR/coverage-${platform}-${coverage_type}.svg"
                generate_badge "$platform" "$coverage_type" "$percentage" "$badge_file"
            done
        else
            log_warn "Platform coverage report not found: $platform_xml"
            
            # Generate placeholder badges for missing platforms
            for coverage_type in $COVERAGE_TYPES; do
                local badge_file="$BADGES_DIR/coverage-${platform}-${coverage_type}.svg"
                generate_badge "$platform" "$coverage_type" "0" "$badge_file"
            done
        fi
    done
}

# Generate comprehensive coverage summary
generate_coverage_summary() {
    log_info "Generating coverage summary..."
    
    local summary_file="$BADGES_DIR/coverage-summary.json"
    local timestamp
    timestamp=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
    
    cat > "$summary_file" << EOF
{
  "timestamp": "$timestamp",
  "project": "WeatherKMP",
  "platforms": {
EOF
    
    local first_platform=true
    for platform in $PLATFORMS; do
        if [[ "$first_platform" = false ]]; then
            echo "," >> "$summary_file"
        fi
        first_platform=false
        
        echo "    \"$platform\": {" >> "$summary_file"
        
        local platform_xml="$COVERAGE_DIR/${platform}/report.xml"
        if [[ ! -f "$platform_xml" ]]; then
            platform_xml="$COVERAGE_DIR/report.xml"
        fi
        
        local first_type=true
        for coverage_type in $COVERAGE_TYPES; do
            if [[ "$first_type" = false ]]; then
                echo "," >> "$summary_file"
            fi
            first_type=false
            
            local percentage
            percentage=$(calculate_coverage_percentage "$platform_xml" "$coverage_type")
            echo "      \"$coverage_type\": $percentage" >> "$summary_file"
        done
        
        echo "    }" >> "$summary_file"
    done
    
    cat >> "$summary_file" << EOF

  },
  "thresholds": {
    "excellent": $THRESHOLD_EXCELLENT,
    "good": $THRESHOLD_GOOD,
    "moderate": $THRESHOLD_MODERATE,
    "poor": $THRESHOLD_POOR
  }
}
EOF
    
    log_info "Coverage summary saved: $summary_file"
}

# Update README with badges
update_readme() {
    log_info "Updating README.md with coverage badges..."
    
    if [[ ! -f "$README_PATH" ]]; then
        log_warn "README.md not found: $README_PATH"
        return
    fi
    
    # Create badges markdown
    local badges_section="## 📊 Coverage Badges

![Overall Line Coverage](.github/badges/coverage-line.svg)
![Overall Branch Coverage](.github/badges/coverage-branch.svg)
![Overall Instruction Coverage](.github/badges/coverage-instruction.svg)

### Platform-Specific Coverage

#### Android
![Android Line Coverage](.github/badges/coverage-android-line.svg)
![Android Branch Coverage](.github/badges/coverage-android-branch.svg)
![Android Instruction Coverage](.github/badges/coverage-android-instruction.svg)

#### Common
![Common Line Coverage](.github/badges/coverage-common-line.svg)
![Common Branch Coverage](.github/badges/coverage-common-branch.svg)
![Common Instruction Coverage](.github/badges/coverage-common-instruction.svg)

#### iOS
![iOS Line Coverage](.github/badges/coverage-ios-line.svg)
![iOS Branch Coverage](.github/badges/coverage-ios-branch.svg)
![iOS Instruction Coverage](.github/badges/coverage-ios-instruction.svg)

---
"
    
    # Create a temporary file for the new README
    local temp_readme
    temp_readme=$(mktemp)
    
    # Check if coverage badges section already exists
    if grep -q "## 📊 Coverage Badges" "$README_PATH"; then
        # Replace existing section
        awk '
            /## 📊 Coverage Badges/,/^---$/ {skip=1}
            !skip {print}
            /^---$/ && skip {skip=0; next}
        ' "$README_PATH" > "$temp_readme"
        
        # Add the new badges section
        echo "$badges_section" >> "$temp_readme"
    else
        # Add badges section after the main heading
        awk '
            NR==1 {print; print ""; print "'"$badges_section"'"; next}
            {print}
        ' "$README_PATH" > "$temp_readme"
    fi
    
    # Replace the original README
    mv "$temp_readme" "$README_PATH"
    
    log_info "README.md updated with coverage badges."
}

# Generate GitHub Actions workflow integration
generate_github_workflow() {
    log_info "Generating GitHub Actions workflow integration..."
    
    local workflow_dir="$PROJECT_ROOT/.github/workflows"
    local workflow_file="$workflow_dir/coverage-badges.yml"
    
    mkdir -p "$workflow_dir"
    
    cat > "$workflow_file" << 'EOF'
name: Generate Coverage Badges

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  coverage:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Setup Gradle
      uses: gradle/gradle-build-action@v2
    
    - name: Run tests and generate coverage
      run: ./gradlew test koverXmlReport
    
    - name: Generate coverage badges
      run: ./scripts/generate-coverage-badges.sh
    
    - name: Commit and push badge changes
      if: github.ref == 'refs/heads/main'
      run: |
        git config --local user.email "action@github.com"
        git config --local user.name "GitHub Action"
        git add .github/badges/
        git add README.md
        git diff --staged --quiet || git commit -m "Update coverage badges 🎯"
        git push
EOF
    
    log_info "GitHub Actions workflow created: $workflow_file"
}

# Validate coverage thresholds
validate_coverage() {
    log_info "Validating coverage against thresholds..."
    
    local overall_xml="$COVERAGE_DIR/report.xml"
    local failed_checks=()
    
    if [[ -f "$overall_xml" ]]; then
        for coverage_type in $COVERAGE_TYPES; do
            local percentage
            percentage=$(calculate_coverage_percentage "$overall_xml" "$coverage_type")
            
            if [[ $percentage -lt $THRESHOLD_POOR ]]; then
                failed_checks+=("Overall $coverage_type coverage ($percentage%) is below threshold ($THRESHOLD_POOR%)")
            fi
        done
    fi
    
    if [[ ${#failed_checks[@]} -gt 0 ]]; then
        log_warn "Coverage validation warnings:"
        for check in "${failed_checks[@]}"; do
            log_warn "  - $check"
        done
    else
        log_info "All coverage checks passed! 🎉"
    fi
}

# Print usage information
print_usage() {
    cat << EOF
Usage: $0 [options]

Options:
  --help, -h              Show this help message
  --skip-tests           Skip running tests, use existing coverage reports
  --skip-readme          Skip updating README.md
  --skip-workflow        Skip generating GitHub Actions workflow
  --threshold-excellent N Set excellent threshold (default: 90)
  --threshold-good N     Set good threshold (default: 80)
  --threshold-moderate N Set moderate threshold (default: 70)
  --threshold-poor N     Set poor threshold (default: 60)

Examples:
  $0                     # Full coverage badge generation
  $0 --skip-tests        # Use existing coverage reports
  $0 --threshold-good 85 # Set good threshold to 85%
EOF
}

# Main execution function
main() {
    local skip_tests=false
    local skip_readme=false
    local skip_workflow=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --help|-h)
                print_usage
                exit 0
                ;;
            --skip-tests)
                skip_tests=true
                shift
                ;;
            --skip-readme)
                skip_readme=true
                shift
                ;;
            --skip-workflow)
                skip_workflow=true
                shift
                ;;
            --threshold-excellent)
                THRESHOLD_EXCELLENT="$2"
                shift 2
                ;;
            --threshold-good)
                THRESHOLD_GOOD="$2"
                shift 2
                ;;
            --threshold-moderate)
                THRESHOLD_MODERATE="$2"
                shift 2
                ;;
            --threshold-poor)
                THRESHOLD_POOR="$2"
                shift 2
                ;;
            *)
                log_error "Unknown option: $1"
                ;;
        esac
    done
    
    log_info "Starting coverage badge generation for WeatherKMP..."
    log_info "Project root: $PROJECT_ROOT"
    
    # Execute pipeline
    create_directories
    check_dependencies
    
    if [[ "$skip_tests" = false ]]; then
        generate_coverage_reports
    else
        log_info "Skipping test execution, using existing reports."
    fi
    
    generate_all_badges
    generate_coverage_summary
    validate_coverage
    
    if [[ "$skip_readme" = false ]]; then
        update_readme
    fi
    
    if [[ "$skip_workflow" = false ]]; then
        generate_github_workflow
    fi
    
    log_info "Coverage badge generation completed successfully! 🎉"
    log_info "Badges saved to: $BADGES_DIR"
    log_info "Coverage summary: $BADGES_DIR/coverage-summary.json"
}

# Execute main function with all arguments
main "$@"