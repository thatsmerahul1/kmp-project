#!/bin/bash

# WeatherKMP 2025 Development Environment Setup Script
# Automates the setup of development environment for cross-platform development

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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
    echo -e "\n${BLUE}🔧 $1${NC}"
}

# Configuration
PROJECT_NAME="WeatherKMP"
MIN_JAVA_VERSION=17
MIN_GRADLE_VERSION="8.5"
MIN_ANDROID_API=24
MIN_XCODE_VERSION="14.0"

# System detection
detect_os() {
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        echo "linux"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        echo "macos"
    elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]]; then
        echo "windows"
    else
        echo "unknown"
    fi
}

OS=$(detect_os)
log_info "Detected OS: $OS"

# Check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Version comparison
version_greater_equal() {
    printf '%s\n%s\n' "$2" "$1" | sort -V -C
}

# Java version check and setup
setup_java() {
    log_step "Setting up Java Development Kit"
    
    if command_exists java; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [[ $JAVA_VERSION -ge $MIN_JAVA_VERSION ]]; then
            log_success "Java $JAVA_VERSION is installed and meets requirements"
            return 0
        else
            log_warning "Java $JAVA_VERSION is installed but version $MIN_JAVA_VERSION+ is required"
        fi
    else
        log_warning "Java is not installed"
    fi
    
    log_info "Installing Java $MIN_JAVA_VERSION..."
    
    case $OS in
        "macos")
            if command_exists brew; then
                brew install --cask zulu$MIN_JAVA_VERSION
            else
                log_error "Homebrew not found. Please install Java $MIN_JAVA_VERSION manually from https://www.azul.com/downloads/"
                return 1
            fi
            ;;
        "linux")
            if command_exists apt-get; then
                sudo apt-get update
                sudo apt-get install -y openjdk-$MIN_JAVA_VERSION-jdk
            elif command_exists yum; then
                sudo yum install -y java-$MIN_JAVA_VERSION-openjdk-devel
            elif command_exists pacman; then
                sudo pacman -S jdk$MIN_JAVA_VERSION-openjdk
            else
                log_error "Package manager not found. Please install Java $MIN_JAVA_VERSION manually"
                return 1
            fi
            ;;
        "windows")
            log_error "Please install Java $MIN_JAVA_VERSION manually from https://www.azul.com/downloads/"
            return 1
            ;;
        *)
            log_error "Unsupported OS. Please install Java $MIN_JAVA_VERSION manually"
            return 1
            ;;
    esac
    
    log_success "Java $MIN_JAVA_VERSION installed successfully"
}

# Android SDK setup
setup_android_sdk() {
    log_step "Setting up Android SDK"
    
    # Check if Android SDK is already installed
    if [[ -n "$ANDROID_HOME" ]] && [[ -d "$ANDROID_HOME" ]]; then
        log_success "Android SDK found at $ANDROID_HOME"
        return 0
    fi
    
    # Check common installation paths
    ANDROID_PATHS=(
        "$HOME/Library/Android/sdk"                    # macOS
        "$HOME/Android/Sdk"                            # Linux
        "/opt/android-sdk"                             # Linux system-wide
        "/usr/local/android-sdk"                       # Linux/macOS system-wide
        "$HOME/AppData/Local/Android/Sdk"              # Windows
    )
    
    for path in "${ANDROID_PATHS[@]}"; do
        if [[ -d "$path" ]]; then
            export ANDROID_HOME="$path"
            log_success "Android SDK found at $path"
            break
        fi
    done
    
    if [[ -z "$ANDROID_HOME" ]]; then
        log_warning "Android SDK not found. Please install Android Studio or SDK manually"
        log_info "Download from: https://developer.android.com/studio"
        
        case $OS in
            "macos")
                if command_exists brew; then
                    log_info "Installing Android Studio via Homebrew..."
                    brew install --cask android-studio
                fi
                ;;
            "linux")
                log_info "Please download and install Android Studio manually"
                ;;
        esac
        
        log_info "After installation, set ANDROID_HOME environment variable"
        return 1
    fi
    
    # Set up environment variables
    export PATH="$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools:$PATH"
    
    # Check if SDK tools are available
    if command_exists sdkmanager; then
        log_info "Installing required Android SDK components..."
        
        # Accept licenses
        yes | sdkmanager --licenses >/dev/null 2>&1
        
        # Install required components
        sdkmanager "platform-tools" "build-tools;34.0.0" "platforms;android-34" \
                   "platforms;android-$MIN_ANDROID_API" "extras;android;m2repository" \
                   "extras;google;m2repository"
        
        log_success "Android SDK components installed"
    else
        log_error "SDK Manager not found. Please check Android SDK installation"
        return 1
    fi
}

