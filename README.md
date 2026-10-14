# OfflineMarketPlace

An offline-first Android marketplace application built with Kotlin and Jetpack Compose. The application allows users to browse marketplace listings, create and edit listings, manage favorites, and synchronize pending changes when network connectivity becomes available.

## Features

- **Browse listings:** View marketplace items in a two-column grid.
- **Create listings:** Create new listings while offline.
- **Edit listings:** Update existing listing details.
- **Favorite listings:** Mark and unmark listings as favorites.
- **Offline-first storage:** Persist listings locally using Room.
- **Pending operation queue:** Store offline changes for later synchronization.
- **Automatic background synchronization:** Schedule synchronization using WorkManager when network constraints are satisfied.
- **Synchronization status:** Display whether changes are pending, syncing, completed, or failed.
- **Conflict protection:** Avoid overwriting newer local changes with an outdated server response.
- **Image attachments:** Support attaching images to listings using available image-selection and camera functionality.
- **Reactive UI:** Observe local data and UI state using Kotlin Flow and StateFlow.
- **Screen-specific ViewModels:** Separate presentation logic and state according to screen responsibilities.
- **Navigation:** Navigate between listing, details, create, and edit screens using Navigation Compose.
- **Dependency injection:** Use Hilt to provide dependencies.
- **Resource management:** Keep user-facing strings in Android string resources.
- **Reusable UI:** Use reusable Compose components and previews to support maintainability.

## Technology Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | Declarative UI development |
| Material 3 | UI components and styling |
| Room | Local database persistence |
| Kotlin Coroutines | Asynchronous operations |
| Flow and StateFlow | Reactive data and UI state |
| Hilt | Dependency injection |
| Navigation Compose | Screen navigation |
| WorkManager | Constraint-based background synchronization |
| MockListingApi | Simulated remote API for development and demonstration |
| Gradle | Build and dependency management |

## Architecture

The application follows a layered architecture that separates presentation, business logic, and data access. Dependencies are provided through Hilt to reduce tight coupling and improve testability.

### Presentation layer

- Jetpack Compose screens and reusable UI components.
- Navigation Compose for screen transitions.
- Separate ViewModels for browsing, listing details, and listing forms.
- UI state models to represent loading, content, errors, and form operations.
- Lifecycle-aware state collection.

### Domain layer

- Domain models that represent marketplace listings independently of database entities.
- Use cases that encapsulate individual business operations.
- Repository interfaces that define contracts between business logic and the data layer.

### Data layer

- Repository implementations that coordinate local and remote data operations.
- `LocalListingDataSource` to abstract local listing and synchronization-queue operations.
- `RoomLocalListingDataSource` to implement local data access using Room and DAOs.
- `ListingApi` to abstract remote listing operations.
- `MockListingApi` to simulate a remote API during development.

### Synchronization layer

- `SyncRepositoryImpl` processes pending create and update operations.
- Room persists pending synchronization operations.
- WorkManager schedules background work subject to network constraints.
- Conditional database updates protect local changes made while a server request is in progress.
- Hilt supplies the dependencies required by repositories, data sources, and ViewModels.

## Offline-first data flow

Room acts as the local source of listing data displayed by the application. User changes are saved locally so that browsing and listing management can continue without an active network connection.

### Creating or editing a listing

1. The user creates or edits a listing through the Compose UI.
2. The corresponding ViewModel invokes the appropriate use case.
3. The repository saves the change locally.
4. The listing and its pending synchronization operation are recorded in Room.
5. The UI observes the updated local data through Flow.
6. Synchronization is scheduled for execution when the required network constraint is satisfied.

### Synchronization flow

1. WorkManager starts the synchronization worker when its constraints are met.
2. The worker invokes the synchronization use case.
3. The synchronization repository retrieves pending operations from Room.
4. The repository loads the latest local listing for each operation.
5. The appropriate API operation is called for the pending create or update.
6. The server response is applied only if the local listing has not changed since the request was prepared.
7. The completed queue operation is removed only when the conditional local update succeeds.
8. If synchronization fails or a local change is detected during the request, the operation remains pending or is retried according to the worker's error-handling policy.

## Conflict handling

The application uses a conditional-update strategy to protect local changes during synchronization.

Before applying a server response, the repository checks whether the listing's local update timestamp still matches the timestamp associated with the request. If the listing has changed in the meantime, the response is not allowed to overwrite the newer local data.

The listing update and removal of its completed synchronization operation are performed transactionally, so the queue entry is not removed when the conditional update fails.

This approach protects against a common synchronization race condition. A production implementation would also need a clearly defined server-side conflict-resolution policy and retry-safe API operations.

## Project structure

The project is organized by responsibility. Exact file names may vary as the implementation evolves.

```text
com.example.offlinemarketplace
│
├── data
│   ├── local
│   │   ├── AppDatabase
│   │   ├── ListingDao
│   │   ├── ListingEntity
│   │   ├── SyncOperationDao
│   │   ├── SyncOperationEntity
│   │   ├── LocalListingDataSource
│   │   └── RoomLocalListingDataSource
│   │
│   ├── remote
│   │   ├── ListingApi
│   │   └── MockListingApi
│   │
│   └── repository
│       ├── ListingRepositoryImpl
│       └── SyncRepositoryImpl
│
├── domain
│   ├── model
│   │   └── Listing
│   ├── repository
│   │   ├── ListingRepository
│   │   └── SyncRepository
│   └── usecase
│
├── sync
│   ├── SyncOperationType
│   ├── SyncScheduler
│   ├── SyncManager
│   └── SyncWorker
│
├── ui
│   ├── browse
│   ├── details
│   ├── form
│   └── components
│
├── di
│
└── OfflineMarketPlaceApp
```

