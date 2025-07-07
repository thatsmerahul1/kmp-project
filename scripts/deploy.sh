#!/bin/bash

# WeatherKMP 2025 Deployment Script
# Automated deployment for multiple environments and platforms

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Logging functions
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

log_step() {
    echo -e "\n${PURPLE}🚀 $1${NC}"
}

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
BUILD_DIR="$PROJECT_ROOT/build"
DIST_DIR="$PROJECT_ROOT/dist"

# Default values
ENVIRONMENT="staging"
PLATFORMS="android"
BUILD_TYPE="release"
SKIP_TESTS=false
SKIP_SECURITY_SCAN=false
DRY_RUN=false
VERBOSE=false

# Version info
VERSION_FILE="$PROJECT_ROOT/version.properties"
DEFAULT_VERSION="2025.1.0"

# Help function
show_help() {
    cat << EOF
WeatherKMP 2025 Deployment Script

Usage: $0 [OPTIONS]

OPTIONS:
    -e, --environment ENV     Target environment (dev, staging, prod) [default: staging]
    -p, --platforms PLATFORMS Comma-separated platforms (android,ios,desktop,web) [default: android]
    -t, --type TYPE          Build type (debug, release) [default: release]
    -v, --version VERSION    Version to deploy [default: auto-detected]
    --skip-tests             Skip running tests
    --skip-security          Skip security scans
    --dry-run                Show what would be deployed without deploying
    --verbose                Enable verbose output
    -h, --help               Show this help message

EXAMPLES:
    $0                                          # Deploy Android to staging
    $0 -e prod -p android,ios                  # Deploy Android and iOS to production
    $0 -e dev -p android --skip-tests          # Deploy Android to dev without tests
    $0 --dry-run -e prod -p android,ios        # Preview production deployment

ENVIRONMENTS:
    dev        Development environment (internal testing)
    staging    Staging environment (pre-production testing)
    prod       Production environment (live deployment)

PLATFORMS:
    android    Android APK/AAB
    ios        iOS IPA (requires macOS)
    desktop    Desktop application JAR
    web        Web application (JS bundle)

EOF
}

# Parse command line arguments
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -e|--environment)
                ENVIRONMENT="$2"
                shift 2
                ;;
            -p|--platforms)
                PLATFORMS="$2"
                shift 2
                ;;
            -t|--type)
                BUILD_TYPE="$2"
                shift 2
                ;;
            -v|--version)
                VERSION="$2"
                shift 2
                ;;
            --skip-tests)
                SKIP_TESTS=true
                shift
                ;;
            --skip-security)
                SKIP_SECURITY_SCAN=true
                shift
                ;;
            --dry-run)
                DRY_RUN=true
                shift
                ;;
            --verbose)
                VERBOSE=true
                shift
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            *)
                log_error "Unknown option: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    # Validate environment
    case $ENVIRONMENT in
        dev|staging|prod)
            ;;
        *)
            log_error "Invalid environment: $ENVIRONMENT. Must be dev, staging, or prod"
            exit 1
            ;;
    esac
    
    # Validate build type
    case $BUILD_TYPE in
        debug|release)
            ;;
        *)
            log_error "Invalid build type: $BUILD_TYPE. Must be debug or release"
            exit 1
            ;;
    esac
}

# Get version
get_version() {
    if [[ -n "$VERSION" ]]; then
        echo "$VERSION"
        return
    fi
    
    if [[ -f "$VERSION_FILE" ]]; then
        grep "version=" "$VERSION_FILE" | cut -d'=' -f2
    elif command -v git >/dev/null 2>&1 && git rev-parse --git-dir >/dev/null 2>&1; then
        # Use git tag or commit hash
        if git describe --tags --exact-match >/dev/null 2>&1; then
            git describe --tags --exact-match
        else
            echo "$DEFAULT_VERSION-$(git rev-parse --short HEAD)"
        fi
    else
        echo "$DEFAULT_VERSION"
    fi
}

