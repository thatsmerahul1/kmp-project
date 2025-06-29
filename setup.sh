#!/bin/bash

# WeatherKMP Template Setup Script
# This script helps you quickly set up the WeatherKMP template for your project

set -e

echo "🌤️  WeatherKMP Template Setup"
echo "=============================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check if Java is installed
check_java() {
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
        print_success "Java found: $JAVA_VERSION"
    else
        print_error "Java not found. Please install JDK 17 or higher."
        exit 1
    fi
}

# Check if Android SDK is set up
check_android_sdk() {
    if [ -n "$ANDROID_HOME" ]; then
        print_success "Android SDK found at: $ANDROID_HOME"
    else
        print_warning "ANDROID_HOME not set. Please set up Android SDK."
    fi
}

# Check if Xcode is installed (on macOS)
check_xcode() {
    if [[ "$OSTYPE" == "darwin"* ]]; then
        if command -v xcodebuild &> /dev/null; then
            XCODE_VERSION=$(xcodebuild -version | head -n1)
            print_success "Xcode found: $XCODE_VERSION"
        else
            print_warning "Xcode not found. iOS development requires Xcode."
        fi
    fi
}

# Function to set up API key
setup_api_key() {
    echo ""
    print_info "Setting up OpenWeatherMap API key..."
    
    if [ -f "gradle.properties" ]; then
        if grep -q "WEATHER_API_KEY=" gradle.properties; then
            CURRENT_KEY=$(grep "WEATHER_API_KEY=" gradle.properties | cut -d'=' -f2)
            if [ "$CURRENT_KEY" = '""' ] || [ -z "$CURRENT_KEY" ]; then
                print_warning "API key not configured in gradle.properties"
                echo "Please add your OpenWeatherMap API key to gradle.properties:"
                echo "WEATHER_API_KEY=your_api_key_here"
                echo ""
                echo "Get your free API key at: https://openweathermap.org/api"
            else
                print_success "API key already configured"
            fi
        fi
    fi
}

# Function to customize package name
customize_package() {
    echo ""
    print_info "Package customization..."
    echo "Current package: com.weather"
    echo "To customize for your project:"
    echo "1. Find and replace 'com.weather' with your package name"
    echo "2. Update applicationId in androidApp/build.gradle.kts"
    echo "3. Update bundle identifier in iOS project"
    echo ""
}

# Function to build project
build_project() {
    echo ""
    print_info "Building the project..."
    
    # Make gradlew executable
    chmod +x gradlew
    
    # Clean and build
    print_info "Running clean build..."
    if ./gradlew clean build; then
        print_success "Build completed successfully!"
    else
        print_error "Build failed. Please check the errors above."
        exit 1
    fi
}

# Function to run tests
run_tests() {
    echo ""
    print_info "Running tests..."
    
    if ./gradlew :shared:allTests; then
        print_success "All tests passed!"
    else
        print_error "Some tests failed. Please check the output above."
    fi
}

# Main setup process
main() {
    echo ""
    print_info "Starting setup process..."
    
    # Environment checks
    print_info "Checking development environment..."
    check_java
    check_android_sdk
    check_xcode
    
    # API key setup
    setup_api_key
    
    # Package customization info
    customize_package
    
    # Ask user if they want to build
    echo ""
    read -p "Would you like to build the project now? (y/n): " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        build_project
        
        # Ask about running tests
        read -p "Would you like to run tests? (y/n): " -n 1 -r
        echo ""
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            run_tests
        fi
    fi
    
    # Final instructions
    echo ""
    print_success "Setup completed!"
    echo ""
    echo "📱 Next steps:"
    echo "1. Configure your API key in gradle.properties"
    echo "2. Open the project in Android Studio"
    echo "3. For iOS: open iosApp/iosApp.xcodeproj in Xcode"
    echo "4. Customize the package name for your project"
    echo "5. Start building your amazing KMP app! 🚀"
    echo ""
    echo "📚 Documentation:"
    echo "- README.md - Setup and usage guide"
    echo "- TASK_MASTER.md - Project progress tracker"
    echo "- docs/adr/ - Architecture decision records"
    echo ""
    print_success "Happy coding! 🎉"
}

# Run the main function
main