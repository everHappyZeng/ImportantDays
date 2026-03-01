# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ImportantDays is an Android application built with Kotlin and Jetpack Compose for tracking important dates and events. The app allows users to create, manage, and receive reminders for significant days like birthdays, anniversaries, and other memorable occasions.

## Build Commands

### Build the project
```bash
./gradlew build
```

### Run tests
```bash
# Unit tests
./gradlew test

# Instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest
```

### Clean build
```bash
./gradlew clean
```

### Install debug APK
```bash
./gradlew installDebug
```

## Architecture

This project follows **Clean Architecture** principles with clear separation of concerns:

### Layer Structure

**Data Layer** (`app/src/main/java/com/example/importantdays/data/`)
- `model/`: Room entities (e.g., `ImportantDayEntity`)
- `local/`: Room database, DAOs, and type converters
- `repository/`: Repository implementations

**Domain Layer** (`app/src/main/java/com/example/importantdays/domain/`)
- `model/`: Domain models (business logic entities)
- `repository/`: Repository interfaces
- `usecase/`: Use cases for business operations (e.g., `GetUpcomingDaysUseCase`, `ToggleFavoriteUseCase`)

**Presentation Layer** (`app/src/main/java/com/example/importantdays/presentation/`)
- `home/`: Home screen with upcoming days
- `favorites/`: Favorites screen
- `detail/`: Detail view for a specific day
- `addedit/`: Add/Edit screen for creating or modifying days
- `profile/`: Profile screen
- `components/`: Reusable UI components

**Other Key Directories**
- `di/`: Manual dependency injection via `AppContainer` (no Dagger/Hilt)
- `navigation/`: Navigation graph and screen definitions
- `util/`: Utility classes (mappers, date utils, extensions)

### Dependency Injection

This project uses **manual dependency injection** through `AppContainer` (not Dagger/Hilt):
- `AppContainer` is initialized in `ImportantDaysApplication.onCreate()`
- Access the container via `(application as ImportantDaysApplication).container`
- The container provides repository and use case instances
- ViewModels receive dependencies through constructor injection from the container

### ViewModel Pattern

ViewModels follow a consistent pattern:
- Use `StateFlow` for UI state management
- Define sealed classes/objects for UI states (Loading, Empty, Success, Error)
- Use cases are injected via constructor
- All data operations happen in `viewModelScope`

Example: `HomeViewModel` uses `HomeUiState` sealed class with Loading, Empty, and Success states.

### Navigation

- Uses Jetpack Navigation Compose
- `Screen` sealed class defines all routes in `navigation/Screen.kt`
- `NavGraph` in `navigation/NavGraph.kt` sets up all navigation destinations
- Main screens (Home, Favorites, Profile) use adaptive bottom navigation via `NavigationSuiteScaffold`
- Detail and AddEdit screens hide the bottom bar

### Database

- **Room** database with single entity: `ImportantDayEntity`
- Database name: `important_days_database`
- Uses `Converters` for `LocalDate` and `List<String>` type conversion
- Singleton pattern via `ImportantDaysDatabase.getDatabase(context)`

### Data Models

**ImportantDayEntity** (Room entity):
- `id`: Auto-generated primary key
- `title`, `description`: Basic info
- `date`: LocalDate for the important day
- `dayType`: Enum (ONE_TIME or YEARLY_REPEAT)
- `isFavorite`: Boolean flag
- `reminderEnabled`, `reminderDaysBefore`: Reminder settings
- `notificationChannels`: List of notification channels
- `createdAt`, `updatedAt`: Timestamps

**Domain Model Mapping**:
- Entity-to-domain mapping happens in `util/Mappers.kt`
- Domain models live in `domain/model/`
- Keep entities and domain models separate for clean architecture

## Key Technologies

- **Language**: Kotlin 2.0.21
- **UI**: Jetpack Compose with Material3
- **Database**: Room 2.6.1
- **Navigation**: Navigation Compose 2.8.5
- **Async**: Kotlin Coroutines + Flow
- **Build**: Gradle with Kotlin DSL, KSP for annotation processing
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36

## Development Guidelines

### Adding New Features

1. **Create domain model** in `domain/model/` if needed
2. **Add use case** in `domain/usecase/` for business logic
3. **Update repository** interface and implementation if data access is needed
4. **Create ViewModel** with StateFlow-based UI state
5. **Build Composable UI** in appropriate presentation package
6. **Wire up navigation** in `NavGraph.kt` and `Screen.kt`
7. **Update AppContainer** to provide new dependencies

### Database Migrations

When modifying `ImportantDayEntity`:
- Increment version number in `@Database` annotation
- Provide migration strategy or use `fallbackToDestructiveMigration()` for development
- Test migration thoroughly before release

### ViewModel Creation

ViewModels are manually instantiated in Composables using the AppContainer:
```kotlin
val application = LocalContext.current.applicationContext as ImportantDaysApplication
val viewModel = remember {
    HomeViewModel(
        getUpcomingDaysUseCase = application.container.getUpcomingDaysUseCase,
        toggleFavoriteUseCase = application.container.toggleFavoriteUseCase
    )
}
```

### Testing

- Unit tests: `app/src/test/java/`
- Instrumented tests: `app/src/androidTest/java/`
- Run specific test: `./gradlew test --tests "com.example.importantdays.ExampleUnitTest"`

## Important Notes

### Dependency Management

Dependencies are managed via Gradle version catalog in `gradle/libs.versions.toml`. When adding new dependencies:
1. Add version to `[versions]` section
2. Add library to `[libraries]` section
3. Reference in `build.gradle.kts` using `libs.` prefix

### WorkManager Integration

The project includes WorkManager for background reminder notifications. When working with reminders, coordinate with WorkManager scheduling logic.

### Compose Material3

This project uses Material3 (not Material2). Use Material3 components and theming APIs throughout the UI.