# Setup environment
setup_environment() {
    log_step "Setting up deployment environment"
    
    # Create build and dist directories
    mkdir -p "$BUILD_DIR" "$DIST_DIR"
    
    # Set environment variables based on target
    case $ENVIRONMENT in
        dev)
            export BUILD_VARIANT="debug"
            export API_BASE_URL="https://api-dev.weatherkmp.com"
            export MONITORING_ENABLED="true"
            export SECURITY_FEATURES="false"
            ;;
        staging)
            export BUILD_VARIANT="release"
            export API_BASE_URL="https://api-staging.weatherkmp.com"
            export MONITORING_ENABLED="true"
            export SECURITY_FEATURES="true"
            ;;
        prod)
            export BUILD_VARIANT="release"
            export API_BASE_URL="https://api.weatherkmp.com"
            export MONITORING_ENABLED="true"
            export SECURITY_FEATURES="true"
            ;;
    esac
    
    VERSION=$(get_version)
    log_info "Environment: $ENVIRONMENT"
    log_info "Version: $VERSION"
    log_info "Build type: $BUILD_TYPE"
    log_info "Platforms: $PLATFORMS"
    
    if [[ "$DRY_RUN" == "true" ]]; then
        log_warning "DRY RUN MODE - No actual deployment will occur"
    fi
}

# Pre-deployment checks
pre_deployment_checks() {
    log_step "Running pre-deployment checks"
    
    # Check if project directory is valid
    if [[ ! -f "$PROJECT_ROOT/build.gradle.kts" ]]; then
        log_error "Not a valid WeatherKMP project directory"
        exit 1
    fi
    
    # Check Git status for production
    if [[ "$ENVIRONMENT" == "prod" ]] && command -v git >/dev/null 2>&1; then
        if ! git diff-index --quiet HEAD --; then
            log_error "Working directory is not clean. Commit changes before production deployment"
            exit 1
        fi
        
        CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
        if [[ "$CURRENT_BRANCH" != "main" ]] && [[ "$CURRENT_BRANCH" != "master" ]]; then
            log_warning "Deploying from branch '$CURRENT_BRANCH' to production"
            read -p "Continue? (y/N): " confirm
            if [[ "$confirm" != "y" ]] && [[ "$confirm" != "Y" ]]; then
                log_info "Deployment cancelled"
                exit 0
            fi
        fi
    fi
    
    # Check required tools
    if [[ ! -f "$PROJECT_ROOT/gradlew" ]]; then
        log_error "Gradle wrapper not found"
        exit 1
    fi
    
    # Platform-specific checks
    IFS=',' read -ra PLATFORM_ARRAY <<< "$PLATFORMS"
    for platform in "${PLATFORM_ARRAY[@]}"; do
        case $platform in
            ios)
                if [[ "$(uname)" != "Darwin" ]]; then
                    log_error "iOS deployment requires macOS"
                    exit 1
                fi
                if ! command -v xcodebuild >/dev/null 2>&1; then
                    log_error "Xcode not found (required for iOS deployment)"
                    exit 1
                fi
                ;;
            web)
                if ! command -v node >/dev/null 2>&1; then
                    log_error "Node.js not found (required for web deployment)"
                    exit 1
                fi
                ;;
        esac
    done
    
    log_success "Pre-deployment checks passed"
}

# Run tests
run_tests() {
    if [[ "$SKIP_TESTS" == "true" ]]; then
        log_warning "Skipping tests (--skip-tests flag)"
        return 0
    fi
    
    log_step "Running tests"
    
    cd "$PROJECT_ROOT"
    
    # Unit tests
    log_info "Running unit tests..."
    if [[ "$VERBOSE" == "true" ]]; then
        ./gradlew test
    else
        ./gradlew test --console=plain | grep -E "(BUILD|FAILED|ERROR|SUCCESSFUL)"
    fi
    
    # Platform-specific tests
    IFS=',' read -ra PLATFORM_ARRAY <<< "$PLATFORMS"
    for platform in "${PLATFORM_ARRAY[@]}"; do
        case $platform in
            android)
                log_info "Running Android tests..."
                if [[ "$VERBOSE" == "true" ]]; then
                    ./gradlew :shared:testDebugUnitTest
                else
                    ./gradlew :shared:testDebugUnitTest --console=plain | grep -E "(BUILD|FAILED|ERROR|SUCCESSFUL)"
                fi
                ;;
            ios)
                log_info "Running iOS tests..."
                if [[ "$VERBOSE" == "true" ]]; then
                    ./gradlew :shared:iosTest
                else
                    ./gradlew :shared:iosTest --console=plain | grep -E "(BUILD|FAILED|ERROR|SUCCESSFUL)"
                fi
                ;;
        esac
    done
    
    log_success "All tests passed"
}