# Xcode setup (macOS only)
setup_xcode() {
    if [[ "$OS" != "macos" ]]; then
        log_info "Skipping Xcode setup (not on macOS)"
        return 0
    fi
    
    log_step "Setting up Xcode for iOS development"
    
    if command_exists xcodebuild; then
        XCODE_VERSION=$(xcodebuild -version | head -n 1 | cut -d' ' -f2)
        if version_greater_equal "$XCODE_VERSION" "$MIN_XCODE_VERSION"; then
            log_success "Xcode $XCODE_VERSION is installed and meets requirements"
        else
            log_warning "Xcode $XCODE_VERSION is installed but version $MIN_XCODE_VERSION+ is recommended"
        fi
    else
        log_warning "Xcode not found. Please install from App Store"
        log_info "Download from: https://apps.apple.com/app/xcode/id497799835"
        return 1
    fi
    
    # Install Xcode command line tools
    if ! xcode-select -p >/dev/null 2>&1; then
        log_info "Installing Xcode command line tools..."
        xcode-select --install
    else
        log_success "Xcode command line tools are installed"
    fi
    
    # Accept Xcode license
    if ! xcrun --show-sdk-path >/dev/null 2>&1; then
        log_info "Accepting Xcode license..."
        sudo xcodebuild -license accept
    fi
    
    log_success "Xcode setup completed"
}

# Node.js setup (for web target)
setup_nodejs() {
    log_step "Setting up Node.js for web development"
    
    if command_exists node; then
        NODE_VERSION=$(node --version | cut -d'v' -f2)
        if version_greater_equal "$NODE_VERSION" "18.0.0"; then
            log_success "Node.js $NODE_VERSION is installed and meets requirements"
            return 0
        else
            log_warning "Node.js $NODE_VERSION is installed but version 18+ is recommended"
        fi
    else
        log_warning "Node.js not found"
    fi
    
    log_info "Installing Node.js..."
    
    case $OS in
        "macos")
            if command_exists brew; then
                brew install node
            else
                log_error "Please install Node.js manually from https://nodejs.org/"
                return 1
            fi
            ;;
        "linux")
            if command_exists apt-get; then
                curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
                sudo apt-get install -y nodejs
            elif command_exists yum; then
                curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
                sudo yum install -y nodejs
            else
                log_error "Please install Node.js manually from https://nodejs.org/"
                return 1
            fi
            ;;
        "windows")
            log_error "Please install Node.js manually from https://nodejs.org/"
            return 1
            ;;
    esac
    
    log_success "Node.js installed successfully"
}

# Git setup
setup_git() {
    log_step "Setting up Git"
    
    if command_exists git; then
        log_success "Git is installed"
    else
        log_warning "Git not found. Installing..."
        
        case $OS in
            "macos")
                if command_exists brew; then
                    brew install git
                else
                    log_error "Please install Git manually"
                    return 1
                fi
                ;;
            "linux")
                if command_exists apt-get; then
                    sudo apt-get install -y git
                elif command_exists yum; then
                    sudo yum install -y git
                elif command_exists pacman; then
                    sudo pacman -S git
                fi
                ;;
            "windows")
                log_error "Please install Git from https://git-scm.com/"
                return 1
                ;;
        esac
        
        log_success "Git installed successfully"
    fi
    
    # Configure Git (if not already configured)
    if [[ -z "$(git config --global user.name)" ]]; then
        log_info "Git user configuration not found"
        read -p "Enter your Git username: " git_username
        read -p "Enter your Git email: " git_email
        
        git config --global user.name "$git_username"
        git config --global user.email "$git_email"
        
        log_success "Git configured successfully"
    else
        log_success "Git is already configured"
    fi
}

