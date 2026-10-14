# Engineering Standards — OfflineMarketPlace

## 1. Purpose

This document defines the engineering principles and coding standards followed in the OfflineMarketPlace Android application. The goal is to maintain readable, testable, maintainable, and reliable code while supporting offline-first functionality.

## 2. Technology Standards

- **Language:** Kotlin
- **UI:** Jetpack Compose and Material 3
- **Architecture:** Layered architecture with separation of concerns
- **Dependency Injection:** Hilt
- **Local Persistence:** Room
- **Asynchronous Processing:** Kotlin Coroutines and Flow
- **Navigation:** Navigation Compose
- **Background Synchronization:** WorkManager
- **Testing:** JUnit and appropriate coroutine-testing and mocking libraries

## 3. Architecture and Separation of Concerns

The application separates presentation, domain, and data responsibilities.

### Presentation Layer

- Use Jetpack Compose for UI components and screens.
- Use screen-specific ViewModels to manage screen-related state and events.
- Keep business logic out of composable functions.
- Collect observable state using lifecycle-aware APIs.
- Use reusable composables where appropriate.
- Provide Compose previews for reusable components and important screens.

### Domain Layer

- Keep business rules in use cases rather than UI components.
- Use domain models independently of database entities and remote API models.
- Define repository interfaces for operations required by the domain.
- Keep use cases focused on a single business responsibility.

### Data Layer

- Implement repository interfaces in the data layer.
- Separate local persistence from remote API access.
- Use data-source interfaces to reduce direct dependencies on database implementation details.
- Map database entities and API models to domain models at the appropriate boundaries.
- Avoid exposing Room entities directly to the UI.

## 4. Dependency Injection

- Use Hilt to provide dependencies.
- Prefer constructor injection for classes with dependencies.
- Use Hilt modules to bind interfaces to their implementations and provide external dependencies.
- Avoid manually constructing repositories, data sources, and ViewModels throughout the application.
- Keep dependency configuration centralized and easy to maintain.

## 5. Kotlin Coding Standards

- Follow Kotlin naming conventions.
- Use `PascalCase` for classes, interfaces, and enums.
- Use `camelCase` for properties and functions.
- Use descriptive names that communicate intent.
- Prefer immutable variables with `val` over `var` whenever possible.
- Use data classes for appropriate data-holding models.
- Avoid unnecessary code duplication.
- Keep functions small and focused.
- Avoid deeply nested conditional logic.
- Use enums or sealed classes for fixed operation types and state representations instead of repeated string literals where appropriate.

## 6. Null Safety and Error Handling

- Use Kotlin's nullable types to represent values that may be absent.
- Avoid the not-null assertion operator (`!!`) unless