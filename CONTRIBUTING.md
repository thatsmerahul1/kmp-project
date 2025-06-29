# Contributing to WeatherKMP

Thank you for your interest in contributing to WeatherKMP! This document provides guidelines for contributing to this Kotlin Multiplatform template project.

## 🎯 Project Vision

WeatherKMP aims to be the **most comprehensive and production-ready KMP template** available, showcasing best practices in:
- Clean Architecture implementation
- Offline-first data strategies  
- Modern UI development (Compose + SwiftUI)
- Comprehensive testing approaches
- Professional development workflows

## 🚀 Getting Started

### Prerequisites

- **JDK 17** or higher
- **Android Studio** Hedgehog (2023.1.1) or newer
- **Xcode 15** (for iOS development)
- **Git** for version control

### Setup

1. **Fork and Clone**
   ```bash
   git clone https://github.com/your-username/weather-kmp-template.git
   cd weather-kmp-template
   ```

2. **Run Setup Script**
   ```bash
   ./setup.sh
   ```

3. **Configure API Key**
   ```properties
   # gradle.properties
   WEATHER_API_KEY=your_openweathermap_api_key
   ```

4. **Build and Test**
   ```bash
   ./gradlew clean build
   ./gradlew :shared:allTests
   ```

## 📝 How to Contribute

### Types of Contributions

We welcome various types of contributions:

- 🐛 **Bug Fixes** - Fix issues in existing functionality
- ✨ **New Features** - Add new capabilities to the template
- 📚 **Documentation** - Improve guides, comments, and examples
- 🧪 **Tests** - Add or improve test coverage
- 🎨 **UI/UX** - Enhance user interface and experience
- ⚡ **Performance** - Optimize app performance
- 🏗️ **Architecture** - Improve code structure and patterns

### Contribution Process

1. **Check Issues**
   - Look for existing issues or feature requests
   - Comment on issues you'd like to work on
   - Create new issues for bugs or feature requests