# Security scan
run_security_scan() {
    if [[ "$SKIP_SECURITY_SCAN" == "true" ]]; then
        log_warning "Skipping security scan (--skip-security flag)"
        return 0
    fi
    
    if [[ "$ENVIRONMENT" != "prod" ]]; then
        log_info "Skipping security scan (not production environment)"
        return 0
    fi
    
    log_step "Running security scans"
    
    cd "$PROJECT_ROOT"
    
    # OWASP dependency check
    if ./gradlew tasks --all | grep -q "dependencyCheckAnalyze"; then
        log_info "Running OWASP dependency check..."
        ./gradlew dependencyCheckAnalyze
    fi
    
    # Custom security scan
    if ./gradlew tasks --all | grep -q "securityScan"; then
        log_info "Running custom security scan..."
        ./gradlew securityScan
    fi
    
    log_success "Security scans completed"
}

# Build applications
build_applications() {
    log_step "Building applications"
    
    cd "$PROJECT_ROOT"
    
    IFS=',' read -ra PLATFORM_ARRAY <<< "$PLATFORMS"
    for platform in "${PLATFORM_ARRAY[@]}"; do
        case $platform in
            android)
                build_android
                ;;
            ios)
                build_ios
                ;;
            desktop)
                build_desktop
                ;;
            web)
                build_web
                ;;
            *)
                log_error "Unknown platform: $platform"
                exit 1
                ;;
        esac
    done
    
    log_success "All builds completed"
}

# Build Android
build_android() {
    log_info "Building Android application..."
    
    if [[ "$BUILD_TYPE" == "release" ]]; then
        if [[ "$ENVIRONMENT" == "prod" ]]; then
            # Build AAB for Play Store
            ./gradlew :androidApp:bundleRelease
            
            # Also build APK for direct distribution
            ./gradlew :androidApp:assembleRelease
            
            # Copy outputs
            cp androidApp/build/outputs/bundle/release/androidApp-release.aab "$DIST_DIR/weatherkmp-${VERSION}-release.aab"
            cp androidApp/build/outputs/apk/release/androidApp-release.apk "$DIST_DIR/weatherkmp-${VERSION}-release.apk"
        else
            ./gradlew :androidApp:assembleRelease
            cp androidApp/build/outputs/apk/release/androidApp-release.apk "$DIST_DIR/weatherkmp-${VERSION}-${ENVIRONMENT}.apk"
        fi
    else
        ./gradlew :androidApp:assembleDebug
        cp androidApp/build/outputs/apk/debug/androidApp-debug.apk "$DIST_DIR/weatherkmp-${VERSION}-debug.apk"
    fi
    
    log_success "Android build completed"
}

# Build iOS
build_ios() {
    log_info "Building iOS application..."
    
    cd "$PROJECT_ROOT"
    
    # Build framework first
    if [[ "$BUILD_TYPE" == "release" ]]; then
        ./gradlew :shared:linkPodReleaseFrameworkIosArm64
        ./gradlew :shared:linkPodReleaseFrameworkIosX64  # For simulator
    else
        ./gradlew :shared:linkPodDebugFrameworkIosArm64
        ./gradlew :shared:linkPodDebugFrameworkIosX64
    fi
    
    # Build iOS app
    cd iosApp
    
    if [[ "$BUILD_TYPE" == "release" ]]; then
        # Archive for distribution
        xcodebuild -workspace iosApp.xcworkspace \
                   -scheme iosApp \
                   -configuration Release \
                   -destination generic/platform=iOS \
                   -archivePath "$BUILD_DIR/iosApp.xcarchive" \
                   archive
        
        # Export IPA
        xcodebuild -exportArchive \
                   -archivePath "$BUILD_DIR/iosApp.xcarchive" \
                   -exportOptionsPlist exportOptions.plist \
                   -exportPath "$BUILD_DIR/ios-export"
        
        # Copy IPA
        cp "$BUILD_DIR/ios-export/iosApp.ipa" "$DIST_DIR/weatherkmp-${VERSION}-${ENVIRONMENT}.ipa"
    else
        # Build for simulator
        xcodebuild -workspace iosApp.xcworkspace \
                   -scheme iosApp \
                   -configuration Debug \
                   -destination 'platform=iOS Simulator,name=iPhone 14' \
                   build
    fi
    
    cd "$PROJECT_ROOT"
    log_success "iOS build completed"
}