## Memory and CPU considerations

- **Lazy rendering:** Use `LazyVerticalGrid` so listing items are composed lazily instead of composing the entire collection at once.
- **Reactive local data:** Observe Room queries through Flow to keep the UI updated without repeatedly loading data on the main thread.
- **Lifecycle-aware collection:** Collect UI state using lifecycle-aware APIs to avoid unnecessary collection when screens are not active.
- **Asynchronous operations:** Perform database and network operations in suspend functions and coroutines rather than blocking the main thread.
- **Background constraints:** Use WorkManager constraints to avoid unnecessary synchronization attempts when the network is unavailable.
- **Persistent queue:** Store pending operations in Room so they survive process recreation.
- **Controlled recomposition:** Keep UI state scoped to the relevant screen and avoid unnecessary state changes and large collection copies.
- **Image handling:** Avoid retaining unnecessarily large image bitmaps in memory; use appropriately sized images and efficient image-loading practices where applicable.
- **Scalability:** Consider pagination and database query optimization if the listing collection grows significantly.
- **Performance validation:** Profile memory consumption, CPU usage, and frame rendering on representative devices before making quantitative performance claims.

## Project setup

### Prerequisites

- Android Studio.
- A compatible JDK and Android SDK configured for the project.
- Git, if cloning the repository.
- An Android emulator or physical Android device.

### Clone the repository

```bash
git clone https://github.com/GayathriV16/OfflineMarketPlace.git
cd OfflineMarketPlace
```

Open the project in Android Studio and allow Gradle synchronization to complete.

### Build the application

On Windows, execute the following command from the project root:

```powershell
.\gradlew.bat assembleDebug
```

To install the debug build on a connected device:

```powershell
.\gradlew.bat installDebug
```

### Run the application

1. Open the project in Android Studio.
2. Wait for Gradle synchronization to complete.
3. Select an emulator or connected Android device.
4. Run the application.
5. Test listing browsing, details, creation, editing, favorites, and synchronization with network connectivity disabled and restored.

## Testing and verification

The primary manual verification scenarios are:

- Browse marketplace listings.
- Open listing details.
- Create a listing while online.
- Create a listing while offline and verify that it appears locally.
- Edit a listing and verify the updated local data.
- Toggle a favorite while offline.
- Restore network connectivity and verify pending changes synchronize.
- Confirm that the pending-operation count and sync status update appropriately.
- Verify that a newer local change is not overwritten by an older server response.
- Check screen navigation and form validation.

Run available automated tests with:

```powershell
.\gradlew.bat test
```

To run Android instrumented tests, if configured:

```powershell
.\gradlew.bat connectedAndroidTest
```

Automated test coverage should include use cases, repository behavior, operation queuing, synchronization success and failure, and conflict protection.

## Current API implementation

The project uses `MockListingApi`, an in-memory implementation of `ListingApi`, to simulate remote listing operations without requiring a production backend.

This is suitable for demonstrating the offline-first workflow and exercising synchronization logic. Because the mock implementation keeps its remote state in memory, it does not provide the durability or multi-device behavior of a real server.

A production implementation should provide a persistent backend, authentication where required, retry-safe create operations, appropriate error handling, and a defined conflict-resolution policy.

## Engineering standards

The implementation aims to follow these principles:

- **Separation of concerns:** Keep UI, business logic, persistence, and remote API responsibilities separate.
- **Dependency inversion:** Depend on repository and data-source interfaces where appropriate.
- **Single responsibility:** Keep ViewModels and use cases focused on their respective screen or business operation.
- **Dependency injection:** Use Hilt instead of manually constructing application dependencies throughout the code.
- **Resource management:** Keep user-facing strings in Android resources.
- **Null safety:** Use Kotlin nullability explicitly and handle missing data appropriately.
- **State management:** Expose screen state through observable state holders and collect it with lifecycle awareness.
- **Database consistency:** Use Room transactions for related local changes and synchronization-queue operations.
- **Error handling:** Handle expected failures and preserve pending operations when synchronization cannot safely complete.
- **Maintainability:** Prefer clear names, reusable components, and focused functions.
- **Testing:** Add automated tests for business logic and synchronization behavior.

See [ENGINEERING_STANDARDS.md](ENGINEERING_STANDARDS.md) for additional project guidelines.

## Future improvements

- Expand automated unit and integration test coverage for repositories and synchronization.
- Add explicit database migration strategies and schema export.
- Implement server-side idempotency for retry-safe listing creation.
- Improve conflict resolution for concurrent edits across devices.
- Add pagination and profile performance with larger listing collections.
- Replace the in-memory mock API with a persistent backend for production use.
- Add more comprehensive automated UI tests for navigation and offline workflows.

## Repository

GitHub: https://github.com/GayathriV16/OfflineMarketPlace

## License

No license has been specified for this repository. Add a license if the project is intended for public reuse.