# IDE setup recommendations
setup_ide() {
    log_step "IDE Setup Recommendations"
    
    log_info "Recommended IDEs for WeatherKMP development:"
    log_info "• Android Studio (recommended for Android/KMP): https://developer.android.com/studio"
    log_info "• IntelliJ IDEA Ultimate (full KMP support): https://www.jetbrains.com/idea/"
    log_info "• Xcode (required for iOS): Available on Mac App Store"
    
    if [[ "$OS" == "macos" ]]; then
        if command_exists brew; then
            read -p "Install Android Studio via Homebrew? (y/n): " install_studio
            if [[ "$install_studio" == "y" ]]; then
                brew install --cask android-studio
                log_success "Android Studio installed"
            fi
        fi
    fi
}

# Docker setup (for monitoring)
setup_docker() {
    log_step "Setting up Docker for monitoring stack"
    
    if command_exists docker; then
        log_success "Docker is installed"
        
        # Check if Docker daemon is running
        if docker info >/dev/null 2>&1; then
            log_success "Docker daemon is running"
        else
            log_warning "Docker daemon is not running. Please start Docker"
        fi
        
        # Install Docker Compose if not available
        if ! command_exists docker-compose; then
            log_info "Installing Docker Compose..."
            case $OS in
                "macos"|"linux")
                    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
                    sudo chmod +x /usr/local/bin/docker-compose
                    ;;
            esac
        fi
        
        log_success "Docker setup completed"
    else
        log_warning "Docker not found"
        log_info "Download Docker Desktop from: https://www.docker.com/products/docker-desktop"
        
        case $OS in
            "macos")
                if command_exists brew; then
                    read -p "Install Docker Desktop via Homebrew? (y/n): " install_docker
                    if [[ "$install_docker" == "y" ]]; then
                        brew install --cask docker
                    fi
                fi
                ;;
        esac
    fi
}

# Project dependencies
setup_project_dependencies() {
    log_step "Setting up project dependencies"
    
    # Check if we're in a WeatherKMP project
    if [[ ! -f "build.gradle.kts" ]] && [[ ! -f "settings.gradle.kts" ]]; then
        log_warning "Not in a WeatherKMP project directory"
        return 0
    fi
    
    log_info "Installing project dependencies..."
    
    # Gradle wrapper permissions
    if [[ -f "gradlew" ]]; then
        chmod +x gradlew
        log_success "Gradle wrapper permissions set"
    fi
    
    # Download dependencies
    if command_exists ./gradlew; then
        ./gradlew dependencies --refresh-dependencies
        log_success "Project dependencies downloaded"
    else
        log_warning "Gradle wrapper not found. Run './gradlew dependencies' after project setup"
    fi
    
    # iOS dependencies (if on macOS)
    if [[ "$OS" == "macos" ]] && [[ -d "iosApp" ]]; then
        if command_exists pod; then
            log_info "Installing iOS dependencies..."
            cd iosApp
            pod install --repo-update
            cd ..
            log_success "iOS dependencies installed"
        else
            log_warning "CocoaPods not found. Install with: sudo gem install cocoapods"
        fi
    fi
}

