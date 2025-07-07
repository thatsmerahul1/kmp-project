# Coverage Badges Guide for WeatherKMP 2025

This guide explains how to use the comprehensive coverage badge generation system integrated into the WeatherKMP project.

## 🎯 Overview

The coverage badge system automatically generates SVG badges displaying test coverage metrics for:
- **Overall project coverage** (all platforms combined)
- **Platform-specific coverage** (Android, iOS, Common)
- **Coverage types** (Line, Branch, Instruction)

## 🚀 Quick Start

### Generate Coverage Badges

```bash
# Method 1: Using the shell script
./scripts/generate-coverage-badges.sh

# Method 2: Using Gradle integration
./gradlew generateCoverageBadges

# Method 3: Full workflow (test + badges + validation + docs)
./gradlew coverageWorkflow
```

### View Generated Badges

Badges are saved to `.github/badges/` directory:
- `coverage-line.svg` - Overall line coverage
- `coverage-branch.svg` - Overall branch coverage  
- `coverage-instruction.svg` - Overall instruction coverage
- `coverage-android-line.svg` - Android-specific line coverage
- `coverage-ios-branch.svg` - iOS-specific branch coverage
- etc.

## 📊 Badge Types and Metrics

### Coverage Types

1. **Line Coverage** - Percentage of lines executed by tests
2. **Branch Coverage** - Percentage of conditional branches tested
3. **Instruction Coverage** - Percentage of bytecode instructions covered

### Platform Support

- **Android** (`androidTest`) - Android-specific test coverage
- **Common** (`commonTest`) - Cross-platform shared test coverage
- **iOS** (`iosTest`) - iOS-specific test coverage

### Quality Thresholds

| Threshold | Percentage | Badge Color | Description |
|-----------|------------|-------------|-------------|
| Excellent | ≥ 90% | 🟢 Bright Green | Outstanding coverage |
| Good | ≥ 80% | 🟢 Green | Good coverage |
| Moderate | ≥ 70% | 🟡 Yellow | Acceptable coverage |
| Poor | ≥ 60% | 🟠 Orange | Needs improvement |
| Critical | < 60% | 🔴 Red | Insufficient coverage |

## 🛠️ Configuration

### Shell Script Configuration

Edit `scripts/coverage-config.yml` to customize:

```yaml
# Coverage thresholds
thresholds:
  excellent: 90
  good: 80
  moderate: 70
  poor: 60

# Badge styling
styling:
  style: "flat-square"
  logo: "kotlin"
  logoColor: "white"

# Platform configuration
platforms:
  android:
    enabled: true
    display_name: "Android"
  # ... more platforms
```

### Gradle Integration Configuration

Modify `scripts/coverage-badges.gradle.kts`:

```kotlin
val config = CoverageBadgeConfig(
    thresholds = mapOf(
        "excellent" to 90,
        "good" to 80,
        "moderate" to 70,
        "poor" to 60
    ),
    platforms = listOf("android", "common", "ios"),
    coverageTypes = listOf("line", "branch", "instruction")
)
```

## 🔧 Available Commands

### Shell Script Commands

```bash
# Full badge generation
./scripts/generate-coverage-badges.sh

# Skip running tests (use existing reports)
./scripts/generate-coverage-badges.sh --skip-tests

# Skip updating README.md
./scripts/generate-coverage-badges.sh --skip-readme

# Custom thresholds
./scripts/generate-coverage-badges.sh --threshold-good 85

# Show help
./scripts/generate-coverage-badges.sh --help
```

### Gradle Tasks

```bash
# Generate coverage badges only
./gradlew generateCoverageBadges

# Validate coverage against thresholds
./gradlew validateCoverage

# Update README with badges
./gradlew updateReadmeWithBadges

# Complete workflow
./gradlew coverageWorkflow

# Generate coverage reports
./gradlew koverXmlReport
```

## 📝 README Integration

Badges are automatically added to README.md in this format:

```markdown
## 📊 Coverage Badges

### Overall Coverage
![Line Coverage](.github/badges/coverage-line.svg)
![Branch Coverage](.github/badges/coverage-branch.svg)
![Instruction Coverage](.github/badges/coverage-instruction.svg)

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
```

## 🔄 CI/CD Integration

### GitHub Actions

A workflow file is automatically generated at `.github/workflows/coverage-badges.yml`:

