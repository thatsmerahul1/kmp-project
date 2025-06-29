# ADR-0002: Clean Architecture Adoption

## Status
**Accepted** - 2024-12-29

## Context
We need to establish a clear architectural pattern for the KMP template that ensures separation of concerns, testability, maintainability, and scalability across both Android and iOS platforms.

## Decision
We have decided to implement **Clean Architecture** with **MVVM pattern** for the presentation layer.

### Architecture Layers:
1. **Presentation Layer** (Platform-specific)
   - ViewModels (Shared)
   - Views (Platform-specific: Compose/SwiftUI)
   - UI State Management

2. **Domain Layer** (Shared)
   - Use Cases
   - Repository Interfaces  
   - Domain Models
   - Business Rules

3. **Data Layer** (Shared)
   - Repository Implementations
   - Data Sources (Local/Remote)
   - Data Models (DTOs/Entities)
   - Mappers

## Rationale

### Benefits of Clean Architecture:
1. **Separation of Concerns**: Each layer has distinct responsibilities
2. **Testability**: Easy to unit test business logic independently
3. **Flexibility**: Easy to swap implementations (e.g., different APIs)
4. **Maintainability**: Changes in one layer don't affect others
5. **Scalability**: New features follow established patterns

### MVVM for Presentation:
1. **Reactive UI**: UI reacts to state changes
2. **Shared ViewModels**: Business logic shared between platforms
3. **Platform-specific Views**: Native UI optimizations
4. **State Management**: Clear uni-directional data flow

### Alternatives Considered:

#### MVI (Model-View-Intent)
- **Pros**: Unidirectional data flow, predictable state changes
- **Cons**: More complex for simple UIs, learning curve
- **Verdict**: Rejected - MVVM sufficient for template complexity

#### MVP (Model-View-Presenter)
- **Pros**: Clear separation, testable
- **Cons**: Tight coupling between View and Presenter, not reactive
- **Verdict**: Rejected - Less suitable for reactive UIs

#### Feature-based Architecture
- **Pros**: Modular, scalable for large apps
- **Cons**: Overkill for template, adds complexity
- **Verdict**: Rejected - Too complex for demo template

## Implementation Details

### Dependency Direction:
```
Presentation → Domain ← Data
```

### Shared Components:
- **Domain Layer**: 100% shared
- **Data Layer**: 95% shared (platform-specific drivers)
- **ViewModels**: 100% shared
- **Views**: 0% shared (platform-specific)

### Key Principles:
1. **Dependency Inversion**: High-level modules don't depend on low-level modules
2. **Single Responsibility**: Each class has one reason to change
3. **Open/Closed**: Open for extension, closed for modification
4. **Interface Segregation**: Clients don't depend on unused interfaces

## Consequences

### Positive:
- Clear separation of business logic and UI
- Easy to test each layer independently
- ViewModels can be shared between platforms
- Changes to UI don't affect business logic
- Easy to add new features following established patterns

### Negative:
- More boilerplate code initially
- Learning curve for developers new to Clean Architecture
- Potential over-engineering for very simple features

### Guidelines:
- Use cases for complex business operations
- Repository pattern for data access abstraction
- Shared ViewModels with platform-specific UI
- Dependency injection for loose coupling
- Error handling at appropriate layers

## Testing Strategy
- **Unit Tests**: Domain layer use cases and models
- **Integration Tests**: Repository implementations
- **UI Tests**: Platform-specific view logic
- **ViewModel Tests**: State management and business flows