# Environment file setup
setup_environment() {
    log_step "Setting up environment configuration"
    
    # Create local.properties if it doesn't exist
    if [[ ! -f "local.properties" ]]; then
        log_info "Creating local.properties file..."
        
        cat > local.properties << EOF
# Local configuration for WeatherKMP
# Generated on $(date)

# Android SDK location
sdk.dir=$ANDROID_HOME

# API Keys (add your actual keys here)
# WEATHER_API_KEY=your_openweathermap_api_key

# Feature Flags (development overrides)
ENABLE_SECURITY_FEATURES=true
ENABLE_MONITORING=true
ENABLE_CRASH_ANALYTICS=true
ENABLE_COMPOSE_MULTIPLATFORM=true
ENABLE_FEATURE_TOGGLES=true

# Development settings
DEBUG_LOGGING=true
MOCK_DATA=false
PERFORMANCE_OVERLAY=false

# Monitoring endpoints (development)
PROMETHEUS_URL=http://localhost:9090
GRAFANA_URL=http://localhost:3000

# Build optimization
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
kotlin.incremental=true
EOF
        
        log_success "local.properties created"
        log_warning "Remember to add your API keys to local.properties"
    else
        log_success "local.properties already exists"
    fi
    
    # Setup shell environment
    SHELL_CONFIG=""
    if [[ -f "$HOME/.zshrc" ]]; then
        SHELL_CONFIG="$HOME/.zshrc"
    elif [[ -f "$HOME/.bashrc" ]]; then
        SHELL_CONFIG="$HOME/.bashrc"
    elif [[ -f "$HOME/.bash_profile" ]]; then
        SHELL_CONFIG="$HOME/.bash_profile"
    fi
    
    if [[ -n "$SHELL_CONFIG" ]]; then
        log_info "Setting up shell environment variables..."
        
        # Check if ANDROID_HOME is already in shell config
        if ! grep -q "ANDROID_HOME" "$SHELL_CONFIG"; then
            echo "" >> "$SHELL_CONFIG"
            echo "# WeatherKMP Development Environment" >> "$SHELL_CONFIG"
            echo "export ANDROID_HOME=\"$ANDROID_HOME\"" >> "$SHELL_CONFIG"
            echo "export PATH=\"\$ANDROID_HOME/tools:\$ANDROID_HOME/tools/bin:\$ANDROID_HOME/platform-tools:\$PATH\"" >> "$SHELL_CONFIG"
            
            log_success "Environment variables added to $SHELL_CONFIG"
            log_warning "Please restart your terminal or run: source $SHELL_CONFIG"
        else
            log_success "Environment variables already configured"
        fi
    fi
}

# Verification
verify_setup() {
    log_step "Verifying development environment setup"
    
    local errors=0
    
    # Java
    if command_exists java; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [[ $JAVA_VERSION -ge $MIN_JAVA_VERSION ]]; then
            log_success "Java $JAVA_VERSION ✓"
        else
            log_error "Java version $MIN_JAVA_VERSION+ required"
            ((errors++))
        fi
    else
        log_error "Java not found"
        ((errors++))
    fi
    
    # Android SDK
    if [[ -n "$ANDROID_HOME" ]] && [[ -d "$ANDROID_HOME" ]]; then
        log_success "Android SDK ✓"
    else
        log_error "Android SDK not found"
        ((errors++))
    fi
    
    # Xcode (macOS only)
    if [[ "$OS" == "macos" ]]; then
        if command_exists xcodebuild; then
            log_success "Xcode ✓"
        else
            log_warning "Xcode not found (required for iOS development)"
        fi
    fi
    
    # Node.js
    if command_exists node; then
        NODE_VERSION=$(node --version)
        log_success "Node.js $NODE_VERSION ✓"
    else
        log_warning "Node.js not found (required for web target)"
    fi
    
    # Git
    if command_exists git; then
        log_success "Git ✓"
    else
        log_error "Git not found"
        ((errors++))
    fi
    
    # Docker
    if command_exists docker; then
        log_success "Docker ✓"
    else
        log_warning "Docker not found (required for monitoring stack)"
    fi
    
    echo ""
    if [[ $errors -eq 0 ]]; then
        log_success "🎉 Development environment setup completed successfully!"
        log_info "You can now start developing with WeatherKMP 2025"
        echo ""
        log_info "Next steps:"
        log_info "1. Add your API keys to local.properties"
        log_info "2. Run './gradlew build' to verify setup"
        log_info "3. Open the project in Android Studio or IntelliJ IDEA"
        log_info "4. Start the monitoring stack: 'cd monitoring && docker-compose up -d'"
    else
        log_error "Setup completed with $errors error(s). Please fix the issues above."
        return 1
    fi
}

# Main setup function
main() {
    echo -e "${BLUE}"
    echo "╔══════════════════════════════════════════════════════════════════════════════╗"
    echo "║                    WeatherKMP 2025 Development Setup                        ║"
    echo "║                                                                              ║"
    echo "║  This script will set up your development environment for cross-platform    ║"
    echo "║  Kotlin Multiplatform development with modern tools and practices.          ║"
    echo "╚══════════════════════════════════════════════════════════════════════════════╝"
    echo -e "${NC}\n"
    
    log_info "Starting development environment setup..."
    log_info "Detected operating system: $OS"
    
    # Setup steps
    setup_java
    setup_android_sdk
    setup_xcode
    setup_nodejs
    setup_git
    setup_docker
    setup_ide
    setup_project_dependencies
    setup_environment
    
    # Verification
    verify_setup
}

# Script entry point
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi