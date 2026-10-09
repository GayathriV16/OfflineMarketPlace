# Engineering Standards — OfflineMarketPlace

## 1. Purpose

This document defines the coding conventions, architectural principles, data-handling rules, and quality expectations for the OfflineMarketPlace Android application.

## 2. General Coding Standards

* Use Kotlin for application code.
* Follow consistent Kotlin naming conventions:

  * Classes and objects: `PascalCase`
  * Functions and properties: `camelCase`
  * Constants: `UPPER_SNAKE_CASE` where appropriate
* Prefer immutable values with `val`.
* Keep functions focused on a single responsibility.
* Avoid duplicated business logic.
* Use meaningful names and add comments for non-obvious decisions.
* Remove unused imports, obsolete code, and debug statements before submission.

## 3. Architecture and Separation of Concerns

### Presentation Layer

* Compose screens are responsible for rendering UI and forwarding user actions.
* ViewModels manage presentation state and coordinate use cases.
* `BrowseViewModel` handles browsing, favorites, and browse-related synchronization state.
* `ListingFormViewModel` handles listing creation and editing.
* Use a consolidated `BrowseUiState` when multiple values are needed by the browse screen.
* Keep database and network implementation details out of composables.

### Domain Layer

* Use cases represent individual application actions.
* Use cases should not depend on Android UI components.
* Keep business rules in the appropriate domain or data component rather than duplicating them in screens.

### Data Layer

* `ListingRepository` coordinates local listing operations and pending sync records.
* DAOs are responsible for Room queries.
* `ListingApi` abstracts remote operations.
* `MockListingApi` provides the current in-memory API implementation.

## 4. Dependency Injection

* Use Hilt for dependency management.
* Use constructor injection where appropriate.
* Register database, DAO, repository, and API dependencies through Hilt modules or injectable constructors.
* Use singleton scope for application-wide shared resources such as the Room database.
* Avoid manually constructing dependencies inside ViewModels.

## 5. UI and Resource Management

* Store user-facing strings in `strings.xml`.
* Store reusable dimensions in resource files.
* Store reusable colors in resource definitions.
* Use Compose navigation to manage screen transitions.
* Provide Compose previews for reusable components and screens where practical.
* Handle loading, empty, success, and error conditions explicitly.
* Keep composables focused on UI rendering.

## 6. Null and Error Handling

* Use nullable types only when a value can legitimately be absent.
* Handle nullable image URLs and optional values safely.
* Avoid force-unwrapping nullable values unless validity is guaranteed.
* Handle expected exceptions at an appropriate boundary.
* Re-throw `CancellationException` when catching broad exceptions in coroutine code.
* Do not silently swallow errors that affect user-visible operations.
* Keep network failures distinguishable from successful synchronization.

## 7. Reactive State Management

* Use Flow for ongoing data streams from Room.
* Use StateFlow for observable ViewModel state.
* Collect UI state using lifecycle-aware collection.
* Avoid exposing mutable state flows directly to the UI.
* Use a data class when multiple UI values can coexist.
* Use sealed classes or enums when representing mutually exclusive states or a fixed set of operation types, as appropriate.

## 8. Database Standards

* Use Room for persistent local storage.
* Keep database access inside DAOs and repositories.
* Use suspend functions for one-time database operations and Flow for observable queries.
* Use transactions when multiple local database changes must succeed or fail together.
* Keep database schema versions and migration strategies consistent.
* Avoid destructive migrations when local user data must be preserved.
* Consider exporting Room schemas to support migration review.

## 9. Offline Synchronization Standards

* Save offline changes locally before attempting remote synchronization.
* Record pending operations persistently in Room.
* Represent supported operation types with `SyncOperationType`.
* Process operations in a deterministic order.
* Preserve a pending CREATE operation when the same unsynced listing is edited or favorited.
* Avoid overwriting a newer local edit with a stale server response.
* Remove a pending operation only when the corresponding synchronization outcome has been handled safely.
* Propagate network failures so the worker can apply its configured retry policy.
* Prevent overlapping synchronization runs where appropriate.
* Design retries to be safe if the server succeeds but the client does not receive or persist the response.
* Use server-side idempotency keys or equivalent guarantees in a production API.
* Define a policy for pending operations whose local listing no longer exists.

## 10. Performance and Resource Usage

* Use lazy Compose layouts for long listing collections.
* Avoid unnecessary recompositions and repeated network calls.
* Keep expensive work off the main thread.
* Avoid loading large datasets unnecessarily.
* Measure memory and CPU usage with profiling tools instead of relying on assumptions.
* Consider pagination and image-loading optimizations as the dataset grows.

## 11. Testing and Validation

Before submitting a change, verify:

* The project builds successfully.
* Listing creation and editing work.
* Favorite changes update the UI.
* Offline changes persist after the app is restarted.
* Pending operations are synchronized after connectivity returns.
* Network failures do not crash the application.
* A newer local edit is not overwritten by a stale sync response.
* Repeated sync requests do not produce unintended duplicate effects.
* Loading and synchronization states are displayed correctly.

Add automated tests for use cases, repository behavior, DAO operations, and synchronization edge cases where practical.

## 12. Code Review Checklist

* [ ] Does the change have a clear responsibility?
* [ ] Are dependencies injected appropriately?
* [ ] Are nullable values and errors handled safely?
* [ ] Are user-facing strings and dimensions stored in resources?
* [ ] Is UI state exposed immutably?
* [ ] Are related database changes transactional?
* [ ] Can synchronization retries safely repeat operations?
* [ ] Are local edits protected from stale server responses?
* [ ] Have relevant tests and build checks passed?
* [ ] Has documentation been updated when behavior or architecture changes?

## 13. Known Limitations

The current implementation uses an in-memory `MockListingApi`. Server persistence and production-grade idempotency therefore depend on replacing the mock with a real backend.

The local repository operations and sync-queue changes should be reviewed for transactional consistency. Database migrations, worker retry behavior, and concurrency guarantees should also be verified before production use.
