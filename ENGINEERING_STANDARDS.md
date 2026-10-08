# Engineering Standards

## 1. Architecture and Code Organization

* Follow the MVVM architecture pattern.
* Keep UI, ViewModel, Repository, local data, remote data, and synchronization responsibilities separated.
* UI components should not access Room or the API directly.
* Use the Repository layer as the single entry point for application data operations.
* Keep reusable UI components in the `ui/components` package.
* Organize files by feature and responsibility.

## 2. Kotlin Coding Standards

* Use Kotlin for all application code.
* Follow Kotlin naming conventions:

    * Classes and interfaces: PascalCase
    * Functions and variables: camelCase
    * Constants: UPPER_SNAKE_CASE where appropriate
* Prefer immutable data using `val`.
* Use data classes for models and entities where appropriate.
* Avoid unnecessary nullable types.
* Use meaningful names instead of abbreviations.
* Keep functions small and focused on a single responsibility.

## 3. Jetpack Compose Standards

* Use Jetpack Compose for UI development.
* Keep composable functions focused and reusable.
* Hoist state to the appropriate parent/ViewModel.
* Use `LazyColumn` or `LazyVerticalGrid` for large collections.
* Provide stable keys for listing items.
* Avoid performing database or network operations directly inside composables.
* Use `StateFlow` and lifecycle-aware collection for UI state.

## 4. Local Data and Offline-First Design

* Room is the local source of truth for marketplace listings.
* Listing data must remain available when the device is offline.
* Offline creates and updates are stored locally.
* Pending operations are stored in a dedicated synchronization queue.
* Repeated edits to the same listing are coalesced where possible to avoid unnecessary sync operations.

## 5. Synchronization Standards

* Synchronization must run through the dedicated SyncManager.
* WorkManager is used for background synchronization.
* Network connectivity is required before executing background synchronization.
* Failed synchronization operations should be retried.
* Server responses are persisted back into Room after synchronization.
* Last-write-wins is used as the conflict-resolution strategy.
* Synchronization status should be visible to the user.

## 6. Networking Standards

* Keep remote API operations behind the `ListingApi` abstraction.
* Use clear API models separate from local database entities.
* Do not expose remote API implementation details to the UI layer.
* Handle network failures without losing locally stored user changes.
* Use a mock REST API for the assignment demonstration.

## 7. Image Handling Standards

* Use Android Photo Picker for selecting images.
* Use the camera for taking listing photos.
* Store selected/captured images in application-controlled storage.
* Use Coil for asynchronous image loading.
* Display thumbnails in listing cards to reduce memory usage.
* Avoid loading unnecessarily large images into memory.

## 8. Input Validation

* Validate required fields before creating or updating a listing.
* Validate that the title and description are not empty.
* Validate that the price is a valid positive number.
* Display clear validation messages to users.
* Do not allow invalid data to enter the local database.

## 9. Security Standards

* Do not hard-code secrets, passwords, or authentication tokens in source code.
* Sensitive credentials should use secure Android storage such as Android Keystore-backed mechanisms.
* Request only the permissions required by the application.
* Do not log sensitive information.
* Use HTTPS for production network communication.

## 10. Performance Standards

* The application should remain responsive with approximately 200 listings.
* Use lazy Compose components for large lists and grids.
* Use stable item keys to improve Compose performance.
* Use asynchronous image loading and caching.
* Avoid unnecessary database queries and recompositions.
* Perform synchronization and database operations away from the main UI thread.

## 11. Testing Standards

* Write unit tests for synchronization and conflict-resolution logic.
* Test both successful and conflicting update scenarios.
* Test creation of listings through the mock API.
* Keep tests deterministic and independent.
* Run the test suite before submitting changes.

## 12. Build and Code Quality

* Keep the project compiling successfully before submission.
* Run unit tests after significant changes.
* Use Android Studio inspection/lint tools to identify potential issues.
* Avoid unused imports, dead code, and unnecessary dependencies.
* Keep Gradle dependencies and versions consistent.

## 13. Version Control Standards

* Use meaningful commit messages.
* Keep commits focused on a single logical change.
* Do not commit generated build files, local IDE configuration, or sensitive credentials.
* Maintain a clean repository structure.
* Update the README when major architectural or functional changes are introduced.

## 14. Documentation Standards

* Maintain a README describing setup, architecture, offline behavior, synchronization, and testing.
* Include architecture and sequence diagrams with the project submission.
* Document important design decisions and performance considerations.
* Keep documentation consistent with the current implementation.

## 15. Review Checklist

Before submission:

* [ ] Application builds successfully.
* [ ] Unit tests pass.
* [ ] Offline listing creation works.
* [ ] Offline listing editing works.
* [ ] Favorites work.
* [ ] Images can be selected or captured.
* [ ] Automatic synchronization works when connectivity returns.
* [ ] 200 listings can be browsed smoothly.
* [ ] Architecture diagram is included.
* [ ] Sequence diagram is included.
* [ ] README is included.
* [ ] Engineering standards are included.
* [ ] Demo video is recorded.
