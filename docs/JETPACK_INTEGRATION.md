# 🚀 Jetpack Integration Patterns - 2025 Standards

## 📋 Overview

This document outlines the integration patterns for modern Android Jetpack libraries within our KMP architecture, following 2025 standards and best practices.

## 🏗️ Architecture Principles

### 1. Platform Abstraction
- **Shared Logic**: Business logic remains in commonMain
- **Platform Bridges**: Jetpack-specific implementations in androidMain
- **Interface Segregation**: Clear contracts between shared and platform code

### 2. Lifecycle Management
- **SavedStateHandle**: Process death resilience
- **ViewModel Scoping**: Proper lifecycle awareness
- **Flow Collection**: Lifecycle-aware reactive programming

### 3. Modern Jetpack Integration
- **Compose Integration**: Ready for Jetpack Compose
- **Navigation**: Type-safe navigation with Navigation Component
- **Room**: Database migration strategies
- **Work Manager**: Background task scheduling

---

## 🔧 Implementation Patterns

### 1. ViewModel Bridge Pattern

The `AndroidViewModelBridge` provides seamless integration between shared ViewModels and Jetpack Lifecycle.

```kotlin
// Shared ViewModel (in commonMain)
abstract class BaseViewModel<UiState : BaseUiState, UiEvent : BaseUiEvent> {
    protected val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    abstract fun onEvent(event: UiEvent)
    abstract fun createInitialState(): UiState
}

// Android Bridge (in androidMain)
abstract class AndroidViewModelBridge<UiState : BaseUiState, UiEvent : BaseUiEvent>(
    private val savedStateHandle: SavedStateHandle? = null
) : ViewModel() {
    
    protected abstract val sharedViewModel: BaseViewModel<UiState, UiEvent>
    val uiState: StateFlow<UiState> = sharedViewModel.uiState
    
    fun onEvent(event: UiEvent) = sharedViewModel.onEvent(event)
}
```

### 2. SavedStateHandle Integration

```kotlin
// Process death resilient state management
class WeatherViewModelImpl(
    savedStateHandle: SavedStateHandle
) : AndroidViewModelBridge<WeatherUiState, WeatherUiEvent>(savedStateHandle) {
    
    override val sharedViewModel = WeatherViewModel(
        weatherRepository = get(),
        logger = get()
    )
    
    override fun onStateRestored(savedStateHandle: SavedStateHandle) {
        // Restore critical state after process death
        val lastSearchQuery = getFromState<String>("last_search_query")
        val selectedLocationId = getFromState<String>("selected_location_id")
        
        lastSearchQuery?.let { query ->
            sharedViewModel.onEvent(WeatherUiEvent.RestoreSearchQuery(query))
        }
        
        selectedLocationId?.let { locationId ->
            sharedViewModel.onEvent(WeatherUiEvent.RestoreSelectedLocation(locationId))
        }
    }
    
    override fun onStateSaved(state: WeatherUiState) {
        // Save critical state for process death scenarios
        saveToState("last_search_query", state.searchQuery)
        saveToState("selected_location_id", state.selectedLocation?.id)
    }
}
```

### 3. Navigation Integration

```kotlin
// Shared Navigation Manager (in commonMain)
interface NavigationManager {
    val navigationCommands: Flow<NavigationCommand>
    fun navigate(command: NavigationCommand)
}

// Android Navigation Implementation (in androidMain)
class AndroidNavigationManager(
    private val navController: NavController
) : NavigationManager {
    
    private val _navigationCommands = MutableSharedFlow<NavigationCommand>()
    override val navigationCommands = _navigationCommands.asSharedFlow()
    
    init {
        navigationCommands.onEach { command ->
            when (command) {
                is NavigationCommand.NavigateTo -> {
                    navController.navigate(command.route) {
                        if (command.options.clearBackStack) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    }
                }
                is NavigationCommand.NavigateBack -> {
                    navController.popBackStack()
                }
                // Handle other navigation commands...
            }
        }.launchIn(lifecycleScope)
    }
}
```

### 4. Room Database Integration

```kotlin
// Shared Migration Logic (in commonMain)
abstract class DatabaseMigration(val fromVersion: Int, val toVersion: Int) {
    abstract suspend fun migrate(database: DatabaseContext): Result<Unit>
    abstract val description: String
}

// Room Integration (in androidMain)
class SharedMigrationWrapper(
    private val sharedMigration: DatabaseMigration,
    private val logger: Logger
) : Migration(sharedMigration.fromVersion, sharedMigration.toVersion) {
    
    override fun migrate(database: SupportSQLiteDatabase) {
        runBlocking {
            val context = RoomDatabaseContext(database, logger)
            sharedMigration.migrate(context).getOrThrow()
        }
    }
}

@Database(
    entities = [WeatherEntity::class, ForecastEntity::class],
    version = DatabaseSchema.CURRENT_VERSION,
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun forecastDao(): ForecastDao
    
    companion object {
        fun create(context: Context, logger: Logger): WeatherDatabase {
            return Room.databaseBuilder(
                context,
                WeatherDatabase::class.java,
                RoomDatabaseConfig.DATABASE_NAME
            )
            .addMigrations(*RoomMigrationFactory.createAllMigrations(logger))
            .addCallback(object : WeatherRoomCallback(logger) {
                override fun onDatabaseCreated(db: SupportSQLiteDatabase) {
                    // Initialize default data
                }
                override fun onDatabaseOpened(db: SupportSQLiteDatabase) {
                    // Perform any opening tasks
                }
            })
            .build()
        }
    }
}
```