# Build Desktop
build_desktop() {
    log_info "Building desktop application..."
    
    if [[ "$BUILD_TYPE" == "release" ]]; then
        ./gradlew :desktopApp:packageDistributionForCurrentOS
        
        # Find and copy the distribution
        DESKTOP_DIST=$(find desktopApp/build/compose/binaries -name "*.dmg" -o -name "*.msi" -o -name "*.deb" -o -name "*.rpm" | head -1)
        if [[ -n "$DESKTOP_DIST" ]]; then
            EXTENSION="${DESKTOP_DIST##*.}"
            cp "$DESKTOP_DIST" "$DIST_DIR/weatherkmp-${VERSION}-${ENVIRONMENT}.${EXTENSION}"
        fi
    else
        ./gradlew :desktopApp:jar
        cp desktopApp/build/libs/desktopApp.jar "$DIST_DIR/weatherkmp-${VERSION}-desktop-debug.jar"
    fi
    
    log_success "Desktop build completed"
}

# Build Web
build_web() {
    log_info "Building web application..."
    
    if [[ "$BUILD_TYPE" == "release" ]]; then
        ./gradlew :webApp:jsBrowserDistribution
        
        # Create web distribution archive
        cd webApp/build/dist/js/productionExecutable
        tar -czf "$DIST_DIR/weatherkmp-${VERSION}-web.tar.gz" .
        cd "$PROJECT_ROOT"
    else
        ./gradlew :webApp:jsBrowserDevelopmentWebpack
    fi
    
    log_success "Web build completed"
}

# Deploy to environment
deploy_to_environment() {
    if [[ "$DRY_RUN" == "true" ]]; then
        log_step "DRY RUN: Would deploy to $ENVIRONMENT"
        show_deployment_summary
        return 0
    fi
    
    log_step "Deploying to $ENVIRONMENT environment"
    
    case $ENVIRONMENT in
        dev)
            deploy_to_dev
            ;;
        staging)
            deploy_to_staging
            ;;
        prod)
            deploy_to_production
            ;;
    esac
    
    log_success "Deployment to $ENVIRONMENT completed"
}

# Deploy to development
deploy_to_dev() {
    log_info "Deploying to development environment..."
    
    # Development deployment typically involves:
    # - Copying files to development server
    # - Installing on test devices
    # - Updating internal app distribution
    
    IFS=',' read -ra PLATFORM_ARRAY <<< "$PLATFORMS"
    for platform in "${PLATFORM_ARRAY[@]}"; do
        case $platform in
            android)
                log_info "Installing Android APK on connected devices..."
                if adb devices | grep -q "device$"; then
                    adb install -r "$DIST_DIR"/weatherkmp-*-debug.apk 2>/dev/null || true
                fi
                ;;
            web)
                log_info "Deploying web app to development server..."
                # rsync or scp to development server
                # Example: rsync -avz "$DIST_DIR"/weatherkmp-*-web.tar.gz dev-server:/var/www/
                ;;
        esac
    done
}

# Deploy to staging
deploy_to_staging() {
    log_info "Deploying to staging environment..."
    
    # Staging deployment typically involves:
    # - Uploading to internal app distribution (TestFlight, Firebase App Distribution)
    # - Deploying web app to staging server
    # - Notifying QA team
    
    IFS=',' read -ra PLATFORM_ARRAY <<< "$PLATFORMS"
    for platform in "${PLATFORM_ARRAY[@]}"; do
        case $platform in
            android)
                log_info "Uploading Android APK to Firebase App Distribution..."
                # firebase appdistribution:distribute "$DIST_DIR"/weatherkmp-*-staging.apk \
                #   --app YOUR_ANDROID_APP_ID \
                #   --release-notes "Version $VERSION for staging testing"
                ;;
            ios)
                log_info "Uploading iOS IPA to TestFlight..."
                # xcrun altool --upload-app -f "$DIST_DIR"/weatherkmp-*-staging.ipa \
                #   -u YOUR_APPLE_ID -p YOUR_APP_SPECIFIC_PASSWORD
                ;;
            web)
                log_info "Deploying web app to staging server..."
                # Deploy to staging web server
                ;;
        esac
    done
}