```yaml
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
    - name: Run tests and generate coverage
      run: ./gradlew test koverXmlReport
    - name: Generate coverage badges
      run: ./scripts/generate-coverage-badges.sh
    - name: Commit and push badge changes
      run: |
        git config --local user.email "action@github.com"
        git config --local user.name "GitHub Action"
        git add .github/badges/ README.md
        git commit -m "Update coverage badges 🎯" || exit 0
        git push
```

### Manual CI Integration

Add to your CI pipeline:

```bash
# Run tests
./gradlew test

# Generate coverage reports
./gradlew koverXmlReport

# Generate badges
./scripts/generate-coverage-badges.sh

# Commit changes (if needed)
git add .github/badges/ README.md
git commit -m "Update coverage badges"
```

## 📊 Coverage Reports and Analysis

### Generated Files

1. **Badge SVG files** - `.github/badges/coverage-*.svg`
2. **Coverage summary** - `.github/badges/coverage-summary.json`
3. **XML reports** - `shared/build/reports/kover/*.xml`

### Coverage Summary JSON

```json
{
  "timestamp": "2025-01-01T12:00:00Z",
  "project": "WeatherKMP",
  "platforms": {
    "android": {
      "line": 85,
      "branch": 78,
      "instruction": 82
    },
    "common": {
      "line": 92,
      "branch": 89,
      "instruction": 91
    },
    "ios": {
      "line": 87,
      "branch": 81,
      "instruction": 85
    }
  },
  "thresholds": {
    "excellent": 90,
    "good": 80,
    "moderate": 70,
    "poor": 60
  }
}
```

## 🐛 Troubleshooting

### Common Issues

1. **No coverage reports found**
   ```bash
   # Run tests first
   ./gradlew test koverXmlReport
   ```

2. **Permission denied on script**
   ```bash
   chmod +x ./scripts/generate-coverage-badges.sh
   ```

3. **Missing dependencies**
   ```bash
   # Install curl (required)
   brew install curl  # macOS
   apt-get install curl  # Ubuntu
   
   # Install xmllint (optional, improves parsing)
   brew install libxml2  # macOS
   apt-get install libxml2-utils  # Ubuntu
   ```

4. **Gradle script not found**
   ```bash
   # Verify the script path in shared/build.gradle.kts
   apply(from = "../scripts/coverage-badges.gradle.kts")
   ```

### Debugging

Enable verbose output:

```bash
# Shell script debugging
bash -x ./scripts/generate-coverage-badges.sh

# Gradle debugging
./gradlew generateCoverageBadges --debug
```

## 🔧 Advanced Configuration

### Custom Badge Templates

Create custom badge URLs in the script:

```bash
# Custom badge endpoint
local badge_url="https://custom-badge-service.com/badge/$label-$message-$color"

# Custom styling
local badge_url="https://img.shields.io/badge/$label-$message-$color?style=for-the-badge&logo=custom"
```

### Integration with External Services

```bash
# Send coverage data to external service
curl -X POST "https://api.codecov.io/upload" \
  -H "Authorization: Bearer $CODECOV_TOKEN" \
  -F "file=@shared/build/reports/kover/report.xml"
```

### Custom Notification Scripts

```bash
# Slack notification
curl -X POST -H 'Content-type: application/json' \
  --data "{\"text\":\"Coverage updated: $percentage%\"}" \
  $SLACK_WEBHOOK_URL
```

## 📚 Related Documentation

- [Testing Guide](../TESTING_GUIDE.md) - Comprehensive testing documentation
- [Kover Documentation](https://kotlin.github.io/kotlinx-kover/) - Official Kover plugin docs
- [Shields.io](https://shields.io/) - Badge generation service documentation
- [GitHub Actions](https://docs.github.com/en/actions) - CI/CD automation

## 🎯 Best Practices

1. **Run badges generation after every test run**
2. **Set appropriate coverage thresholds for your project**
3. **Include badges in pull request descriptions**
4. **Monitor coverage trends over time**
5. **Use platform-specific badges for detailed insights**
6. **Automate badge updates in CI/CD pipeline**
7. **Keep coverage configuration in version control**

## 🤝 Contributing

To improve the coverage badge system:

1. Fork the repository
2. Create a feature branch
3. Modify scripts in `scripts/` directory
4. Test with your changes
5. Submit a pull request

## 📄 License

This coverage badge system is part of the WeatherKMP project and follows the same license terms.