---

## 🎯 Best Practices

### 1. State Management
- **Single Source of Truth**: Shared ViewModel holds state
- **Reactive Updates**: Use StateFlow for state emission
- **Process Death Resilience**: Save critical state in SavedStateHandle
- **Error Handling**: Unified error handling through Result wrapper

### 2. Lifecycle Awareness
- **Automatic Cleanup**: ViewModel clears shared resources
- **Flow Collection**: Use lifecycle-aware collectors
- **Memory Management**: Prevent memory leaks with proper scoping

### 3. Navigation
- **Type Safety**: Use sealed classes for navigation commands
- **State Restoration**: Handle deep links and state restoration
- **Animation Support**: Abstract animation types for platform flexibility

### 4. Database
- **Migration Strategy**: Shared migration logic with platform bridges
- **Version Management**: Centralized schema version control
- **Error Recovery**: Graceful handling of migration failures

---

## 📊 Integration Checklist

### ✅ ViewModel Integration
- [x] AndroidViewModelBridge implemented
- [x] SavedStateHandle support added
- [x] Lifecycle-aware state management
- [x] Automatic cleanup on ViewModel clear
- [x] Error handling integration
- [x] Retry and refresh mechanisms

### ✅ Navigation Integration
- [x] NavigationManager abstraction
- [x] Type-safe navigation commands
- [x] Deep link support preparation
- [x] Animation type abstraction
- [x] Back stack management
- [x] Modal and dialog support

### ✅ Database Integration
- [x] DatabaseMigration abstraction
- [x] Room migration bridge
- [x] Schema version management
- [x] Migration validation
- [x] Error handling for migrations
- [x] Transaction support

### ⏳ Pending Integrations
- [ ] Work Manager abstraction
- [ ] DataStore integration
- [ ] Notification abstraction
- [ ] Biometric authentication bridge
- [ ] Camera and media abstractions

---

## 🔄 Migration Strategies

### 1. Gradual Adoption
1. **Start with Core**: Implement base abstractions first
2. **Feature by Feature**: Migrate individual features
3. **Test Coverage**: Maintain test coverage during migration
4. **Rollback Plan**: Ability to rollback changes if needed

### 2. Compose Migration Preparation
```kotlin
// UI Abstraction for Compose Migration
interface UIComponent<T> {
    fun render(state: T)
}

// Platform-specific implementations
// Android (current): Fragment/View implementation
// Future: Compose implementation
class ComposeWeatherComponent : UIComponent<WeatherUiState> {
    @Composable
    override fun render(state: WeatherUiState) {
        // Compose UI implementation
    }
}
```

### 3. Testing Strategy
```kotlin
// Shared tests for business logic
class WeatherViewModelTest {
    @Test
    fun `when weather requested, should emit loading then success`() = runTest {
        // Test shared ViewModel logic
    }
}

// Android-specific tests for Jetpack integration
class AndroidWeatherViewModelTest {
    @Test
    fun `should save and restore state correctly`() {
        // Test SavedStateHandle integration
    }
}
```

---

## 📈 Performance Considerations

### 1. Memory Management
- **ViewModel Scoping**: Proper lifecycle scoping prevents leaks
- **Flow Cancellation**: Automatic cancellation when ViewModel clears
- **Resource Cleanup**: Explicit cleanup in onCleared()

### 2. State Restoration
- **Selective Saving**: Save only critical state data
- **Serialization**: Use efficient serialization for SavedStateHandle
- **Lazy Loading**: Restore state only when needed

### 3. Navigation Performance
- **Command Batching**: Batch navigation commands when possible
- **State Preservation**: Minimize state loss during navigation
- **Memory Optimization**: Clear unnecessary back stack entries

---

## 🚀 Future Enhancements

### 1. Compose Multiplatform Ready
- **UI Abstraction**: Ready for Compose migration
- **Theme System**: Shared theme tokens
- **Animation System**: Platform-agnostic animations

### 2. Advanced Jetpack Features
- **Paging 3**: Infinite scroll pagination
- **Work Manager**: Background task scheduling
- **DataStore**: Preferences and data storage
- **CameraX**: Camera functionality abstraction

### 3. Modern Architecture Patterns
- **MVI Pattern**: Consider MVI for complex state management
- **Unidirectional Data Flow**: Enforce strict data flow patterns
- **Event Sourcing**: Consider for complex business operations

---

## 📚 Resources

### Documentation
- [Android Jetpack Guide](https://developer.android.com/jetpack)
- [KMP Best Practices](https://kotlinlang.org/docs/multiplatform-mobile-best-practices.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)

### Code Examples
- `AndroidViewModelBridge.kt` - ViewModel integration
- `NavigationManager.kt` - Navigation abstraction
- `DatabaseMigration.kt` - Database migration patterns
- `RoomMigrationBridge.kt` - Room integration

---

*Last Updated: January 2025*  
*Version: 1.0.0*  
*Maintainer: Development Team*