# Deploy to production
deploy_to_production() {
    log_info "Deploying to production environment..."
    
    # Production deployment requires confirmation
    echo -e "${YELLOW}⚠️  You are about to deploy to PRODUCTION${NC}"
    echo "Version: $VERSION"
    echo "Platforms: $PLATFORMS"
    echo ""
    read -p "Are you sure you want to continue? (yes/no): " confirm
    
    if [[ "$confirm" != "yes" ]]; then
        log_info "Production deployment cancelled"
        exit 0
    fi
    
    # Production deployment typically involves:
    # - Uploading to app stores (Google Play, App Store)
    # - Deploying web app to production servers
    # - Creating release notes and documentation
    # - Notifying stakeholders
    
    IFS=',' read -ra PLATFORM_ARRAY <<< "$PLATFORMS"
    for platform in "${PLATFORM_ARRAY[@]}"; do
        case $platform in
            android)
                log_info "Uploading Android AAB to Google Play Console..."
                # bundletool or Google Play Console API
                log_warning "Manual upload to Google Play Console required"
                log_info "File: $DIST_DIR/weatherkmp-${VERSION}-release.aab"
                ;;
            ios)
                log_info "Uploading iOS IPA to App Store Connect..."
                # xcrun altool --upload-app -f "$DIST_DIR"/weatherkmp-*-prod.ipa \
                #   -u YOUR_APPLE_ID -p YOUR_APP_SPECIFIC_PASSWORD
                log_warning "Manual submission in App Store Connect required"
                ;;
            web)
                log_info "Deploying web app to production servers..."
                # Deploy to production web infrastructure
                ;;
            desktop)
                log_info "Publishing desktop application..."
                log_warning "Manual distribution or update server deployment required"
                ;;
        esac
    done
    
    # Create release tag
    if command -v git >/dev/null 2>&1; then
        log_info "Creating release tag..."
        git tag -a "v$VERSION" -m "Release version $VERSION"
        # git push origin "v$VERSION"
        log_success "Release tag v$VERSION created"
    fi
}

# Post-deployment tasks
post_deployment_tasks() {
    log_step "Running post-deployment tasks"
    
    # Update monitoring dashboards
    if [[ -f "$PROJECT_ROOT/monitoring/scripts/update-dashboards.sh" ]]; then
        log_info "Updating monitoring dashboards..."
        bash "$PROJECT_ROOT/monitoring/scripts/update-dashboards.sh" "$ENVIRONMENT" "$VERSION"
    fi
    
    # Send notifications
    send_deployment_notifications
    
    # Create deployment report
    create_deployment_report
    
    log_success "Post-deployment tasks completed"
}

# Send deployment notifications
send_deployment_notifications() {
    log_info "Sending deployment notifications..."
    
    # Slack notification (if webhook configured)
    if [[ -n "$SLACK_WEBHOOK_URL" ]]; then
        curl -X POST -H 'Content-type: application/json' \
             --data "{\"text\":\"🚀 WeatherKMP $VERSION deployed to $ENVIRONMENT\nPlatforms: $PLATFORMS\"}" \
             "$SLACK_WEBHOOK_URL" 2>/dev/null || true
    fi
    
    # Email notification (if configured)
    if [[ -n "$NOTIFICATION_EMAIL" ]] && command -v mail >/dev/null 2>&1; then
        echo "WeatherKMP $VERSION has been deployed to $ENVIRONMENT environment." | \
        mail -s "Deployment Notification: WeatherKMP $VERSION" "$NOTIFICATION_EMAIL" || true
    fi
    
    # Discord notification (if webhook configured)
    if [[ -n "$DISCORD_WEBHOOK_URL" ]]; then
        curl -X POST -H 'Content-type: application/json' \
             --data "{\"content\":\"🚀 **WeatherKMP $VERSION** deployed to **$ENVIRONMENT**\\nPlatforms: $PLATFORMS\"}" \
             "$DISCORD_WEBHOOK_URL" 2>/dev/null || true
    fi
}

