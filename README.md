# OfflineMarketPlace

A native Android marketplace application built with Kotlin and Jetpack Compose.

The application is designed as an offline-first marketplace where users can browse listings, create and edit listings, mark favorites, and synchronize changes when network connectivity is available.

## Features

- Browse marketplace listings
- 200 sample listings for performance testing
- Offline-first local persistence
- Create listings while offline
- Edit listings while offline
- Favorite/unfavorite listings
- Camera support for listing images
- Android Photo Picker support
- Local image persistence
- Image caching and thumbnail-sized requests using Coil
- Background synchronization using WorkManager
- Manual "Sync Now" option
- Pending sync operation count
- Last-write-wins conflict resolution
- Input validation
- Loading progress indicator
- Unit tests for synchronization conflict handling

## Architecture

The application follows a layered architecture:

UI
↓
ViewModel
↓
Repository
↓
Room Database
↓
Mock REST API

Background synchronization is handled separately:

WorkManager
↓
SyncWorker
↓
SyncManager
↓
Room + Mock REST API

### Main layers

#### UI

Jetpack Compose screens:

- BrowseScreen
- DetailsScreen
- CreateListingScreen
- ListingCard

#### ViewModel

`BrowseViewModel` manages:

- UI state
- Listing state
- Favorites
- Create/update operations
- Sync status
- Loading state

#### Repository

`ListingRepository` provides a single data-access layer between the UI/ViewModel and local/remote sources.

#### Local database

Room is used for complete local persistence.

Tables:

- `listings`
- `sync_operations`

The Room database acts as the local source of truth for the UI.

#### Remote API

`ListingApi` defines the REST-style API contract.

`MockListingApi` provides an in-memory mock backend for:

- Fetching listings
- Creating listings
- Updating listings

## Offline-First Behavior

The application does not require an active network connection for normal marketplace operations.

When the user creates or edits a listing:

1. The change is immediately written to Room.
2. A sync operation is added to the local sync queue.
3. The UI immediately reflects the local change.
4. WorkManager schedules synchronization.
5. Synchronization runs when network connectivity is available.
6. After successful synchronization, the pending operation is removed.

This allows users to continue working even when offline.

## Synchronization

Synchronization is implemented using:

- `SyncManager`
- `SyncWorker`
- `SyncScheduler`
- Room sync-operation queue
- WorkManager network constraints

WorkManager requires a connected network before executing queued synchronization work.

The application also checks for pending operations when it starts and schedules synchronization when necessary.

## Conflict Resolution

The application uses a **last-write-wins** strategy.

Each listing contains an `updatedAt` timestamp.

When an update reaches the mock server:

- If the incoming timestamp is newer than or equal to the server timestamp, the incoming version wins.
- If the incoming timestamp is older, the existing server version is retained.

The server result is then written back to the local Room database.

This ensures that the local database reflects the final server version after synchronization.

## Image Handling

Users can attach images using:

- Android Photo Picker
- Camera

Selected images are copied into application-private storage.

This allows images to remain available after the original picker URI is no longer accessible.

Coil is used for image loading and caching.

Listing thumbnails use a constrained image request size of approximately `240 x 240` pixels to reduce unnecessary memory usage.

The listing cards display images at a fixed height to avoid excessive layout and memory costs.

## Memory and CPU Considerations

The application is designed to handle a larger number of marketplace listings efficiently, including a test dataset of 200 listings.

### Memory Optimization

* Uses `LazyVerticalGrid` so listing items are composed only as they become visible instead of loading all UI elements at once.
* Uses Coil for asynchronous image loading and caching.
* Listing images are displayed as thumbnails rather than loading large full-resolution images into the UI.
* Images selected from the camera or photo picker are copied to the application's local storage and referenced by URI/path instead of keeping bitmap objects in memory.
* Room provides persistent local storage, avoiding the need to keep the complete dataset in memory.
* `StateFlow` is used to expose reactive UI state without unnecessary duplication of data.

### CPU Optimization

* Database operations are performed using Kotlin coroutines and suspend functions so they do not block the main UI thread.
* Network and synchronization operations run in background workers using WorkManager.
* WorkManager uses network constraints so synchronization is performed when a network connection is available.
* The sync queue processes only pending create/update operations instead of repeatedly uploading the complete listing dataset.
* `LazyVerticalGrid` reduces unnecessary UI composition and rendering work when scrolling through large datasets.

### Performance Validation

* The application was tested with 200 marketplace listings.
* The listing grid remained responsive during scrolling.
* Offline creation and editing are persisted locally using Room.
* Background synchronization is handled by WorkManager to keep the UI responsive.
* Image loading is performed asynchronously to avoid blocking the main thread.

These approaches help keep memory usage controlled, reduce unnecessary CPU work, and maintain a responsive user experience as the number of listings increases.


## Performance

The application contains 200 listings for performance testing.

`LazyVerticalGrid` is used instead of loading all listing views at once.

Additional performance considerations include:

- Lazy loading of listing cards
- Stable item keys
- Fixed image dimensions
- Coil image caching
- Thumbnail-sized image requests
- Local Room persistence
- Avoiding unnecessary large bitmap allocations

The application was tested with 200 listings and scrolling remained smooth after image-size optimization.

## Security

The current backend is a mock REST API and does not require authentication tokens.

No real credentials are stored in the application.

For a production backend with authentication, API tokens should be stored using Android Keystore-backed secure storage rather than plain-text storage.

## Input Validation

Listing creation and editing validate:

- Title must not be empty
- Category must not be empty
- Description must not be empty
- Price must be a valid non-negative number

Validation occurs before the listing is saved.

## Testing

Unit tests are included for synchronization conflict resolution.

Current tests verify:

1. A newer update is accepted.
2. An older update does not overwrite a newer server version.

The tests are located under:

`app/src/test`

## Build and Technology Stack

- Kotlin
- Jetpack Compose
- Material 3
- Room
- WorkManager
- Coil
- Retrofit API abstraction
- Kotlin Coroutines
- JUnit

## Architecture Diagram

![Architecture Diagram](docs/architecture-diagram.png)

## Sequence Diagram

![Sequence Diagram](docs/sequence-diagram.png)

## Project Structure

```text
com.example.offlinemarketplace
│
├── AppContainer.kt
├── MainActivity.kt
│
├── data
│   ├── local
│   │   ├── AppDatabase.kt
│   │   ├── DatabaseProvider.kt
│   │   ├── ListingDao.kt
│   │   ├── ListingEntity.kt
│   │   ├── SyncOperationDao.kt
│   │   └── SyncOperationEntity.kt
│   │
│   ├── model
│   │   └── Listing.kt
│   │
│   ├── remote
│   │   ├── ApiModels.kt
│   │   ├── ListingApi.kt
│   │   └── MockListingApi.kt
│   │
│   └── repository
│       └── ListingRepository.kt
│
├── sync
│   ├── SyncManager.kt
│   ├── SyncScheduler.kt
│   └── SyncWorker.kt
│
├── ui
│   ├── browse
│   │   └── BrowseScreen.kt
│   ├── components
│   │   └── ListingCard.kt
│   ├── create
│   │   └── CreateListingScreen.kt
│   ├── details
│   │   └── DetailsScreen.kt
│   └── theme
│
└── viewmodel
    └── BrowseViewModel.kt