2. **Create Branch**
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/bug-description
   ```

3. **Make Changes**
   - Follow our coding standards (see below)
   - Add tests for new functionality
   - Update documentation as needed

4. **Test Your Changes**
   ```bash
   ./gradlew :shared:allTests
   ./gradlew :androidApp:testDebugUnitTest
   ./gradlew ktlintCheck
   ```

5. **Commit and Push**
   ```bash
   git add .
   git commit -m "feat: add weather alerts feature"
   git push origin feature/your-feature-name
   ```

6. **Create Pull Request**
   - Use descriptive title and description
   - Reference related issues
   - Include screenshots for UI changes
   - Ensure CI passes

## 🎨 Coding Standards

### Kotlin Style Guide

Follow the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html):

```kotlin
// ✅ Good
class WeatherRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun getWeather(): Result<Weather> {
        return try {
            val data = remoteDataSource.fetchWeather()
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ❌ Bad
class weatherRepository(private val localDataSource:LocalDataSource,private val remoteDataSource:RemoteDataSource){
    suspend fun getWeather():Result<Weather>{
        try{
            val data=remoteDataSource.fetchWeather()
            return Result.success(data)
        }catch(e:Exception){
            return Result.failure(e)
        }
    }
}
```

### Architecture Guidelines

1. **Clean Architecture**: Maintain clear separation between layers
2. **Dependency Inversion**: High-level modules shouldn't depend on low-level ones
3. **Single Responsibility**: Each class should have one reason to change
4. **Interface Segregation**: Don't force clients to depend on unused interfaces

### Testing Standards

- **Unit Tests**: Test business logic in isolation
- **Integration Tests**: Test component interactions
- **Descriptive Names**: Test names should describe what is being tested

```kotlin
@Test
fun `getWeatherForecast should emit cached data first then fresh data when both available`() {
    // Given
    // When  
    // Then
}
```

### Commit Message Format

Use [Conventional Commits](https://www.conventionalcommits.org/) format:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or fixing tests
- `chore`: Build process or auxiliary tool changes

**Examples:**
```bash
feat(weather): add weather alerts functionality
fix(cache): resolve cache invalidation issue
docs(readme): update setup instructions
test(repository): add offline scenario tests
```

## 🧪 Testing Guidelines

### Test Categories

1. **Unit Tests** (`shared/src/commonTest/`)
   - Domain models and business logic
   - Use cases and repository logic
   - Data mappers and transformations

2. **Integration Tests**
   - Database operations
   - API integration
   - Repository implementations

3. **UI Tests**
   - Platform-specific UI logic
   - User interaction flows

### Test Structure

```kotlin
class WeatherRepositoryTest {
    
    @BeforeTest
    fun setup() {
        // Test setup
    }
    
    @Test
    fun `should return cached data when network is unavailable`() {
        // Given
        val cachedWeather = createMockWeather()
        every { localDataSource.getWeather() } returns cachedWeather
        every { networkDataSource.getWeather() } throws NetworkException()
        
        // When
        val result = repository.getWeather()
        
        // Then
        assertEquals(cachedWeather, result.getOrNull())
    }
    
    @AfterTest
    fun teardown() {
        // Cleanup
    }
}
```

## 📱 Platform-Specific Guidelines

### Android (Jetpack Compose)

- Use Material 3 design system
- Follow Compose best practices
- Maintain accessibility standards
- Use proper state management

### iOS (SwiftUI)

- Follow iOS Human Interface Guidelines
- Use proper SwiftUI patterns
- Maintain KMP integration patterns
- Handle iOS-specific requirements

### Shared Code

- Use `expect`/`actual` for platform-specific implementations
- Keep business logic platform-agnostic
- Use proper error handling with Result types
- Maintain clean interfaces

## 📚 Documentation Standards

### Code Documentation

```kotlin
/**
 * Repository for managing weather data with offline-first strategy.
 *
 * This repository implements the offline-first pattern by:
 * 1. Returning cached data immediately if available
 * 2. Fetching fresh data in the background
 * 3. Updating the UI only when fresh data differs
 *
 * @param localDataSource Local cache data source
 * @param remoteDataSource Remote API data source
 */
class WeatherRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
)
```

### README Updates

When adding features, update:
- Feature list in README.md
- Setup instructions if needed
- Usage examples
- Architecture diagrams

## 🔍 Review Process

### Pull Request Requirements

- [ ] All tests pass
- [ ] Code follows style guidelines
- [ ] Documentation is updated
- [ ] No breaking changes (or properly documented)
- [ ] Feature is properly tested
- [ ] UI changes include screenshots

### Review Criteria

Reviewers will check for:

1. **Functionality**: Does it work as expected?
2. **Architecture**: Does it follow clean architecture principles?
3. **Testing**: Is it adequately tested?
4. **Performance**: Does it impact app performance?
5. **Documentation**: Is it properly documented?
6. **Compatibility**: Does it work on both platforms?

## 🚨 Reporting Issues

### Bug Reports

When reporting bugs, include:

- **Device/Platform**: Android/iOS version, device model
- **Steps to Reproduce**: Clear step-by-step instructions
- **Expected Behavior**: What should happen
- **Actual Behavior**: What actually happens
- **Screenshots/Logs**: Visual evidence or error logs
- **Environment**: Development setup details

### Feature Requests

For new features, provide:

- **Use Case**: Why is this feature needed?
- **Proposed Solution**: How should it work?
- **Alternatives**: Other solutions considered
- **Impact**: How does it fit with existing architecture?

## 📄 License

By contributing to WeatherKMP, you agree that your contributions will be licensed under the MIT License.

## 🙏 Recognition

Contributors will be recognized in:
- CONTRIBUTORS.md file
- Release notes
- Project documentation

## 📞 Getting Help

- **GitHub Issues**: For bugs and feature requests
- **Discussions**: For questions and general discussion
- **Documentation**: Check README.md and docs/ folder

Thank you for helping make WeatherKMP better! 🚀