# Create deployment report
create_deployment_report() {
    log_info "Creating deployment report..."
    
    REPORT_FILE="$DIST_DIR/deployment-report-${VERSION}-${ENVIRONMENT}.md"
    
    cat > "$REPORT_FILE" << EOF
# WeatherKMP Deployment Report

## Deployment Information
- **Version**: $VERSION
- **Environment**: $ENVIRONMENT
- **Build Type**: $BUILD_TYPE
- **Platforms**: $PLATFORMS
- **Date**: $(date)
- **Deployed By**: $(whoami)

## Build Artifacts
$(ls -la "$DIST_DIR"/weatherkmp-${VERSION}* 2>/dev/null || echo "No artifacts found")

## Platform Status
EOF

    IFS=',' read -ra PLATFORM_ARRAY <<< "$PLATFORMS"
    for platform in "${PLATFORM_ARRAY[@]}"; do
        echo "- **$platform**: ✅ Deployed" >> "$REPORT_FILE"
    done
    
    cat >> "$REPORT_FILE" << EOF

## Environment Configuration
- **API Base URL**: $API_BASE_URL
- **Monitoring Enabled**: $MONITORING_ENABLED
- **Security Features**: $SECURITY_FEATURES

## Next Steps
- Monitor application performance and error rates
- Verify all features are working as expected
- Update documentation if necessary

---
*Generated by WeatherKMP deployment script*
EOF
    
    log_success "Deployment report created: $REPORT_FILE"
}

# Show deployment summary
show_deployment_summary() {
    echo ""
    echo "┌─────────────────────────────────────────────────────────────────────────────┐"
    echo "│                           DEPLOYMENT SUMMARY                                │"
    echo "├─────────────────────────────────────────────────────────────────────────────┤"
    echo "│ Version:     $VERSION"
    echo "│ Environment: $ENVIRONMENT"
    echo "│ Platforms:   $PLATFORMS"
    echo "│ Build Type:  $BUILD_TYPE"
    echo "│ Artifacts:   $(ls -1 "$DIST_DIR"/weatherkmp-${VERSION}* 2>/dev/null | wc -l) files"
    echo "└─────────────────────────────────────────────────────────────────────────────┘"
    echo ""
    
    log_info "Deployment artifacts:"
    ls -la "$DIST_DIR"/weatherkmp-${VERSION}* 2>/dev/null || log_warning "No artifacts found"
}

# Cleanup
cleanup() {
    log_step "Cleaning up temporary files"
    
    # Remove temporary build files
    rm -rf "$BUILD_DIR/tmp" 2>/dev/null || true
    
    # Keep distribution files but clean old ones (older than 30 days)
    find "$DIST_DIR" -name "weatherkmp-*" -type f -mtime +30 -delete 2>/dev/null || true
    
    log_success "Cleanup completed"
}

# Main deployment function
main() {
    echo -e "${PURPLE}"
    echo "╔══════════════════════════════════════════════════════════════════════════════╗"
    echo "║                         WeatherKMP 2025 Deployment                          ║"
    echo "║                                                                              ║"
    echo "║  Automated deployment script for cross-platform Kotlin Multiplatform       ║"
    echo "║  applications with comprehensive CI/CD pipeline integration.                ║"
    echo "╚══════════════════════════════════════════════════════════════════════════════╝"
    echo -e "${NC}\n"
    
    # Parse arguments
    parse_args "$@"
    
    # Setup
    setup_environment
    
    # Pre-deployment checks
    pre_deployment_checks
    
    # Run tests
    run_tests
    
    # Security scan
    run_security_scan
    
    # Build applications
    build_applications
    
    # Deploy
    deploy_to_environment
    
    # Post-deployment
    post_deployment_tasks
    
    # Show summary
    show_deployment_summary
    
    # Cleanup
    cleanup
    
    log_success "🎉 Deployment completed successfully!"
}

# Error handling
trap 'log_error "Deployment failed at line $LINENO. Exit code: $?"' ERR

# Script entry point
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi